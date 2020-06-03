@file:Suppress("UnstableApiUsage")

package com.halcyonmobile.errorhandlerlint

import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UClass
import java.util.EnumSet

val LOGGABLE_MISUSE_ISSUE = Issue.create(
    id = "LoggableMisuse",
    briefDescription = "Loggable is for RemoteException",
    explanation = "Loggable interface is meant to be used for with RemoteException only!",
    category = CORRECTNESS,
    priority = PRIORITY,
    severity = Severity.WARNING,
    implementation = Implementation(LoggableInterfaceDetector::class.java, EnumSet.of(Scope.JAVA_FILE))
)

class LoggableInterfaceDetector : Detector(), Detector.UastScanner {
    companion object {
        private const val LOGGABLE_INTERFACE = "com.halcyonmobile.errorhandlerrest.logger.Loggable"
        private const val NETWORK_EXCEPTION_CLASS = "com.halcyonmobile.errorhandlerrest.exception.RemoteException"
    }

    override fun applicableSuperClasses(): List<String>? = listOf(
        LOGGABLE_INTERFACE,
        NETWORK_EXCEPTION_CLASS
    )

    override fun visitClass(context: JavaContext, declaration: UClass) {

        var isLoggable = false
        var isNetworkException = false
        for (uastSuperType in declaration.uastSuperTypes) {
            val qualifiedName = uastSuperType.getQualifiedName()
            if (qualifiedName == LOGGABLE_INTERFACE) {
                isLoggable = true
            }
            if (qualifiedName == NETWORK_EXCEPTION_CLASS) {
                isNetworkException = true
            }
        }
        if (isLoggable && !isNetworkException) {
            reportMisuseOfLoggableInterface(context, declaration)
        }
    }

    private fun reportMisuseOfLoggableInterface(context: JavaContext, uClass: UClass) {
        context.report(
            LOGGABLE_MISUSE_ISSUE,
            uClass,
            context.getNameLocation(uClass),
            "Loggable is meant to be used on RemoteException only!"
        )
    }
}