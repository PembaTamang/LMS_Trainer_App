package orionedutech.`in`.lmstrainerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_questions")
data class ActivityQuestions(
    val activity_qns_id: String,
    val activity_qns_language: String,
    val activity_qns_type: String,
    val activity_qns_value: String,
    val activity_id : String
){
    @PrimaryKey(autoGenerate = true)
    var id1: Int = 0
}