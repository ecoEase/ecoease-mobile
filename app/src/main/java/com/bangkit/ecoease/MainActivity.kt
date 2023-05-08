package com.bangkit.ecoease

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bangkit.ecoease.data.Screen
import com.bangkit.ecoease.ui.screen.CameraScreen
import com.bangkit.ecoease.ui.screen.LottieScreen
import com.bangkit.ecoease.ui.screen.TempScreen
import com.bangkit.ecoease.ui.theme.EcoEaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                            LottieScreen()
                        }
                        composable(
                            route = Screen.Temp.route,
                            arguments = listOf(navArgument("path"){type = NavType.StringType})
                        ){
                            val filePath = it.arguments?.getString("path") ?: ""
                            TempScreen(
                                navController = navController,
                                filePath = filePath
                            )
                        }
                        composable(Screen.Camera.route){
                            CameraScreen(
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
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