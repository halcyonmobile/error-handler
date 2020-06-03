@file:Suppress("UnstableApiUsage")

package com.halcyonmobile.errorhandlerlint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope.JAVA_FILE
import com.android.tools.lint.detector.api.Severity.WARNING
import org.jetbrains.uast.UAnnotated
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import java.util.EnumSet

class MissingParsedErrorDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes() = listOf<Class<out UElement>>(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = MissingParsedErrorVisitor(context)

    class MissingParsedErrorVisitor(private val context: JavaContext) : UElementHandler() {
        override fun visitClass(node: UClass) {
            // Rather than checking each method, check only interfaces
            // Should be much faster this way.
            if (node.isInterface) {
                node.methods.forEach(::processAnnotations)
            }
        }

        private fun processAnnotations(method: UMethod) {
            val methodsAnnotations = context.evaluator.getAllAnnotations(method as UAnnotated, false)
                .mapNotNull { it.qualifiedName }
                .distinct()

            if (methodsAnnotations.isEmpty()) {
                return
            } else {
                val containsParsedAnnotation = PARSED_ERROR_REFERENCE in methodsAnnotations
                val containsRetrofitAnnotation = methodsAnnotations.any { it in RETROFIT_ANNOTATIONS }

                if (containsRetrofitAnnotation && !containsParsedAnnotation) {
                    reportMissingParsedAnnotations(method)
                }
            }
        }

        private fun reportMissingParsedAnnotations(element: UElement) {
            context.report(
                ISSUE,
                element,
                context.getNameLocation(element),
                "Missing @ParsedError annotation!",
                provideQuickfixForMissingParsedAnnotation(element)
            )
        }

        private fun provideQuickfixForMissingParsedAnnotation(method: UElement): LintFix = LintFix.create()
            .replace()
            .with("@ParsedError(::class)")
            .range(context.getLocation(method))
            .reformat(true)
            .beginning()
            .name("Add @ParsedError annotation")
            .robot(false)
            .build()
    }

    companion object {
        val RETROFIT_ANNOTATIONS = listOf(
            "retrofit2.http.GET",
            "retrofit2.http.PUT",
            "retrofit2.http.POST",
            "retrofit2.http.DELETE"
        )

        const val PARSED_ERROR_REFERENCE = "com.halcyonmobile.errorparsing.ParsedError"

        val ISSUE = Issue.create(
            id = "MissingParsedError",
            briefDescription = "Missing @ParsedError annotations on method",
            explanation = "Retrofit methods should be wrapped with @ParsedError annotation for a proper error handling.",
            category = CORRECTNESS,
            priority = PRIORITY,
            severity = WARNING,
            implementation = Implementation(MissingParsedErrorDetector::class.java, EnumSet.of(JAVA_FILE))
        )
    }
}