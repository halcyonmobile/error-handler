@file:Suppress("UnstableApiUsage")

package com.halcyonmobile.errorhandlerlint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API

internal const val PRIORITY = 10 // Does not matter anyways within Lint.

class IssueRegistry : IssueRegistry() {
    override val api = CURRENT_API

    override val issues
        get() = listOf(
            LoggableInterfaceDetector.ISSUE,
            MissingParsedErrorDetector.ISSUE
        )
}