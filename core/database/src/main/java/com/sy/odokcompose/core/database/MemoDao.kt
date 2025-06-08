package com.sy.odokcompose.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.sy.odokcompose.core.database.entity.BookEntity
import com.sy.odokcompose.core.database.entity.MemoEntity
import com.sy.odokcompose.core.database.entity.MemoTagCrossRef
import com.sy.odokcompose.core.database.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    @Query("SELECT * FROM memos WHERE bookId = :bookId ORDER BY pageNumber ASC")
    fun getMemosByBookId(bookId: Int): Flow<List<MemoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemo(memo: MemoEntity): Long

    @Update
    suspend fun updateMemo(memo: MemoEntity)

    @Delete
    suspend fun deleteMemo(memo: MemoEntity)

    @Query("DELETE FROM memos WHERE bookId = :bookId")
    suspend fun deleteMemosByBookId(bookId: Int)

    @Query("SELECT * FROM memos WHERE memoId = :memoId")
    suspend fun getMemoById(memoId: Int): MemoEntity?

    @Query("DELETE FROM memos WHERE memoId = :memoId")
    suspend fun deleteMemoById(memoId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemoTagCrossRef(crossRef: MemoTagCrossRef)

    @Delete
    suspend fun deleteMemoTagCrossRef(crossRef: MemoTagCrossRef)

    @Transaction
    @Query("SELECT * FROM tags WHERE tagId IN (SELECT tagId FROM memo_tags WHERE memoId = :memoId)")
    fun getTagsForMemo(memoId: Long): Flow<List<TagEntity>>

    @Transaction
    @Query("SELECT * FROM memos WHERE memoId IN (SELECT memoId FROM memo_tags WHERE tagId = :tagId)")
    fun getMemosByTagId(tagId: Long): Flow<List<MemoEntity>>

    @Query("SELECT * FROM memos ORDER BY memoId DESC")
    fun getAllMemos(): Flow<List<MemoEntity>>
}