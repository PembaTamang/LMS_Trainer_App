package orionedutech.`in`.lmstrainerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_answers")
data class ActivityAnswers(
    val answer_right_wrong: String,
    val answer_value: String,
    val question_ans_id: String,
    val question_id: String,
    val activity_id:String
){
    @PrimaryKey(autoGenerate = true)
    var id1: Int = 0
}