package com.halcyonmobile.errorhandling.shared

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

open class Consumable<out T>(private val content: T) {

    var consumed = false
        private set

    fun consume(): T? {
        return if (consumed) {
            null
        } else {
            consumed = true
            content
        }
    }

    fun peek(): T = content
}

@MainThread
inline fun <T> LiveData<Consumable<T>>.observeEvent(owner: LifecycleOwner, crossinline observer: (T) -> Unit): Observer<Consumable<T>> {
    val wrappedObserver = Observer<Consumable<T>> { t ->
        t.consume()?.let { observer(it) }
    }
    observe(owner, wrappedObserver)
    return wrappedObserver
}
