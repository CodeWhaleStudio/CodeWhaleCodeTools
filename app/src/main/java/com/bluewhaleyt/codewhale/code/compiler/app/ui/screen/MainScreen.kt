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
        CompilerButton(text = "Sass compiler") {
            navController.navigate("sass_example")
        }
        CompilerButton(text = "Java compiler") {
            navController.navigate("java_example")
        }
        CompilerButton(text = "Kotlin compiler") {
            navController.navigate("kotlin_example")
        }
    }
}

@Composable
private fun CompilerButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick
    ) {
        Text(text = text)
    }
}