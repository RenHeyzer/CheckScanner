package com.example.checkscanner

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

const val PREFERENCES_NAME = "SCANNER_PREFERENCES"

class PreferencesHelper(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        const val USER_INN_KEY = "inn"
    }

    var userInn: String?
        set(value) = sharedPreferences.edit().putString(USER_INN_KEY, value).apply()
        get() = sharedPreferences.getString(USER_INN_KEY, "")
}