package com.bluewhaleyt.codewhale.code.compiler.kotlin

import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompileOptions

data class KotlinCompileOptions(
    var languageVersion: String = "1.8",
    var apiVersion: String = languageVersion,
    var jvmTarget: String = "1.8"
) : JavaCompileOptions()