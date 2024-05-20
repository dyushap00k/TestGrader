package com.testgrader.ui

import android.view.View
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> invalidatedState(initial: T) = InvalidatedState(initial)

class InvalidatedState<T>(initial: T) : ReadWriteProperty<View, T> {
    private var value = initial

    override fun getValue(thisRef: View, property: KProperty<*>): T = value

    override fun setValue(thisRef: View, property: KProperty<*>, value: T) {
        this.value = value
        thisRef.invalidate()
    }
}