package org.readium.r2.testapp.db

/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


class DataStorePrefs {

    companion object {
        @Volatile
        private var INSTANCE: DataStore<Preferences>? = null

        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

        fun getDataStorePrefs(context: Context): DataStore<Preferences> {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = context.dataStore
                INSTANCE = instance
                return instance
            }
        }
    }
}