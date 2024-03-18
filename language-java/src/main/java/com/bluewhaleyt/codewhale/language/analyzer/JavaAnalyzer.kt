package com.bluewhaleyt.codewhale.language.analyzer

import com.bluewhaleyt.codewhale.code.core.analyzer.Analyzer
import com.bluewhaleyt.codewhale.code.core.ExperimentalCompilerApi
import com.bluewhaleyt.codewhale.code.core.compiler.CompilerCache.saveCache
import com.bluewhaleyt.codewhale.language.compiler.java.JavaCompileOptions
import com.bluewhaleyt.codewhale.language.compiler.java.JavaProject
import com.sun.tools.javac.api.JavacTool
import com.sun.tools.javac.file.JavacFileManager
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticDetail
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticRegion
import java.io.File
import java.nio.charset.Charset
import java.util.Locale
import javax.tools.Diagnostic
import javax.tools.DiagnosticCollector
import javax.tools.JavaFileObject
import javax.tools.StandardLocation

@ExperimentalCompilerApi
class JavaAnalyzer(
    val project: JavaProject,
    val file: File?,
    val options: JavaCompileOptions = JavaCompileOptions(),
    val diagnosticDetail: (Diagnostic<out JavaFileObject>) -> DiagnosticDetail = { diagnostic ->
        DiagnosticDetail(briefMessage = diagnostic.getMessage(Locale.getDefault()))
    },
) : Analyzer {

    private val args by lazy {
        listOf(
            "-XDcompilePolicy=byfile",
            "-XD-Xprefer=source",
            "-XDide",
            "-XDsuppressAbortOnBadClassFile",
            "-XDshould-stop.at=GENERATE",
            "-XDdiags.formatterOptions=-source",
            "-XDdiags.layout=%L%m|%L%m|%L%m",
            "-XDbreakDocCommentParsingOnError=false",
            "-Xlint:cast",
            "-Xlint:deprecation",
            "-Xlint:empty",
            "-Xlint:fallthrough",
            "-Xlint:finally",
            "-Xlint:path",
            "-Xlint:unchecked",
            "-Xlint:varargs",
            "-Xlint:static",
            "-proc:none"
        )
    }
    private var diagnostics = DiagnosticCollector<JavaFileObject>()
    private val tool: JavacTool by lazy { JavacTool.create() }
    private val standardFileManager: JavacFileManager by lazy {
        tool.getStandardFileManager(
            diagnostics, Locale.getDefault(), Charset.defaultCharset()
        )
    }
    init {
        standardFileManager.setLocation(
            StandardLocation.PLATFORM_CLASS_PATH,
            project.classpathDir.walk().toList()
        )
        if (!project.binDir.exists()) {
            project.binDir.mkdirs()
        }
        standardFileManager.setLocation(StandardLocation.CLASS_OUTPUT, listOf(project.binDir))
    }
    override fun analyze() {
        with(standardFileManager) {
            setLocation(StandardLocation.CLASS_PATH, getClasspath())
            autoClose = false
        }

        val newArgs = args.toMutableList()
        newArgs.apply {
            add("-source")
            add(options.sourceVersion)
            add("-target")
            add(options.targetVersion)
        }

        tool.getTask(System.out.writer(), standardFileManager, diagnostics, newArgs, null, getSourceFiles())
            .apply {
                parse()
                analyze()
                if (diagnostics.diagnostics.isEmpty()) {
                    try {
                        generate().forEach(JavaAnalyzeCache::saveCache)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
    }
    override fun reset() {
        diagnostics = DiagnosticCollector<JavaFileObject>()
    }

    override fun getProblems(): List<DiagnosticRegion> {
        val diagnostics = diagnostics.diagnostics
        val problems = mutableListOf<DiagnosticRegion>()
        try {
            diagnostics.forEachIndexed { index, diagnostic ->
                if (diagnostic.source == null) return@forEachIndexed
                val severity =
                    when (diagnostic.kind) {
                        Diagnostic.Kind.ERROR -> DiagnosticRegion.SEVERITY_ERROR
                        Diagnostic.Kind.WARNING,
                        Diagnostic.Kind.MANDATORY_WARNING -> DiagnosticRegion.SEVERITY_WARNING
                        else -> DiagnosticRegion.SEVERITY_TYPO
                    }
                if (diagnostic.code == "compiler.err.cant.resolve.location") {
//                    val symbol = diagnostic.source.getCharContent(true)
//                        .substring(diagnostic.startPosition.toInt(), diagnostic.endPosition.toInt())
                }
                problems.add(
                    DiagnosticRegion(
                        diagnostic.startPosition.toInt(),
                        diagnostic.endPosition.toInt(),
                        severity,
                        0,
                        diagnosticDetail(diagnostic)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return problems
    }

    private fun getClasspath(): List<File> {
        val classpath = mutableListOf<File>()
        classpath.add(File(project.binDir, "classes"))
        project.libsDir.walk().forEach {
            if (it.extension == "jar") {
                classpath.add(it)
            }
        }
        project.binDir
            .resolve("classes")
            .walk()
            .filter { it.extension == "class" }
            .forEach {
                if (JavaAnalyzeCache.getCache(it) != null && JavaAnalyzeCache.getCache(it)!!.lastModified == it.lastModified()) {
                    classpath.add(it)
                }
            }
        return classpath
    }

    private fun getSourceFiles(): List<JavaFileObject> {
        val sourceFiles = mutableListOf<JavaFileObject>()
        project.srcDir.walk().forEach {
            if (it.extension == "java") {
                val cache = JavaAnalyzeCache.getCache(it)
                if (cache == null || cache.lastModified < it.lastModified()) {
                    sourceFiles.add(JavaAnalyzeCache.saveCache(it))
                }
            }
        }
        return sourceFiles
    }
}