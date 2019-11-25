package orionedutech.`in`.lmstrainerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assessment_questions_table")
data class AssesmentQuestion(
    val assesment_question: String,
    val assesment_question_id: String,
    val assesment_id: String,
    val question_type: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}