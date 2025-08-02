package com.vincent.android.cili.util

import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedPref<T>(
    private val context: Context,
    private val name: String,
    private val defValue: T,
    private val pref: String = "default",
    private val commit: Boolean = false
) : ReadWriteProperty<Any?, T> {

    private val prefs by lazy {
        context.getSharedPreferences(pref, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        findPreference(findProperName(property))

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
        putPreference(findProperName(property), value)

    private fun findProperName(property: KProperty<*>) = if (name.isEmpty()) property.name else name

    private fun findPreference(key: String): T = when (defValue) {
        is Int -> prefs.getInt(key, defValue)
        is Long -> prefs.getLong(key, defValue)
        is Float -> prefs.getFloat(key, defValue)
        is Boolean -> prefs.getBoolean(key, defValue)
        is String -> prefs.getString(key, defValue)
        else -> throw IllegalArgumentException("Unsupported type.")
    } as T

    private fun putPreference(key: String, value: T) {
        val edit = prefs.edit().apply {
            when (value) {
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                else -> throw IllegalArgumentException("Unsupported type.")
            }
        }
        if (commit) edit.commit() else edit.apply()
    }

}

