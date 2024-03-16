package com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.lang

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.android.tools.r8.CompilationMode
import com.bluewhaleyt.codewhale.code.compiler.app.ROOT_DIR
import com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.base.CompileScreen
import com.bluewhaleyt.codewhale.code.compiler.core.applyCompileReporter
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompilationResult
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompileOptions
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompiler
import com.bluewhaleyt.codewhale.code.compiler.java.JavaProject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun JavaExampleScreen() {
    val scope = rememberCoroutineScope()
    var compilationResult by remember {
        mutableStateOf(JavaCompilationResult())
    }
    var reporterText by remember { mutableStateOf("") }
    val compiler = JavaCompiler(
        context = LocalContext.current,
        reporter = applyCompileReporter { report ->
            reporterText += "${report.kind}: ${report.message}\n"
        },
        project = JavaProject(
            dataDir = File("$ROOT_DIR/java"),
            projectDir = File("$ROOT_DIR/java/projects/JavaTest")
        ),
        options = JavaCompileOptions(
            inputStream = "23".byteInputStream(), // for std input like if Scanner is used
            sourceVersion = "17",
            targetVersion = "17",
        )
    )
    Column {
        CompileScreen(
            title = "Java Compiler",
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