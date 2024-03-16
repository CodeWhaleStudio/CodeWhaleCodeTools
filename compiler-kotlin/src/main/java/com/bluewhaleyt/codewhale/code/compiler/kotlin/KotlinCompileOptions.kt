package com.bluewhaleyt.codewhale.code.compiler.kotlin

import com.bluewhaleyt.codewhale.code.compiler.java.JavaCompileOptions

data class KotlinCompileOptions(
    val languageVersion: String = "1.8",
    val apiVersion: String = languageVersion,
    val jvmTarget: String = "1.8"
) : JavaCompileOptions()