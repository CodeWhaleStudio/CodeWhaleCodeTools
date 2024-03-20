package com.bluewhaleyt.codewhale.code.language.kotlin

import com.bluewhaleyt.codewhale.code.language.java.compiler.JavaCompileOptions

data class KotlinCompileOptions(
    var languageVersion: String = "1.8",
    var apiVersion: String = languageVersion,
    var jvmTarget: String = "1.8",
    override var className: String = "MainKt"
) : JavaCompileOptions()