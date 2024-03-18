package com.bluewhaleyt.codewhale.code.core.analyzer

interface Analyzer {
    fun analyze()

    fun reset()

    fun getProblems(): List<Any>
}