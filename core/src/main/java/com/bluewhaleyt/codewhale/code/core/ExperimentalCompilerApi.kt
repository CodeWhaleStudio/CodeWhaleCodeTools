package com.bluewhaleyt.codewhale.code.core

@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    "The compiler API is still consider as experimental, which is likely unstable. The API will " +
            "always get changes. Please use it at your own risk."
)
annotation class ExperimentalCompilerApi()
