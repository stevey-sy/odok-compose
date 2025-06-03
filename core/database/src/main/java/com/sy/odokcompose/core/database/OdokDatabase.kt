package com.sy.odokcompose.core.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sy.odokcompose.core.database.entity.BookEntity

@Database(
    entities = [BookEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class OdokDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    
    companion object {
        private const val DATABASE_NAME = "odok-database"
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE books ADD COLUMN finishedReadCnt INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        @Volatile
        private var INSTANCE: OdokDatabase? = null
        
        fun getInstance(context: Context): OdokDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OdokDatabase::class.java,
                    DATABASE_NAME
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 