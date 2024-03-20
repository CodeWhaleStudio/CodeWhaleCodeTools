package com.bluewhaleyt.codewhale.code.app.ui.screen.lang

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bluewhaleyt.codewhale.code.app.ROOT_DIR
import com.bluewhaleyt.codewhale.code.app.ui.screen.base.CompileScreen
import com.bluewhaleyt.codewhale.code.core.compiler.createCompileReporter
import com.bluewhaleyt.codewhale.code.language.java.analyzer.JavaAnalyzer
import com.bluewhaleyt.codewhale.code.language.java.codenavigation.JavaCodeNavigation
import com.bluewhaleyt.codewhale.code.language.java.codenavigation.JavaCodeNavigationItem
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaCompilationResult
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaCompiler
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaProject
import io.github.rosemoe.sora.langs.java.JavaLanguage
import io.github.rosemoe.sora.widget.CodeEditor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

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
        },
    )
    val analyzer = JavaAnalyzer(
        project = project,
        file = file,
    )
    val codeNavigation = JavaCodeNavigation(
        file = file,
    )
    var compilationResult by remember { mutableStateOf(JavaCompilationResult()) }
    Column {
//        var symbols by remember { mutableStateOf(emptyList<JavaCodeNavigationItem>()) }

//        var editorState by remember { mutableStateOf<CodeEditor?>(null) }
//
//        AnimatedVisibility(visible = codeNavigation.getSymbols().isNotEmpty()) {
//            LazyColumn {
//                itemsIndexed(symbols) { index, item ->
//                    Column(
//                        modifier = Modifier.padding(16.dp)
//                    ){
//                        Column(
//                            modifier = Modifier.padding(start = item.depth.dp * 30)
//                        ) {
//                            val pos = editorState?.text?.indexer?.getCharPosition(item.startPosition)
//                            val lineNumber = pos?.line?.plus(1)
//                            val columnNumber = pos?.column?.plus(1)
//                            Text(text = item.name)
//                            Text(text = "${item.modifier} ($lineNumber:$columnNumber)")
//                        }
//                    }
//                }
//            }
//        }

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
            }
        )
    }
}