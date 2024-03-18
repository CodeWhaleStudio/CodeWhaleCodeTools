package com.bluewhaleyt.codewhale.language.analyzer

import com.bluewhaleyt.codewhale.code.core.ExperimentalCompilerApi
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.event.EventReceiver
import io.github.rosemoe.sora.event.Unsubscribe
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticsContainer
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.subscribeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalCompilerApi::class)
fun CodeEditor.addJavaDiagnosticsMarker(analyzer: JavaAnalyzer) =
    subscribeEvent(JavaDiagnosticsMarkerEvent(this, analyzer))

@OptIn(ExperimentalCompilerApi::class)
class JavaDiagnosticsMarkerEvent(
    val editor: CodeEditor,
    val analyzer: JavaAnalyzer
) : EventReceiver<ContentChangeEvent> {

    private val diagnostics = DiagnosticsContainer()

    init {
        analyze(editor.text)
    }

    override fun onReceive(event: ContentChangeEvent, unsubscribe: Unsubscribe) {
        analyze(event.editor.text)
    }

    private fun analyze(content: Content) = CoroutineScope(Dispatchers.IO).launch {
        analyzer.file?.writeText(content.toString())
        analyzer.reset()

        analyzer.analyze()
        diagnostics.reset()
        diagnostics.addDiagnostics(analyzer.getProblems())

        editor.diagnostics = diagnostics
    }
}
