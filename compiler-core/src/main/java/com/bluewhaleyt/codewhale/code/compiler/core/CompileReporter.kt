package com.bluewhaleyt.codewhale.code.compiler.core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class CompilerReportKind {
    INFO, WARNING, ERROR, LOGGING, OUTPUT
}

data class CompileReport(
    val kind: CompilerReportKind,
    val message: String
)

open class CompileReporter(
    val callback: (CompileReport) -> Unit = { report ->
        println("${report.kind}: ${report.message}")
    }
) {
    var success = false
        private set
    var failure = false
        private set
    private var startTime = System.currentTimeMillis()
//    private var totalTime = 0L

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
//        totalTime += endTime - startTime
//        reportOutput(message(totalTime))
        reportOutput(message)
        success = true
    }

    private fun formatTime(timeMillis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(timeMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis)
        val pattern = if (hours > 0) {
            "HH'h'mm'm'ss's'"
        } else if (minutes > 0) {
            "m'm'ss's'"
        } else {
            "s's'"
        }
        return SimpleDateFormat(pattern, Locale.getDefault())
            .format(Date(timeMillis))
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