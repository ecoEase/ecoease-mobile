package com.bangkit.ecoease

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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

    // TODO: move all business logic in viewmodel
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashViewModel = ViewModelFactory(Injection.provideInjection(this)).create(SplashViewModel::class.java)
        super.onCreate(savedInstanceState)
        cameraViewModel = ViewModelFactory(Injection.provideInjection(this)).create(CameraViewModel::class.java)
        val orderViewModel = ViewModelFactory(Injection.provideInjection(this)).create(OrderViewModel::class.java)
        val garbageViewModel = ViewModelFactory(Injection.provideInjection(this)).create(GarbageViewModel::class.java)
        val addressViewModel = ViewModelFactory(Injection.provideInjection(this)).create(AddressViewModel::class.java)

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

                var isTopBarShown = !listNoTopBar.map { it.route }.contains(currentRoute)
                var topBarTitle = if(currentRoute != Screen.Home.route) Text( text = currentRoute?.let { text ->  text.replaceFirstChar { it.uppercase() }}  ?: "",textAlign = TextAlign.Center) else {}

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                    ,
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = {
                            // TODO: refactor topappbar code
//                                 TopBar(isShown = isTopBarShown, title = { topBarTitle })
                            if(!listNoTopBar.map { it.route }.contains(currentRoute)){
                                TopAppBar(
                                    backgroundColor = MaterialTheme.colors.background,
                                    elevation = 0.dp,
                                    title ={
                                        when{
                                            currentRoute?.substringBefore("?") == Screen.ChatRoom.route -> {
                                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                    Avatar(
                                                        imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=464&q=80",
                                                        size = AvatarSize.EXTRA_SMALL,
                                                    )
                                                    Text(
                                                        text = Screen.ChatRoom.getTitle()?.let { text ->  text.replaceFirstChar { it.uppercase() }}  ?: "",
                                                        textAlign = TextAlign.Center,
                                                    )
                                                }
                                            }
                                            currentRoute != Screen.Home.route -> Text(
                                                text = currentRoute?.let { text ->  text.replaceFirstChar { it.uppercase() }}  ?: "",
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                    },
                                    actions = {
                                        if(currentRoute == Screen.Home.route) Avatar(
                                            imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=464&q=80",
                                            size = AvatarSize.EXTRA_SMALL,
                                            modifier = Modifier
                                                .padding(end = 32.dp)
                                                .clickable { navController.navigate(Screen.Profile.route) }
                                        )
                                    },
                                    navigationIcon = { if(!listMainRoute.map { it.route }.contains(currentRoute)) IconButton(onClick = {
                                        if(orderViewModel.orderState.value.total > 0 && currentRoute == Screen.Order.route){
                                            openDialog = true
                                        }else{
                                            navController.popBackStack()
                                        }
                                    }) { Icon(Icons.Filled.ArrowBack, "backIcon")}},
                                )
                            }
                        },
                        floatingActionButton = {
                            if(listMainRoute.map { it.route }.contains(currentRoute)) FloatingButton(description = "scan", icon = Icons.Default.CameraEnhance, onClick = { navController.navigate(Screen.Scan.route) })
                        },
                        bottomBar = {
                            if(listMainRoute.map { it.route }.contains(currentRoute)) BottomNavBar(navController = navController, items = listMainRoute)
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
                                arguments = listOf(navArgument("path"){type = NavType.StringType})
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
                                    }
                                )
                            }
                            composable(Screen.Home.route){
                                DashboardScreen(navHostController = navController, garbageStateFlow = garbageViewModel.garbageState, loadGarbage = { garbageViewModel.getAllGarbage() })
                            }
                            composable(Screen.History.route){
                                OrderHistoryScreen(
                                    orderHistoryState = orderViewModel.orderHistoryState,
                                    loadOrderHistory = { orderViewModel.loadOrderHistory() },
                                    navHostController = navController
                                )
                            }
                            composable(Screen.Profile.route){
                                ProfileScreen(navHostController = navController, logoutAction = { splashViewModel.logout() })
                            }
                            composable(Screen.Map.route){
                                MapScreen()
                            }
                            composable(Screen.Auth.route){
                                AuthScreen(navHostController = navController, loginAction = { splashViewModel.login() })
                            }
                            composable(Screen.Register.route){
                                RegisterScreen(navHostController = navController)
                            }
                            composable(Screen.Order.route){
                                OrderScreen(
                                    navHostController = navController,
                                    orderStateFlow = orderViewModel.orderState,
                                    addGarbageOrderSlot = { orderViewModel.addGarbageSlot()},
                                    deleteGarbageSlotAt = { orderViewModel.deleteGarbageAt(it)},
                                    updateGarbageAtIndex = { index, newGarbage -> orderViewModel.updateGarbage(index, newGarbage) },
                                    onAcceptResetOrder = {resetOrder()}
                                ) }
                            composable(Screen.ChangeAddress.route){
                                ChangeAddressScreen(
                                    navHostController = navController,
                                    onLoadSavedAddress = { addressViewModel.loadSavedAddress() },
                                    onAddNewAddress = { address -> addressViewModel.addNewAddress(address) },
                                    savedAddressStateFlow = addressViewModel.savedAddress
                                )
                            }
                            composable(Screen.OrderSuccess.route){
                                OrderSuccessScreen(navHostController = navController)
                            }
                            composable(Screen.DetailOrder.route){
                                DetailOrderScreen(navHostController = navController)
                            }
                            composable(Screen.UsersChats.route){
                                UsersChatsScreen(navHostController = navController)
                            }
                            composable(
                                route = "${Screen.ChatRoom.route}?roomId={roomId}",
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
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val imageUri = it.data?.getStringExtra("picture")
            val isBackCam = it.data?.getBooleanExtra("cam-facing", true)
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
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}