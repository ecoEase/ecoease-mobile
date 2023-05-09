package com.bangkit.ecoease

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bangkit.ecoease.CameraActivity.Companion.CAMERA_X_RESULT
import com.bangkit.ecoease.config.ViewModelFactory
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.data.viewmodel.CameraViewModel
import com.bangkit.ecoease.data.viewmodel.SplashViewModel
import com.bangkit.ecoease.di.Injection
import com.bangkit.ecoease.ui.screen.CameraScreen
import com.bangkit.ecoease.ui.screen.OnBoardingScreen
import com.bangkit.ecoease.ui.screen.TempScreen
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    val splashViewModel = SplashViewModel()
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    val cameraViewModel: CameraViewModel = ViewModelFactory(Injection.provideInjection(this)).create(CameraViewModel::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition{
            splashViewModel.isLoading.value
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )

        setContent {
            EcoEaseTheme {
                val navController: NavHostController = rememberNavController()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Temp.route
                    ){
                        composable(Screen.Home.route){
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
                                imageUriState = cameraViewModel.uiStateImageUri,
                                onLoadingImageState = { cameraViewModel.getImageUri() },
                                openCamera = {
                                    val intent = Intent(this@MainActivity, CameraActivity::class.java)
                                    launcherIntentCameraX.launch(intent)
                                }
                            )
                        }
                        composable(Screen.Camera.route){
                            CameraScreen(
                                navController = navController,
                                executor = cameraExecutor
                            )
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
            imageUri?.let {stringUri ->
                cameraViewModel.setImageUri(Uri.parse(stringUri))
            }
            Log.d("TAG", "from camera activity: $imageUri")
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}