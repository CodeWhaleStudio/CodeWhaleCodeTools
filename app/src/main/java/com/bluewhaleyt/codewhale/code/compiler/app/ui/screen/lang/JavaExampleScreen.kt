package com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.lang

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.bluewhaleyt.codewhale.code.compiler.app.ROOT_DIR
import com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.base.CompileScreen
import com.bluewhaleyt.codewhale.code.compiler.core.Language
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
            projectDir = File("$ROOT_DIR/java/projects/JavaTest"),
        ),
        options = JavaCompileOptions(
            sourceVersion = "17",
            targetVersion = "17",
            generateJar = true
        )
    )

    fun compile() {
        compilationResult = compiler.compile()
        if (compilationResult.output?.isNotEmpty() == true) {
            reporterText += "\n\n${compilationResult.output}"
        }
    }

    Column {
        CompileScreen(
            title = "Java Compiler",
            reporterText = reporterText,
            language = Language.Java,
            compilationResult = compilationResult,
            onCompile = { inputValue ->
                scope.launch(Dispatchers.IO) {
                    reporterText = ""
                    compiler.options.inputStream = inputValue.byteInputStream()
                    compile()
                }
            },
        )
    }
}