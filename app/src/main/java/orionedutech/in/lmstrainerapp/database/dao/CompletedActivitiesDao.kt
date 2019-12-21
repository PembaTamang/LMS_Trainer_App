package orionedutech.`in`.lmstrainerapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import orionedutech.`in`.lmstrainerapp.database.entities.CompletedActivity

@Dao
interface CompletedActivitiesDao {
@Insert
suspend fun insert(data : CompletedActivity)

@Query("select count(*) from completed_activities where activityID = :aid and chapterID =:cid")
suspend fun getActivityCount(aid: String,cid:String) : Int

@Transaction
suspend fun hasActivity(aid: String,aix:String):Boolean{
    return getActivityCount(aid,aix) != 0
}

}