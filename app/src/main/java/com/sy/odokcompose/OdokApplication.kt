package com.sy.odokcompose

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sy.odokcompose.core.database.export.DatabaseExporter
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "settings")

@HiltAndroidApp
class OdokApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    @Inject
    lateinit var databaseExporter: DatabaseExporter
    
    override fun onCreate() {
        super.onCreate()
        
        applicationScope.launch {
            val isFirstRun = dataStore.data.first()[IS_FIRST_RUN] ?: true
            if (isFirstRun) {
                // 더미 데이터 가져오기
                databaseExporter.importDummyData()
                
                // 최초 실행 플래그 업데이트
                dataStore.edit { preferences ->
                    preferences[IS_FIRST_RUN] = false
                }
            }
        }
    }
    
    companion object {
        private val IS_FIRST_RUN = booleanPreferencesKey("is_first_run")
    }
} 