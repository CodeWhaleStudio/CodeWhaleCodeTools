package com.bluewhaleyt.codewhale.code.tool.app.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    Column {
        CompilerButton(text = "Java compiler") {
            navController.navigate("java_example")
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