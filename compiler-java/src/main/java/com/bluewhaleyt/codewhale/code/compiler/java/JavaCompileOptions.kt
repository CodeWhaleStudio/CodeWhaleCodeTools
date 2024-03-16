package com.bluewhaleyt.codewhale.code.compiler.java

import android.os.Build
import com.android.tools.r8.CompilationMode
import com.bluewhaleyt.codewhale.code.compiler.core.CompileOptions
import java.io.InputStream

data class JavaCompileOptions(
    val sourceVersion: String = "8",
    val targetVersion: String = sourceVersion,
    val releaseJar: Boolean = false,
    val flags: String? = null,
    val minApiLevel: Int = Build.VERSION_CODES.O,
    val mode: CompilationMode = CompilationMode.DEBUG,
    val inputStream: InputStream? = null
) : CompileOptions()