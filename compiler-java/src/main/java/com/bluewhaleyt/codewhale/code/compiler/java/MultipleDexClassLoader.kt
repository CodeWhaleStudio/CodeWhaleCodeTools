package com.bluewhaleyt.codewhale.code.compiler.java

import dalvik.system.BaseDexClassLoader
import java.io.File

internal class MultipleDexClassLoader(
    private val librarySearchPath: String? = null,
    classLoader: ClassLoader = ClassLoader.getSystemClassLoader()
) {
    val loader by lazy {
        BaseDexClassLoader("", null, librarySearchPath, classLoader)
    }

    private val addDexPath = BaseDexClassLoader::class.java
        .getMethod("addDexPath", String::class.java)

    fun loadDex(dexPath: String): BaseDexClassLoader {
        addDexPath.invoke(loader, dexPath)

        return loader
    }

    fun loadDex(dexFile: File) {
        loadDex(dexFile.absolutePath)
    }

    companion object {
        @JvmStatic
        val INSTANCE = MultipleDexClassLoader(classLoader = Companion::class.java.classLoader!!)
    }
}