package orionedutech.`in`.lmstrainerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assessment_answer_table")
data class AssesmentAnswers(
    val answer_right_wrong: String,
    val answer_value: String,
    val question: String?,
    val question_ans_id: String,
    val question_id: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}