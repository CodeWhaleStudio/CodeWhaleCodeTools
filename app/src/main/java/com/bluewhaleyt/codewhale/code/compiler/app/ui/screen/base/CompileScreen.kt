package com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.base

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Input
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.bluewhaleyt.codewhale.code.compiler.core.CompilationResult
import com.bluewhaleyt.codewhale.code.compiler.core.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CompileScreen(
    title: String,
    reporterText: String,
    language: Language,
    compilationResult: CompilationResult,
    onCompile: (inputValue: String) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { 2 }
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val showInputDialog = remember { mutableStateOf(false) }
    var inputValue by remember { mutableStateOf("") }

    LaunchedEffect(key1 = compilationResult.error) {
        compilationResult.error?.let {
            selectedTabIndex = 1
            pagerState.animateScrollToPage(1)
        }
    }
    
    Column {
        TopAppBar(
            title = { Text(text = title) },
            actions = {
                IconButton(
                    onClick = {
                        if (language.hasStandardInput) {
                            showInputDialog.value = true
                        } else {
                            onCompile("")
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = "Compile")
                }
            }
        )
        
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.height(45.dp),
        ) {
            for (i in 0 until pagerState.pageCount) {
                Tab(
                    modifier = Modifier.height(45.dp),
                    selected = selectedTabIndex == i,
                    onClick = {
                        scope.launch { 
                            selectedTabIndex = i
                            pagerState.animateScrollToPage(i)
                        }
                    }
                ) {
                    val text = when (i) {
                        0 -> "Output"
                        1 -> "Error"
                        else -> ""
                    }
                    Text(text = text)
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { pageIndex ->
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                when (pageIndex) {
                    0 -> {
                        CodeText(
                            modifier = Modifier.weight(1f),
                            text = reporterText
                        ).takeIf { reporterText.isNotEmpty() }
                    }
                    1 -> {
                        compilationResult.error?.let {
                            CodeText(
                                text = it.stackTraceToString(),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        if (showInputDialog.value && language.hasStandardInput) {
            AlertDialog(
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                ),
                modifier = Modifier.width(360.dp),
                onDismissRequest = { showInputDialog.value = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                showInputDialog.value = false
                                onCompile(inputValue)
                            }
                        }
                    ) {
                        Text(text = stringResource(id = android.R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                showInputDialog.value = false
                            }
                        }
                    ) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                },
                title = {
                    Text(text = "Standard input")
                },
                text = {
                    Column {
                        Text(text = """
                            If standard input is required for the program. Please type the value to continue.
                        """.trimIndent())
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = inputValue,
                            onValueChange = {
                                inputValue = it
                            },
                            label = {
                                Text(text = "Input")
                            },
                            leadingIcon = {
                                Icon(imageVector = Icons.Outlined.TextFields, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = """
                            Otherwise, just skip this action by clicking ${stringResource(id = android.R.string.ok)}. The error will be thrown if you did not pay attention to.
                        """.trimIndent())
                    }
                }
            )
        }

    }
}

@Composable
internal fun CodeText(
    modifier: Modifier = Modifier,
    text: String?,
    color: Color = LocalContentColor.current
) {
    text?.let {
        SelectionContainer(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
        ) {
            Text(
                text = it,
                color = color,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}