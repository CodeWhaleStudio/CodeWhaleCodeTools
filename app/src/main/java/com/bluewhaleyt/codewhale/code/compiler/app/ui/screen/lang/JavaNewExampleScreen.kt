package com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.lang

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.bluewhaleyt.codewhale.code.compiler.app.ROOT_DIR
import com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.base.CodeText
import com.bluewhaleyt.codewhale.code.compiler.core.applyCompileReporter
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompilationResult
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompileOptions
import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompiler
import com.bluewhaleyt.codewhale.code.compiler.java.JavaProject
import com.bluewhaleyt.codewhale.code.compiler.java.utils.JavaCompilerUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JavaNewExampleScreen() {
    val scope = rememberCoroutineScope()
    var reporterText by remember { mutableStateOf("") }
    var compilationResult by remember { mutableStateOf(
        JavaCompilationResult()
    ) }
    var showSelectClassDialog by remember { mutableStateOf(false) }
    var availableClasses by remember { mutableStateOf(emptyList<String>()) }
    var compileComplete by remember { mutableStateOf(false) }
    var showCompileSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val project = JavaProject(
        dataDir = File("$ROOT_DIR/java"),
        projectDir = File("$ROOT_DIR/java/projects/JavaTest"),
    )
    val compiler = JavaCompiler(
        context = LocalContext.current,
        reporter = applyCompileReporter { report ->
            reporterText = report.message
        },
        project = project,
    )

    suspend fun showSheet() {
        showCompileSheet = true
        sheetState.show()
    }

    suspend fun closeSheet() {
        sheetState.hide()
        if (!sheetState.isVisible) {
            showCompileSheet = false
        }
    }

    fun compile() {
        scope.launch {
            showSheet()
        }
        scope.launch(Dispatchers.IO) {
            compilationResult = compiler.compile()
            delay(500L)
            closeSheet()
            compileComplete = true
        }
    }
    LaunchedEffect(key1 = Unit) {
        scope.launch(Dispatchers.IO) {
            availableClasses = compiler.checkClasses()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedContent(targetState = availableClasses) { availableClasses ->
            if (availableClasses.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Button(
                    onClick = {
                        if (availableClasses.size > 1) {
                            showSelectClassDialog = true
                        } else {
                            compiler.options.className = availableClasses.first()
                            compile()
                        }
                    }
                ) {
                    Text(text = "compile")
                }
            }
        }
        if (showSelectClassDialog) {
            AlertDialog(
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                ),
                modifier = Modifier.width(360.dp),
                onDismissRequest = { showSelectClassDialog = false },
                confirmButton = {},
                title = {
                    Text(text = "Found ${availableClasses.size} classes")
                },
                text = {
                    Column {
                        Text(text = "Please select a class to compile.")
                        LazyColumn {
                            itemsIndexed(availableClasses) { index, className ->
                                ListItem(
                                    modifier = Modifier
                                        .clickable {
                                            compiler.options.className = className
                                            compile()
                                            showSelectClassDialog = false
                                        }
                                        .fillMaxWidth(),
                                    headlineContent = {
                                        Text(text = className)
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }

        if (showCompileSheet) {
            ModalBottomSheet(
                onDismissRequest = {},
                sheetState = sheetState
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = reporterText)
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
        AnimatedVisibility(visible = compileComplete) {
            Column {
                Text(text = "Output", fontSize = 24.sp)
                CodeText(
                    text = compilationResult.output
                )
                compilationResult.error?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Error", fontSize = 24.sp)
                    CodeText(
                        text = compilationResult.error?.stackTraceToString(),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}