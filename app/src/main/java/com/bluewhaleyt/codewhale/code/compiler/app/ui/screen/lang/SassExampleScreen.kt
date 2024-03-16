package com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.lang

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.bluewhaleyt.codewhale.code.compiler.app.ROOT_DIR
import com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.base.CompileScreen
import com.bluewhaleyt.codewhale.code.compiler.core.applyCompileReporter
import com.bluewhaleyt.codewhale.code.compiler.sass.SassCompilationResult
import com.bluewhaleyt.codewhale.code.compiler.sass.SassCompiler
import com.bluewhaleyt.codewhale.code.compiler.sass.SassCompilerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun SassExampleScreen() {
    val scope = rememberCoroutineScope()
    var compilationResult by remember {
        mutableStateOf(SassCompilationResult())
    }
    var reporterText by remember { mutableStateOf("") }
    val compiler = SassCompiler(
        context = LocalContext.current,
        options = SassCompilerOptions(
            file = File("$ROOT_DIR/others/style.css")
        ),
        reporter = applyCompileReporter { report ->
            reporterText += "${report.kind}: ${report.message}\n"
        }
    )
    Column {
        CompileScreen(
            title = "Sass Compiler",
            reporterText = reporterText,
            source = compiler.options.file.readText(),
            compilationResult = compilationResult,
            onCompile = {
                scope.launch(Dispatchers.IO) {
                    reporterText = ""
                    compilationResult = compiler.compile()
                }
            }
        )
    }
}