package orionedutech.`in`.lmstrainerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_table")
data class Activities (
    val activity_chapter_id: String,
    val activity_description: String?,
    val activity_id: String,
    val activity_name: String
){
    @PrimaryKey(autoGenerate = true)
    var id1: Int = 0
}