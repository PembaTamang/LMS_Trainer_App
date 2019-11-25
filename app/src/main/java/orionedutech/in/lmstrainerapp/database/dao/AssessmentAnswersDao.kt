package orionedutech.`in`.lmstrainerapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import orionedutech.`in`.lmstrainerapp.database.entities.AssesmentAnswers

@Dao
interface AssessmentAnswersDao {
    @Insert
    suspend fun insert(answers: MutableList<AssesmentAnswers>)

    @Query("delete from assessment_answer_table")
    suspend fun deleteAssessmentAnswersTable()

    @Query(" select * from assessment_answer_table where question_id = :qid ")
    suspend fun getAllAssesmentAnswers(qid: String) :List<AssesmentAnswers>

    @Transaction
    suspend fun insertAssessmentAnswers(answers: MutableList<AssesmentAnswers>){
        deleteAssessmentAnswersTable()
        insert(answers)
    }

    @Query("select count(*) from assessment_answer_table")
    suspend fun getTableCount(): Int

    @Transaction
    suspend fun batchDataExists(): Boolean {
        return getTableCount()>0
    }

    @Query("select answer_right_wrong from assessment_answer_table where question_id = :qid and question_ans_id =:aid")
    suspend fun getAnswerValue(qid: String,aid : String) : String


}