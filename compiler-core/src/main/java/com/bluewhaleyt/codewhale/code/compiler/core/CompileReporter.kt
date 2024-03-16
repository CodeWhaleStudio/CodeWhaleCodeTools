package com.bluewhaleyt.codewhale.code.compiler.core

enum class CompilerReportKind {
    INFO, WARNING, ERROR, LOGGING, OUTPUT
}

data class CompileReport(
    val kind: CompilerReportKind,
    val message: String
)

class CompileReporter(
    val callback: (CompileReport) -> Unit = { report ->
        println("${report.kind}: ${report.message}")
    }
) {
    var success = false
        private set
    var failure = false
        private set
    private var startTime = System.currentTimeMillis()

    private fun report(kind: CompilerReportKind, message: String) {
        callback(CompileReport(kind, message))
    }

    fun reportInfo(message: String) {
        report(CompilerReportKind.INFO, message)
    }

    fun reportWarning(message: String) {
        report(CompilerReportKind.WARNING, message)
    }

    fun reportError(message: String) {
        report(CompilerReportKind.ERROR, message)
        failure = true
    }

    fun reportLogging(message: String) {
        report(CompilerReportKind.LOGGING, message)
    }

    fun reportOutput(message: String) {
        report(CompilerReportKind.OUTPUT, message)
    }

    fun reportSuccess(message: String) {
        if (failure) return
        val endTime = System.currentTimeMillis()
        reportOutput(message)
        success = true
    }

}

fun applyCompileReporter(
    onUpdate: (CompileReport) -> Unit
): CompileReporter {
    return CompileReporter { report ->
        if (report.message.isEmpty()) return@CompileReporter
        onUpdate(report)
    }
}