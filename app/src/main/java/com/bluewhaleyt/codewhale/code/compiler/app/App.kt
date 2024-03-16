package com.bluewhaleyt.codewhale.code.compiler.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.MainScreen
import com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.lang.SassExampleScreen

@Composable
fun App() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SassExampleScreen()
    }
}