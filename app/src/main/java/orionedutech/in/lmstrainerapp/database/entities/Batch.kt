package orionedutech.`in`.lmstrainerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "batch_table")
data class Batch(
    val batch_id: String?,
    val batch_name: String?,
    val user_m_id: String?,
    val user_profile_id: String?
){
    @PrimaryKey(autoGenerate = true)
    var id1: Int? = 0
}