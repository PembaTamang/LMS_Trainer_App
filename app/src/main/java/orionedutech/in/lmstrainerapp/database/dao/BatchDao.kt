package orionedutech.`in`.lmstrainerapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import orionedutech.`in`.lmstrainerapp.database.entities.Batch

@Dao
interface BatchDao{

    @Insert
    suspend fun insert(batch : MutableList<Batch>)

    @Query("delete from batch_table")
    suspend fun deleteBatchTable()

    @Query(" select * from batch_table ")
    suspend fun getAllBatches() :List<Batch>

    @Query("select batch_name from batch_table where batch_id = :batch_id")
    suspend fun getBatchName(batch_id:String) : String

    @Query("select count(*) from batch_table")
    suspend fun getTableCount(): Int


    @Transaction
    suspend fun insertBatches(batch: MutableList<Batch>){
        deleteBatchTable()
        insert(batch)
    }

    @Transaction
    suspend fun batchDataExists(): Boolean {
        return getTableCount()>0
    }
}