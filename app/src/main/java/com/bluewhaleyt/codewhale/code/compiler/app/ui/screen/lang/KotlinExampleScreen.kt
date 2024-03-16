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
import com.bluewhaleyt.codewhale.code.compiler.core.ExperimentalCompilerApi
import com.bluewhaleyt.codewhale.code.compiler.core.Language
import com.bluewhaleyt.codewhale.code.compiler.core.applyCompileReporter
import com.bluewhaleyt.codewhale.code.compiler.java.JavaProject
import com.bluewhaleyt.codewhale.code.compiler.kotlin.KotlinCompilationResult
import com.bluewhaleyt.codewhale.code.compiler.kotlin.KotlinCompileOptions
import com.bluewhaleyt.codewhale.code.compiler.kotlin.KotlinCompiler
import com.bluewhaleyt.codewhale.code.compiler.kotlin.KotlinProject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun KotlinExampleScreen() {
    val scope = rememberCoroutineScope()
    var compilationResult by remember {
        mutableStateOf(KotlinCompilationResult())
    }
    var reporterText by remember { mutableStateOf("") }
    val compiler = KotlinCompiler(
        context = LocalContext.current,
        reporter = applyCompileReporter { report ->
            reporterText += "${report.kind}: ${report.message}\n"
        },
        project = KotlinProject(
            dataDir = File("$ROOT_DIR/kotlin"),
            projectDir = File("$ROOT_DIR/kotlin/projects/KotlinTest"),
        ),
        options = KotlinCompileOptions(
            jvmTarget = "17",
            languageVersion = "2.1",
        ).apply {
            generateJar = true
        }
    )
    Column {
        CompileScreen(
            title = "Kotlin Compiler",
            reporterText = reporterText,
            compilationResult = compilationResult,
            onCompile = {
                scope.launch(Dispatchers.IO) {
                    reporterText = ""
                    compilationResult = compiler.compile()
                    if (compilationResult.output?.isNotEmpty() == true) {
                        reporterText += "\n\n${compilationResult.output}"
                    }
                }
            },
        )
    }
}