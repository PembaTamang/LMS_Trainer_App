package orionedutech.`in`.lmstrainerapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.selects.select
import orionedutech.`in`.lmstrainerapp.database.entities.AssesmentQuestion


@Dao
interface AssessmentQuestionsDao {

    @Insert
    suspend fun insert(question: MutableList<AssesmentQuestion>)

    @Query("delete from assessment_questions_table")
    suspend fun deleteAssessmentQuestionsTable()

    @Query("select * from assessment_questions_table where assesment_id = :aid")
    suspend fun getQuestionsByAssessmentID(aid : String) : List<AssesmentQuestion>

    @Transaction
    suspend fun insertAssessmentQuestions(question: MutableList<AssesmentQuestion>){
        deleteAssessmentQuestionsTable()
        insert(question)
    }

    @Query("select count(*) from assessment_questions_table")
    suspend fun getTableCount(): Int

    @Transaction
    suspend fun batchDataExists(): Boolean {
        return getTableCount()>0
    }
}