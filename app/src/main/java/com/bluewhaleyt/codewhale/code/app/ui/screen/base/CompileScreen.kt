package com.bluewhaleyt.codewhale.code.app.ui.screen.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bluewhaleyt.codewhale.code.app.ui.component.CodeEditor
import com.bluewhaleyt.codewhale.code.app.ui.component.CodeEditorState
import com.bluewhaleyt.codewhale.code.core.ExperimentalCompilerApi
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompileScreen(
    file: File?,
    reporterText: String,
    output: String?,
    onCompile: suspend CoroutineScope.() -> Unit,
    onEditorInitialize: ((CodeEditor) -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { 2 })

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        onCompile()
                    }
                }
            ) {
                Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = "Compile")
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.height(48.dp)
            ) {
                for (i in 0 .. 1) {
                    val text = when (i) {
                        0 -> "Code"
                        1 -> "Output"
                        else -> ""
                    }
                    Tab(
                        modifier = Modifier.height(48.dp),
                        selected = selectedTabIndex == i,
                        onClick = {
                            scope.launch {
                                selectedTabIndex = i
                                pagerState.animateScrollToPage(i)
                            }
                        }
                    ) {
                        Text(text = text)
                    }
                }
            }
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> TabPage1(
                        file = file,
                        onEditorInitialize = {
                            onEditorInitialize?.invoke(it)
                        }
                    )
                    1 -> TabPage2(
                        reporterText = reporterText,
                        output = output
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalCompilerApi::class)
@Composable
private fun TabPage1(
    file: File?,
    onEditorInitialize: (CodeEditor) -> Unit
) {
    val editorState by remember { mutableStateOf(CodeEditorState()) }
    CodeEditor(
        modifier = Modifier.fillMaxSize(),
        state = editorState,
        onInitialize = {
            it.apply {
                editorState.content = Content(file?.readText())
                onEditorInitialize(this)
            }
        }
    )
}

@Composable
private fun TabPage2(
    reporterText: String,
    output: String?
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = reporterText
        )
        output?.let {
            AnimatedVisibility(visible = it.isNotEmpty()) {
                Text(
                    text = output,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun CodeEditor.appendText(content: Content): Int {
    if (lineCount <= 0) {
        return 0
    }
    var col = text.getColumnCount(lineCount - 1)
    if (col < 0) {
        col = 0
    }
    text.insert(lineCount - 1, col, content)
    return lineCount - 1
}