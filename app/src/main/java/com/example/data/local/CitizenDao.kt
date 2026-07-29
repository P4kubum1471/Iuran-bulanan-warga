package com.example.data.local

import androidx.room.*
import com.example.data.model.Citizen
import kotlinx.coroutines.flow.Flow

@Dao
interface CitizenDao {
    @Query("SELECT * FROM citizens ORDER BY name ASC")
    fun getAllCitizens(): Flow<List<Citizen>>

    @Query("SELECT * FROM citizens WHERE id = :id")
    suspend fun getCitizenById(id: Long): Citizen?

    @Query("SELECT * FROM citizens WHERE name LIKE '%' || :query || '%' OR houseNumber LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCitizens(query: String): Flow<List<Citizen>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitizen(citizen: Citizen): Long

    @Update
    suspend fun updateCitizen(citizen: Citizen)

    @Query("DELETE FROM citizens WHERE id = :id")
    suspend fun deleteCitizenById(id: Long)

    @Delete
    suspend fun deleteCitizen(citizen: Citizen)

    @Query("DELETE FROM citizens")
    suspend fun deleteAllCitizens()
}
