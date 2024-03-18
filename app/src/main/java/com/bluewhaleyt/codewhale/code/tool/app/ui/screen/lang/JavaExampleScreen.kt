package com.bluewhaleyt.codewhale.code.tool.app.ui.screen.lang

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.bluewhaleyt.codewhale.code.tool.app.ROOT_DIR
import com.bluewhaleyt.codewhale.code.tool.app.ui.screen.base.CompileScreen
import com.bluewhaleyt.codewhale.code.core.ExperimentalCompilerApi
import com.bluewhaleyt.codewhale.code.core.compiler.createCompileReporter
import com.bluewhaleyt.codewhale.language.compiler.java.JavaCompilationResult
import com.bluewhaleyt.codewhale.language.compiler.java.JavaCompiler
import com.bluewhaleyt.codewhale.language.compiler.java.JavaProject
import com.bluewhaleyt.codewhale.language.analyzer.JavaAnalyzer
import com.bluewhaleyt.codewhale.language.analyzer.addJavaDiagnosticsMarker
import io.github.rosemoe.sora.langs.java.JavaLanguage
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
@Composable
fun JavaExampleScreen() {
    var reporterText by remember { mutableStateOf("") }
    val project = JavaProject(
        rootDir = File("$ROOT_DIR/java"),
        projectDir = File("$ROOT_DIR/java/projects/JavaTest")
    )
    val file = project.getSourceFiles(project.srcDir, "java")
        .find { it.name == "Main.java" }
    val compiler = JavaCompiler(
        context = LocalContext.current,
        project = project,
        reporter = createCompileReporter {
            reporterText += "${it.kind}: ${it.message}\n"
        }
    )
    val analyzer = JavaAnalyzer(
        project = project,
        file = file,
    )
    var compilationResult by remember { mutableStateOf(JavaCompilationResult()) }
    CompileScreen(
        file = file,
        reporterText = reporterText,
        output = compilationResult.output,
        onCompile = {
            reporterText = ""
            compilationResult = compiler.compile()
        },
        onEditorInitialize = {
            it.setEditorLanguage(JavaLanguage())
            it.addJavaDiagnosticsMarker(analyzer)
        }
    )
}