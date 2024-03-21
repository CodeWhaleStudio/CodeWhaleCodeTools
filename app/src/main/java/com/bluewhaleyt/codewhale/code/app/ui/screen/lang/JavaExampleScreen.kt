package com.bluewhaleyt.codewhale.code.app.ui.screen.lang

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluewhaleyt.codewhale.code.app.ROOT_DIR
import com.bluewhaleyt.codewhale.code.core.compiler.createCompileReporter
import com.bluewhaleyt.codewhale.code.language.java.analyzer.JavaAnalyzer
import com.bluewhaleyt.codewhale.code.language.java.codenavigation.JavaCodeNavigation
import com.bluewhaleyt.codewhale.code.language.java.codenavigation.JavaCodeNavigationSymbol
import com.bluewhaleyt.codewhale.code.language.java.codenavigation.JavaCodeNavigationSymbolKind
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaCompilationResult
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaCompiler
import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaProject
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
        var symbols by remember { mutableStateOf(emptyList<JavaCodeNavigationSymbol>()) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(key1 = Unit) {
            scope.launch {
                symbols = codeNavigation.getSymbols()
            }
        }

        AnimatedVisibility(visible = symbols.isNotEmpty()) {
            LazyColumn {
                itemsIndexed(symbols) { index, symbol ->
                    val indentPadding = symbol.depth.dp * 30
                    Row(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .fillMaxWidth()
                            .padding(start = indentPadding)
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(
                                color = MaterialTheme.colorScheme.primary,
                                width = 1.dp
                            ),
                            modifier = Modifier
                                .padding(4.dp)
                                .size(18.dp),
                            shape = CircleShape,
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = symbol.kind.name.first().toString(),
                                    fontSize = 14.sp,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row {
                                symbol.name?.let {
                                    Text(
                                        text = it,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace,
                                    )
                                }
                                when (symbol.kind) {
                                    JavaCodeNavigationSymbolKind.Class -> {
                                        symbol.extends?.let {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "extends ${it.joinToString(", ")}",
                                                color = MaterialTheme.colorScheme.tertiary,
                                                fontSize = 14.sp,
                                                fontFamily = FontFamily.Monospace,
                                            )
                                        }
                                        symbol.implements?.let {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "implements ${it.joinToString(", ")}",
                                                color = MaterialTheme.colorScheme.tertiary,
                                                fontSize = 14.sp,
                                                fontFamily = FontFamily.Monospace,
                                            )
                                        }
                                    }
                                    JavaCodeNavigationSymbolKind.Method -> {
                                        symbol.type?.let {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = ": $it",
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontSize = 14.sp,
                                                fontFamily = FontFamily.Monospace,
                                            )
                                        }
                                        symbol.throws?.let {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "throws ${it.joinToString(", ")}",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 14.sp,
                                                fontFamily = FontFamily.Monospace,
                                            )
                                        }
                                    }
                                    else -> {
                                        symbol.type?.let {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = ": $it",
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontSize = 14.sp,
                                                fontFamily = FontFamily.Monospace,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


//        CompileScreen(
//            file = file,
//            reporterText = reporterText,
//            output = compilationResult.output,
//            onCompile = {
//                reporterText = ""
//                compilationResult = compiler.compile()
//            },
//            onEditorInitialize = {
//                it.setEditorLanguage(JavaLanguage())
//            }
//        )
    }
}