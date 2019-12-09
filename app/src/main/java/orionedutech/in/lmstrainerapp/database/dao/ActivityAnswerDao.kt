package orionedutech.`in`.lmstrainerapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import orionedutech.`in`.lmstrainerapp.database.entities.ActivityAnswers

@Dao
interface ActivityAnswerDao {
    suspend fun ins(activity: MutableList<ActivityAnswers>)

    @Query("delete from activity_answers")
    suspend fun clearAnswersTable()

    @Transaction
    suspend fun insert(activity: MutableList<ActivityAnswers>){
      clearAnswersTable()
        ins(activity)
    }

    @Query(" select * from activity_answers where question_id = :qid ")
    suspend fun getAllAssesmentAnswers(qid: String) :List<ActivityAnswers>

    @Query("select answer_right_wrong from activity_answers where question_id = :qid and question_ans_id =:aid")
    suspend fun getAnswerValue(qid: String,aid : String) : String

}