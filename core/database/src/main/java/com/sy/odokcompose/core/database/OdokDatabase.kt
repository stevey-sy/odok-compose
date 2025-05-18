package com.sy.odokcompose.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sy.odokcompose.core.database.entity.BookEntity

@Database(
    entities = [BookEntity::class],
    version = 1,
    exportSchema = true
)
abstract class OdokDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    
    companion object {
        private const val DATABASE_NAME = "odok-database"
        
        @Volatile
        private var INSTANCE: OdokDatabase? = null
        
        fun getInstance(context: Context): OdokDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OdokDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 