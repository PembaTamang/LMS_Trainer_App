package orionedutech.`in`.lmstrainerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assessment_main_table")
data class AssessmentMainData(
    val assesment_completed: String,
    val assesment_end_date: String,
    val assesment_start_date: String,
    val assesment_id: String,
    val assesment_name: String,
    val assesment_time: String

){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}