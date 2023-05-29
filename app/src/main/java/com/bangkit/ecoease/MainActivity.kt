package com.bangkit.ecoease

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bangkit.ecoease.CameraActivity.Companion.CAMERA_X_RESULT
import com.bangkit.ecoease.config.ViewModelFactory
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.model.ImageCaptured
import com.bangkit.ecoease.data.viewmodel.*
import com.bangkit.ecoease.di.Injection
import com.bangkit.ecoease.ui.common.UiState
import com.bangkit.ecoease.ui.component.*
import com.bangkit.ecoease.ui.screen.*
import com.bangkit.ecoease.ui.screen.chat.ChatRoomScreen
import com.bangkit.ecoease.ui.screen.chat.UsersChatsScreen
import com.bangkit.ecoease.ui.screen.onboard.OnBoardingScreen
import com.bangkit.ecoease.ui.screen.order.DetailOrderScreen
import com.bangkit.ecoease.ui.screen.order.OrderHistoryScreen
import com.bangkit.ecoease.ui.screen.order.OrderScreen
import com.bangkit.ecoease.ui.screen.order.OrderSuccessScreen
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


val listMainRoute = listOf(
    Screen.Home,
    Screen.History,
    Screen.Map,
    Screen.UsersChats
)
val listNoTopBar = listOf(
    Screen.Onboard,
    Screen.Auth,
    Screen.Register,
    Screen.OrderSuccess
)
class MainActivity : ComponentActivity() {
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private lateinit var cameraViewModel: CameraViewModel


    companion object{
        val INTENT_GALLERY_RESULT = 201
    }

    // TODO: move all business logic in viewmodel
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashViewModel = ViewModelFactory(Injection.provideInjection(this)).create(SplashViewModel::class.java)
        super.onCreate(savedInstanceState)
        cameraViewModel = ViewModelFactory(Injection.provideInjection(this)).create(CameraViewModel::class.java)
        val orderViewModel = ViewModelFactory(Injection.provideInjection(this)).create(OrderViewModel::class.java)
        val garbageViewModel = ViewModelFactory(Injection.provideInjection(this)).create(GarbageViewModel::class.java)
        val addressViewModel = ViewModelFactory(Injection.provideInjection(this)).create(AddressViewModel::class.java)
        val authViewModel = ViewModelFactory(Injection.provideInjection(this)).create(AuthViewModel::class.java)
        val userViewModel = ViewModelFactory(Injection.provideInjection(this)).create(UserViewModel::class.java)
        val locationViewModel = ViewModelFactory(Injection.provideInjection(this)).create(LocationViewModel::class.java)

        installSplashScreen().setKeepOnScreenCondition{
            splashViewModel.isLoading.value
        }

        setContent {
            EcoEaseTheme {
                val navController: NavHostController = rememberNavController()
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                var openDialog by remember{ mutableStateOf(false) }
                val isReadOnboardNew by splashViewModel.isReadOnboard.collectAsState()
                val isLogged by splashViewModel.isLogged.collectAsState()

                fun resetOrder(){
                    orderViewModel.resetCurrentOrder()
                    navController.popBackStack()
                }

                val isTopBarShown = !listNoTopBar.map { it.route }.contains(currentRoute)
                val isMainRoute = listMainRoute.map { it.route }.contains(currentRoute)

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                    ,
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = { if(isTopBarShown){
                                TopBar(
                                    currentRoute = currentRoute,
                                    navController = navController,
                                    isUseNavButton = !isMainRoute,
                                    isUseAvatar = currentRoute == Screen.Home.route,
                                    onTapNavButton = {
                                        if(orderViewModel.orderState.value.total > 0 && currentRoute == Screen.Order.route){
                                            openDialog = true
                                        }else{
                                            navController.popBackStack()
                                        }
                                    },
                                    onTapAvatar = {
                                        navController.navigate(Screen.Profile.route)
                                    }
                                )
                            }
                        },
                        floatingActionButton = {
                            if(isMainRoute) FloatingButton(description = "scan", icon = Icons.Default.CameraEnhance, onClick = { navController.navigate(Screen.Scan.route) })
                        },
                        bottomBar = {
                            if(isMainRoute) BottomNavBar(navController = navController, items = listMainRoute)
                        },
                        floatingActionButtonPosition = FabPosition.Center,
                        isFloatingActionButtonDocked = true,
                    ) {paddingValues ->
                        // TODO: TESTING FOR EACH SCREEN 
                        DialogBox(text = "Apakah anda yakin ingin membatalkan order anda", onDissmiss = { openDialog = false }, onAccept = { resetOrder() }, isOpen = openDialog)
                        NavHost(
                            navController = navController,
                            startDestination =  if(isReadOnboardNew){
                                                    if(isLogged) Screen.Home.route else Screen.Auth.route
                                                } else Screen.Onboard.route, //Screen.OnBoard.route,
                            modifier = Modifier.padding(paddingValues)
                        ){
                            composable(Screen.Onboard.route){
                                OnBoardingScreen(navController = navController, onFinish = { splashViewModel.finishedOnBoard() })
                            }
                            composable(
                                route = Screen.Scan.route,
                                arguments = listOf(navArgument("path"){
                                    nullable = true
                                    defaultValue = null
                                    type = NavType.StringType
                                })
                            ){
                                val filePath = it.arguments?.getString("path") ?: ""
                                ScanScreen(
                                    navController = navController,
                                    filePath = filePath,
                                    imageCapturedState = cameraViewModel.uiStateImageCaptured,
                                    onLoadingImageState = { cameraViewModel.getImageUri() },
                                    openCamera = {
                                        val intent = Intent(this@MainActivity, CameraActivity::class.java)
                                        launcherIntentCameraX.launch(intent)
                                    },
                                    openGallery = {
                                        val intent = Intent().apply {
                                            action = ACTION_GET_CONTENT
                                            type = "image/*"
                                        }
                                        val chooser = Intent.createChooser(intent, "Choose a picture")
                                        launcherIntentGallery.launch(chooser)
                                    }
                                )
                            }
                            composable(Screen.Home.route){
                                DashboardScreen(
                                    navHostController = navController,
                                    garbageStateFlow = garbageViewModel.garbageState,
                                    onLoadGarbage = { garbageViewModel.getAllGarbage() },
                                    onReloadGarbage = { garbageViewModel.reloadGarbage() },
                                )
                            }
                            composable(Screen.History.route){
                                OrderHistoryScreen(
                                    orderHistoryState = orderViewModel.orderHistoryState,
                                    loadOrderHistory = { orderViewModel.loadOrderHistory() },
                                    reloadOrderHistory = { orderViewModel.reloadOrderHistory() },
                                    navHostController = navController
                                )
                            }
                            composable(Screen.Profile.route){
                                ProfileScreen(
                                    navHostController = navController,
                                    userStateFlow = userViewModel.user,
                                    onLoadUser = { userViewModel.getUser() },
                                    onReloadUser = { userViewModel.reloadUser() },
                                    logoutAction = { authViewModel.logout() }
                                )
                            }
                            composable(Screen.Map.route){
                                MapScreen(
                                    availableOrderStateFlow = orderViewModel.availableOrders,
                                    loadAvailableOrders = { orderViewModel.loadAvailableOrder() }
                                )
                            }
                            composable(Screen.Auth.route){
                                AuthScreen(navHostController = navController, loginAction = { authViewModel.login() })
                            }
                            composable(Screen.Register.route){
                                RegisterScreen(navHostController = navController)
                            }
                            composable(Screen.Order.route){
                                OrderScreen(
                                    navHostController = navController,
                                    orderStateFlow = orderViewModel.orderState,
                                    selectedAddressStateFlow = addressViewModel.selectedAddress,
                                    lastLocationStateFlow = locationViewModel.lastLocationStateFlow,
                                    listGarbageFlow = garbageViewModel.garbageState,
                                    loadListGarbage = { garbageViewModel.getAllGarbage() },
                                    loadLastLocation = { locationViewModel.getLastLocation() },
                                    reloadListGarbage = { garbageViewModel.reloadGarbage() },
                                    onLoadSelectedAddress = { addressViewModel.loadSelectedAddress() },
                                    onReloadSelectedAddress = { addressViewModel.reloadSelectedAddress() },
                                    addGarbageOrderSlot = { orderViewModel.addGarbageSlot()},
                                    deleteGarbageSlotAt = { orderViewModel.deleteGarbageAt(it)},
                                    updateGarbageAtIndex = { index, newGarbage -> orderViewModel.updateGarbage(index, newGarbage) },
                                    onMakeOrder = { listGarbage, totalTransaction, location -> orderViewModel.makeOrder(listGarbage, totalTransaction, location) },
                                    onAcceptResetOrder = {resetOrder()}
                                ) }
                            composable(Screen.ChangeAddress.route){
                                ChangeAddressScreen(
                                    navHostController = navController,
                                    onLoadSavedAddress = { addressViewModel.loadSavedAddress() },
                                    onAddNewAddress = { address -> addressViewModel.addNewAddress(address) },
                                    onDeleteAddress = { address -> addressViewModel.deleteAddress(address) },
                                    onSelectedAddress = { address -> addressViewModel.pickSelectedAddress(address) },
                                    onSaveSelectedAddress = { address -> addressViewModel.confirmSelectedAddress(address) },
                                    onReloadSavedAddress = { addressViewModel.reloadSavedAddress() },
                                    toastMessageState = addressViewModel.message,
                                    savedAddressStateFlow = addressViewModel.savedAddress,
                                    tempSelectedAddressStateFlow = addressViewModel.tempSelectedAddress,
                                )
                            }
                            composable(Screen.OrderSuccess.route){
                                OrderSuccessScreen(navHostController = navController)
                            }
                            composable(
                                route = Screen.DetailOrder.route,
                                arguments = listOf(navArgument("orderId"){type = NavType.StringType})
                            ){
                                val orderId = it.arguments?.getString("orderId") ?: ""
                                DetailOrderScreen(
                                    navHostController = navController,
                                    orderId = orderId,
                                    orderDetailStateFlow = orderViewModel.detailOrderState,
                                    onLoadDetailOrder = { id -> orderViewModel.loadDetailOrder(id) },
                                    onReloadDetailOrder = { orderViewModel.reloadDetailOrder() },
                                    onUpdateOrderStatus = { order, status -> orderViewModel.updateOrder(order, status) },
                                    updateOrderStatusState = MutableStateFlow(UiState.Loading),
                                )
                            }
                            composable(Screen.UsersChats.route){
                                UsersChatsScreen(
                                    navHostController = navController,
                                    onLoadChatRooms = {}
                                )
                            }
                            composable(
                                route = Screen.ChatRoom.route,
                                arguments = listOf(navArgument("roomId"){type = NavType.StringType})
                            ){
                                val roomId = it.arguments?.getString("roomId") ?: "ref"
                                ChatRoomScreen(navHostController = navController, roomId = roomId)
                            }
                        }
                    }
                }

            }
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {result ->
        if (result.resultCode == CAMERA_X_RESULT) {
            val imageUri = result.data?.getStringExtra("picture")
            val isBackCam = result.data?.getBooleanExtra("cam-facing", true)
            imageUri?.let {stringUri ->
                isBackCam?.let { isBackCam ->
                    cameraViewModel.setImage(
                        ImageCaptured(
                            uri = Uri.parse(stringUri),
                            isBackCam = isBackCam
                        )
                    )
                }
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == RESULT_OK){
            val selectedImage = result.data?.data as Uri
            selectedImage?.let { uri ->
                cameraViewModel.setImage(
                    ImageCaptured(uri = uri, isBackCam = true)
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}