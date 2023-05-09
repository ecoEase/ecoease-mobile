package com.bangkit.ecoease

import android.content.Intent
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
import androidx.compose.ui.Modifier
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
import com.bangkit.ecoease.ui.screen.CameraScreen
import com.bangkit.ecoease.ui.screen.OnBoardingScreen
import com.bangkit.ecoease.ui.screen.TempScreen
import com.bangkit.ecoease.ui.theme.EcoEaseTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    val splashViewModel = SplashViewModel()
    private lateinit var cameraExecutor: ExecutorService

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
                val cameraViewModel: CameraViewModel = viewModel(
                    factory = ViewModelFactory()
                )

                val navController: NavHostController = rememberNavController()
                cameraExecutor = Executors.newSingleThreadExecutor()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
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
            val imageUri = it.data?.getSerializableExtra("picture")
            Log.d("TAG", "from camera activity: $imageUri")
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}



@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EcoEaseTheme {
        Greeting("Android")
    }
}