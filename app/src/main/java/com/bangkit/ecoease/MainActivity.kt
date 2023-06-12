package com.bangkit.ecoease

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import com.bangkit.ecoease.data.model.request.FCMNotification
import com.bangkit.ecoease.data.model.request.Notification
import com.bangkit.ecoease.data.viewmodel.*
import com.bangkit.ecoease.di.Injection
import com.bangkit.ecoease.ui.component.*
import com.bangkit.ecoease.ui.screen.*
import com.bangkit.ecoease.ui.screen.auth.AuthScreen
import com.bangkit.ecoease.ui.screen.chat.ChatRoomScreen
import com.bangkit.ecoease.ui.screen.chat.UsersChatsScreen
import com.bangkit.ecoease.ui.screen.map.MapScreen
import com.bangkit.ecoease.ui.screen.onboard.OnBoardingScreen
import com.bangkit.ecoease.ui.screen.order.DetailOrderScreen
import com.bangkit.ecoease.ui.screen.order.OrderHistoryScreen
import com.bangkit.ecoease.ui.screen.order.OrderScreen
import com.bangkit.ecoease.ui.screen.order.OrderSuccessScreen
import com.bangkit.ecoease.ui.screen.register.RegisterScreen
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
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
    private lateinit var registerViewModel: RegisterViewModel



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashViewModel = ViewModelFactory(Injection.provideInjection(this)).create(SplashViewModel::class.java)
        super.onCreate(savedInstanceState)
        cameraViewModel = ViewModelFactory(Injection.provideInjection(this)).create(CameraViewModel::class.java)
        registerViewModel = ViewModelFactory(Injection.provideInjection(this)).create(RegisterViewModel::class.java)
        val authViewModel = ViewModelFactory(Injection.provideInjection(this)).create(AuthViewModel::class.java)
        val orderViewModel = ViewModelFactory(Injection.provideInjection(this)).create(OrderViewModel::class.java)
        val garbageViewModel = ViewModelFactory(Injection.provideInjection(this)).create(GarbageViewModel::class.java)
        val addressViewModel = ViewModelFactory(Injection.provideInjection(this)).create(AddressViewModel::class.java)
        val userViewModel = ViewModelFactory(Injection.provideInjection(this)).create(UserViewModel::class.java)
        val locationViewModel = ViewModelFactory(Injection.provideInjection(this)).create(LocationViewModel::class.java)
        val messageViewModel = ViewModelFactory(Injection.provideInjection(this)).create(MessageViewModel::class.java)

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
                                    userStateFlow = userViewModel.user,
                                    loadUser = { userViewModel.getUser() },
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
                            composable(Screen.Home.route){
                                DashboardScreen(
                                    navHostController = navController,
                                    garbageStateFlow = garbageViewModel.garbageState,
                                    onLoadGarbage = { garbageViewModel.getAllGarbage() },
                                    onReloadGarbage = { garbageViewModel.reloadGarbage() },
                                )
                            }
                            composable(route = Screen.Scan.route){
                                ScanScreen(
                                    navController = navController,
                                    imagePredictState = cameraViewModel.predictResultUiState,
                                    classifyImage = { file -> cameraViewModel.classify(file) },
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
                                    logoutAction = { authViewModel.logout(onSuccess = {
                                        navController.navigate(Screen.Auth.route){
                                            popUpTo(Screen.Home.route){
                                                inclusive = true
                                            }
                                        }
                                    }) }
                                )
                            }
                            composable(Screen.Map.route){
                                MapScreen(
                                    navHostController = navController,
                                    availableOrderStateFlow = orderViewModel.availableOrders,
                                    loadAvailableOrders = { orderViewModel.loadAvailableOrder() }
                                )
                            }
                            composable(Screen.Auth.route){
                                AuthScreen(
                                    navHostController = navController,
                                    emailValidation = authViewModel.emailValidation,
                                    passwordValidation = authViewModel.passwordValidation,
                                    validateEmail = { authViewModel.validateEmailInput() },
                                    validatePassword = { authViewModel.validatePasswordInput() },
                                    loginAction = { authViewModel.login(onSuccess = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Auth.route) {
                                                inclusive = true
                                            }
                                        }
                                    }) },
                                    isLoginValid = authViewModel.isLoginValid
                                )
                            }
                            composable(Screen.Register.route){
                                RegisterScreen(
                                    navHostController = navController,
                                    firstnameValidation = registerViewModel.firstnameValidation,
                                    lastnameValidation = registerViewModel.lastnameValidation,
                                    emailValidation = registerViewModel.emailValidation,
                                    phoneNumberValidation = registerViewModel.phoneNumberValidation,
                                    passwordValidation = registerViewModel.passwordValidation,
                                    imageProfile = registerViewModel.uiStateProfileImage,
                                    loadImageProfile = { registerViewModel.getProfileImageUri() },
                                    validateFirstnameInput = { registerViewModel.validateFirstnameInput() },
                                    validateLastnameInput = { registerViewModel.validateLastnameInput() },
                                    validateEmailInput = { registerViewModel.validateEmailInput() },
                                    validatePhoneNumberInput = { registerViewModel.validatePhoneNumberInput() },
                                    validatePasswordInput = { registerViewModel.validatePasswordInput() },
                                    errorEvent = registerViewModel.eventFlow,
                                    onRegister = { photoFile, onSuccess -> registerViewModel.register(photoFile, onSuccess) },
                                    openGallery = {
                                        val intent = Intent().apply {
                                            action = ACTION_GET_CONTENT
                                            type = "image/*"
                                        }
                                        val chooser = Intent.createChooser(intent, "Choose a picture")
                                        launcherIntentGalleryRegister.launch(chooser)
                                    }
                                )
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
                                    eventFlow = orderViewModel.eventFlow,
                                    onMakeOrder = { listGarbage, totalTransaction, location -> orderViewModel.makeOrder(listGarbage, totalTransaction, location, onSuccess = {
                                        navController.navigate(Screen.OrderSuccess.route){
                                            popUpTo(Screen.Order.route) {
                                                inclusive = true
                                            }

                                        }
                                    }) },
                                    onAcceptResetOrder = {resetOrder()}
                                ) }
                            composable(Screen.ChangeAddress.route){
                                ChangeAddressScreen(
                                    navHostController = navController,
                                    nameValidation = addressViewModel.addressNameValidation,
                                    cityValidation = addressViewModel.cityValidation,
                                    districtValidation = addressViewModel.districtValidation,
                                    detailValidation = addressViewModel.detailValidation,
                                    validateName = {addressViewModel.validateNameInput()},
                                    validateDetail = {addressViewModel.validateDetailInput()},
                                    validateCity = {addressViewModel.validateCityInput()},
                                    validateDistrict = {addressViewModel.validateDistrictInput()},
                                    onLoadSavedAddress = { addressViewModel.loadSavedAddress() },
                                    onAddNewAddress = { address -> addressViewModel.addNewAddress(address) },
                                    onDeleteAddress = { address -> addressViewModel.deleteAddress(address) },
                                    onSelectedAddress = { address -> addressViewModel.pickSelectedAddress(address) },
                                    onSaveSelectedAddress = { address, onSuccess -> addressViewModel.confirmSelectedAddress(address, onSuccess) },
                                    onReloadSavedAddress = { addressViewModel.reloadSavedAddress() },
                                    savedAddressStateFlow = addressViewModel.savedAddress,
                                    eventFlow = addressViewModel.eventFlow,
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
                                    orderId = orderId,
                                    userStateFlow = orderViewModel.myUserData,
                                    orderDetailStateFlow = orderViewModel.detailOrderState,
                                    onLoadDetailOrder = { id -> orderViewModel.loadDetailOrder(id) },
                                    onReloadDetailOrder = { orderViewModel.reloadDetailOrder() },
                                    onUpdateOrderStatus = { order, status -> orderViewModel.updateOrder(order, status, onSuccess = {
                                        navController.navigate(Screen.History.route){
                                            popUpTo(Screen.DetailOrder.createRoute(orderId)){
                                                inclusive = true
                                            }
                                        }
                                    }) },
                                    eventFlow = orderViewModel.eventFlow,
                                    sendNotification = { userFcmToken, message -> messageViewModel.sendNotification(
                                        FCMNotification(to = userFcmToken, notification = Notification(body = message, title = message, subTitle = message))
                                    ) },
                                )
                            }
                            composable(Screen.UsersChats.route){
                                UsersChatsScreen(
                                    navHostController = navController,
                                    onLoadChatRooms = {messageViewModel.getChatrooms()},
                                    chatroomsUiState = messageViewModel.chatrooms,
                                    eventFlow = messageViewModel.eventFlow,
                                    onDeleteRoom = { roomKey, roomId -> messageViewModel.deleteChatroom(roomKey, roomId) },
                                )
                            }
                            composable(
                                route = Screen.ChatRoom.route,
                                arguments = listOf(navArgument("roomId"){type = NavType.StringType})
                            ){
                                val roomId = it.arguments?.getString("roomId") ?: "ref"
                                ChatRoomScreen(
                                    getCurrentUser = {messageViewModel.getCurrentUser()},
                                    userUiState = messageViewModel.user,
                                    getChatroomDetail = {messageViewModel.getDetailChatroom(roomId)},
                                    chatroomDetailUiState = messageViewModel.detailChatrooms,
                                    reloadGetCurrentUser = {messageViewModel.reloadCurrentUser()},
                                    sendNotification = { body -> messageViewModel.sendNotification(body) },
                                    roomId = roomId,
                                )
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
    private val launcherIntentGalleryRegister = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == RESULT_OK){
            val selectedImage = result.data?.data as Uri
            selectedImage?.let { uri ->
                registerViewModel.setProfileImage(
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