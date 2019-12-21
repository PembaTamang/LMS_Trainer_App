package orionedutech.`in`.lmstrainerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_activities")
data class CompletedActivity(
    val activityID : String,
    val chapterID : String
){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}