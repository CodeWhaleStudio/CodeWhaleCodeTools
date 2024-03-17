package com.bluewhaleyt.codewhale.code.compiler.core

abstract class Compiler<T>(
    open val reporter: CompileReporter,
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

    object CompilerCache {
        @JvmStatic
        val cacheMap = mutableMapOf<Class<*>, Task>()

        @JvmStatic
        fun <T : Task> saveCache(compiler: T) {
            cacheMap[compiler::class.java] = compiler
        }

        @JvmStatic
        inline fun <reified T : Task> getCache(): T {
            return cacheMap[T::class.java] as T
        }
    }
}