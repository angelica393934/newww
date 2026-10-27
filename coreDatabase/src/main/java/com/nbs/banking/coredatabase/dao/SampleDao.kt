package com.nbs.banking.coredatabase.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.nbs.banking.coredatabase.entitiy.SampleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SampleDao {
    @Query("SELECT * FROM sample_notes")
    fun getAll(): Flow<List<SampleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SampleEntity): Long

    @Query("DELETE FROM sample_notes WHERE id = :id")
    suspend fun delete(id: Long)
}