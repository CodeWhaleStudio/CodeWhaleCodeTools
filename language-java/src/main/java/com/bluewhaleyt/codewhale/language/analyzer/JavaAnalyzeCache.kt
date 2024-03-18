package com.bluewhaleyt.codewhale.language.analyzer

import java.io.File
import javax.tools.JavaFileObject
import javax.tools.SimpleJavaFileObject

internal object JavaAnalyzeCache {

    val cacheMap = mutableMapOf<String, JavaFileObject>()
    fun saveCache(file: File): JavaFileObject {
        val obj = object : SimpleJavaFileObject(file.toURI(), JavaFileObject.Kind.SOURCE) {
            private val lastModified = file.lastModified()
            override fun getCharContent(ignoreEncodingErrors: Boolean): CharSequence {
                return file.readText()
            }

            override fun getLastModified(): Long {
                return lastModified
            }
        }
        cacheMap[file.absolutePath] = obj
        return obj
    }

    fun saveCache(obj: JavaFileObject) {
        cacheMap[obj.name] = obj
    }

    fun getCache(key: File): JavaFileObject? {
        return cacheMap[key.absolutePath]
    }
}