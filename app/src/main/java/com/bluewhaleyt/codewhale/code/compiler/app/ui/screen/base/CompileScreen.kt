package com.bluewhaleyt.codewhale.code.compiler.app.ui.screen.base

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.bluewhaleyt.codewhale.code.compiler.core.CompilationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CompileScreen(
    title: String,
    reporterText: String,
    compilationResult: CompilationResult,
    onCompile: () -> Unit,
) {
    val pagerState = rememberPagerState(
        pageCount = { 2 }
    )
    var selectedTabIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

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
                IconButton(onClick = onCompile) {
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
    }
}

@Composable
private fun CodeText(
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