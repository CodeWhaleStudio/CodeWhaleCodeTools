package com.bluewhaleyt.codewhale.code.core.compiler

import com.bluewhaleyt.codewhale.code.core.Task

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