package com.bluewhaleyt.codewhale.code.core.compiler

import com.bluewhaleyt.codewhale.code.core.Task

abstract class Compiler<T>(
    open val reporter: CompileReporter = CompileReporter(),
    options: CompileOptions
) {
    companion object {
        @JvmStatic
        var compileListener: (Class<*>, BuildStatus) -> Unit = { _, _ -> }
    }

    open fun compile(): CompilationResult? = null

    inline fun <reified T : Task> compileTask(message: String) {
        val task = CompilerCache.getCache<T>()

        with(reporter) {
            if (failure) return
            reportInfo(message)
            compileListener(T::class.java, BuildStatus.STARTED)
            task.execute(this)
            compileListener(T::class.java, BuildStatus.FINISHED)

            if (failure) {
                reportOutput("Failed to compile ${T::class.simpleName} code.")
            }

            reportInfo("Successfully run ${T::class.simpleName}.")
        }
    }

    sealed class BuildStatus {
        data object STARTED : BuildStatus()
        data object FINISHED : BuildStatus()
    }
}