package orionedutech.`in`.lmstrainerapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

import orionedutech.`in`.lmstrainerapp.database.entities.AssessmentMainData

@Dao
interface AssessmentMainDao {
    @Insert
    suspend fun insert(data: AssessmentMainData)

    @Query("delete from assessment_main_table")
    suspend fun deleteAssessmentMainTable()


    @Query(" select assesment_time from assessment_main_table where assesment_id = :aid ")
    suspend fun getAllAssessmentMainData(aid :String) : String


    @Transaction
    suspend fun insertAssessmentMainData(data:AssessmentMainData){
        deleteAssessmentMainTable()
        insert(data)
    }

    @Query("select count(*) from assessment_main_table")
    suspend fun getTableCount(): Int

    @Transaction
    suspend fun batchDataExists(): Boolean {
        return getTableCount()>0
    }
}