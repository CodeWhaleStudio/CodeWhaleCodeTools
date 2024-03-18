package com.bluewhaleyt.codewhale.code.tool.app.ui.component

import android.content.Context
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.android.tools.r8.internal.ed
import io.github.rosemoe.sora.langs.java.JavaLanguage
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.SchemeDarcula

data class CodeEditorState(
    private val _content: Content = Content()
) {
    var content by mutableStateOf(_content)
}

@Composable
fun CodeEditor(
    modifier: Modifier = Modifier,
    state: CodeEditorState,
    onInitialize: (CodeEditor) -> Unit
) {
    val context = LocalContext.current
    val editor = remember {
        setCodeEditorFactory(context)
    }.apply {
        colorScheme = SchemeDarcula()
        typefaceText = Typeface.MONOSPACE
        typefaceLineNumber = Typeface.MONOSPACE
        props.indicatorWaveAmplitude = 2f
        props.indicatorWaveLength = 4f
        onInitialize(this)
    }
    AndroidView(
        factory = { editor },
        modifier = modifier,
        onRelease = { it.release() }
    )
    LaunchedEffect(key1 = state.content) {
        editor.setText(state.content)
    }
}

private fun setCodeEditorFactory(
    context: Context
): CodeEditor {
    val editor = CodeEditor(context).apply {

    }
    return editor
}