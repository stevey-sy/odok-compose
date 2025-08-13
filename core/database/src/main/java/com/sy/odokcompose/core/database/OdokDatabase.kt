package com.sy.odokcompose.core.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sy.odokcompose.core.database.entity.BookEntity
import com.sy.odokcompose.core.database.entity.BookTagCrossRef
import com.sy.odokcompose.core.database.entity.MemoEntity
import com.sy.odokcompose.core.database.entity.MemoTagCrossRef
import com.sy.odokcompose.core.database.entity.TagEntity
import com.sy.odokcompose.core.database.entity.UserEntity
import com.sy.odokcompose.core.database.entity.AuthTokenEntity
import com.sy.odokcompose.core.database.dao.UserDao
import com.sy.odokcompose.core.database.dao.AuthTokenDao

@Database(
    entities = [
        BookEntity::class, 
        MemoEntity::class, 
        TagEntity::class, 
        MemoTagCrossRef::class,
        BookTagCrossRef::class,
        UserEntity::class,
        AuthTokenEntity::class
    ],
    version = 3, // UUID 전환으로 버전 업
    exportSchema = true,
)
abstract class OdokDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun memoDao(): MemoDao
    abstract fun tagDao(): TagDao
    abstract fun userDao(): UserDao
    abstract fun authTokenDao(): AuthTokenDao
    
    companion object {
        private const val DATABASE_NAME = "odok-database"
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE books ADD COLUMN finishedReadCnt INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // UUID 전환 마이그레이션은 복잡하므로 개발 단계에서는 fallbackToDestructiveMigration 사용
                // 실제 배포 시에는 데이터 보존을 위한 상세한 마이그레이션 구현 필요
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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 