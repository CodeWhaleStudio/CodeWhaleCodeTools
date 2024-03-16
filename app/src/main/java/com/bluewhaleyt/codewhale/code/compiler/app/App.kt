package com.bluewhaleyt.codewhale.code.compiler.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.MainScreen
import com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.lang.JavaExampleScreen
import com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.lang.KotlinExampleScreen
import com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.lang.SassExampleScreen

@Composable
fun App() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "main"
        ) {
            composable("main") {
                MainScreen(navController)
            }
            composable("sass_example") {
                SassExampleScreen()
            }
            composable("java_example") {
                JavaExampleScreen()
            }
            composable("kotlin_example") {
                KotlinExampleScreen()
            }
        }
    }
}