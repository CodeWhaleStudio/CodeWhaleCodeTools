package com.bluewhaleyt.codewhale.code.compiler.app.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.lang.SassExampleScreen

@Composable
fun MainScreen(navController: NavController) {
    Column {
        Button(
            onClick = {
                navController.navigate("sass_example")
            }
        ) {
            Text(text = "Sass compiler")
        }
        Button(
            onClick = {
                navController.navigate("java_example")
            }
        ) {
            Text(text = "Java compiler")
        }
    }
}