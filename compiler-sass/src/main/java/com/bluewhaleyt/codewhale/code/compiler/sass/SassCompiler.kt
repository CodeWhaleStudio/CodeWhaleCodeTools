package com.bluewhaleyt.codewhale.code.compiler.sass

import android.content.Context
import com.bluewhaleyt.codewhale.code.compiler.core.CompileReporter
import com.bluewhaleyt.codewhale.code.compiler.core.Compiler
import de.larsgrefer.sass.embedded.SassCompiler
import de.larsgrefer.sass.embedded.android.AndroidSassCompilerFactory

class SassCompiler(
    private val context: Context,
    val reporter: CompileReporter,
    val options: SassCompilerOptions,
) : Compiler<SassCompilerOptions>(reporter, options) {

    private val compiler = AndroidSassCompilerFactory.bundled(context)

    override fun compile(): SassCompilationResult {
//        reporter.reportInfo("Compiling ${options.language}...")
        reporter.reportInfo("Assembling compiler powered by ${SassCompiler::class.java.name}...")

        val compilationResult = SassCompilationResult()
        try {
//            val compilationSuccess = when (options.language) {
//                SassCompilerLanguage.Css -> compiler.compileCssString(options.source)
//                SassCompilerLanguage.Sass -> compiler.compileSassString(options.source)
//                SassCompilerLanguage.Scss -> compiler.compileScssString(options.source)
//            }
            reporter.reportInfo("Compiling ${options.file.name}...")
            val compilationSuccess = compiler.compileFile(options.file)
            compilationResult.apply {
                output = null
            }
            reporter.reportSuccess("Compile successfully, no output is printed.")
        } catch (e: Throwable) {
            compilationResult.error = e
            reporter.reportError("Compile failed")
        }
        return compilationResult
    }

}