package com.bangkit.ecoease

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
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
import com.bangkit.ecoease.data.viewmodel.CameraViewModel
import com.bangkit.ecoease.data.viewmodel.SplashViewModel
import com.bangkit.ecoease.di.Injection
import com.bangkit.ecoease.ui.component.BottomNavBar
import com.bangkit.ecoease.ui.component.FloatingButton
import com.bangkit.ecoease.ui.screen.CameraScreen
import com.bangkit.ecoease.ui.screen.OnBoardingScreen
import com.bangkit.ecoease.ui.screen.TempScreen
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    lateinit var cameraViewModel: CameraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashViewModel = SplashViewModel()
        super.onCreate(savedInstanceState)

        cameraViewModel = ViewModelFactory(Injection.provideInjection(this)).create(CameraViewModel::class.java)
        installSplashScreen().setKeepOnScreenCondition{
            splashViewModel.isLoading.value
        }

        val listRoute = listOf(
            Screen.Temp,
            Screen.History,
            Screen.Map,
            Screen.Profile
        )
        setContent {
            EcoEaseTheme {
                val navController: NavHostController = rememberNavController()
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                    ,
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        floatingActionButton = {
                            if(currentRoute != Screen.Onboard.route) FloatingButton(description = "scan", icon = Icons.Default.CameraAlt)
                       },
                        bottomBar = {
                            if(currentRoute != Screen.Onboard.route) BottomNavBar(navController = navController, items = listRoute)
                        },
                        floatingActionButtonPosition = FabPosition.Center,
                        isFloatingActionButtonDocked = true,
                    ) {paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Onboard.route,
                            modifier = Modifier.padding(paddingValues)
                        ){
                            composable(Screen.Onboard.route){
                                OnBoardingScreen(
                                    navController = navController
                                )
                            }
                            composable(
                                route = Screen.Temp.route,
                                arguments = listOf(navArgument("path"){type = NavType.StringType})
                            ){
                                val filePath = it.arguments?.getString("path") ?: ""
                                TempScreen(
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
                            composable(Screen.History.route){
                                Text(text = "history")
                            }
                            composable(Screen.Profile.route){
                                Text(text = "history")
                            }
                            composable(Screen.Map.route){
                                Text(text = "map")
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
            Log.d("TAG", "from camera activity: $imageUri")
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}