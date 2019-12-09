package orionedutech.`in`.lmstrainerapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import orionedutech.`in`.lmstrainerapp.database.entities.ActivityQuestions

@Dao
interface ActivityQuestionsDao {


    @Insert
    suspend fun ins(activity: MutableList<ActivityQuestions>)

    @Query("delete from activity_questions")
    suspend fun clearQuestionsTable()

    @Transaction
    suspend fun insert(activity: MutableList<ActivityQuestions>){
        clearQuestionsTable()
        ins(activity)
    }

    @Query("select activity_qns_id from activity_questions where activity_id = :aid")
    suspend fun getQuestionsByAcitivity(aid:String) : List<String>
}