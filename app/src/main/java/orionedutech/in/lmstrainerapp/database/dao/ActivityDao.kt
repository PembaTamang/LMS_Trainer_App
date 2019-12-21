package orionedutech.`in`.lmstrainerapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import orionedutech.`in`.lmstrainerapp.database.entities.Activities

@Dao
interface ActivityDao {

    @Insert
    suspend fun ins(activity: MutableList<Activities>)

    @Query("delete from activity_table")
    suspend fun clearActivityTable()

    @Transaction
    suspend fun insert(activity: MutableList<Activities>) {
        clearActivityTable()
        ins(activity)
    }

    @Query("select activity_name from activity_table where activity_id = :aid")
    suspend fun getActivityName(aid: String): String

    @Query("select * from activity_table where chapter_id = :cid")
    suspend fun getAllByCid(cid: String): List<Activities>

    @Transaction
    suspend fun getActivityIDs(cid: String): List<String>{
      val list =  getAllByCid(cid)
       val newlist : ArrayList<String> = ArrayList()
        list.forEach {
            newlist.add(it.activity_id)
        }
        return newlist
    }
}