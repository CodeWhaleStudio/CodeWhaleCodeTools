package com.bluewhaleyt.codewhale.code.compiler.java

import android.os.Build
import com.android.tools.r8.CompilationMode
import com.bluewhaleyt.codewhale.code.compiler.core.CompileOptions
import java.io.InputStream

open class JavaCompileOptions(
    var sourceVersion: String = "8",
    var targetVersion: String = sourceVersion,
    var generateJar: Boolean = false,
    var flags: String? = null,
    var minApiLevel: Int = Build.VERSION_CODES.O,
    var mode: CompilationMode = CompilationMode.DEBUG,
    var inputStream: InputStream? = null
) : CompileOptions()