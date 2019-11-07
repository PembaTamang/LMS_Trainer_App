package orionedutech.`in`.lmstrainerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
val userID:String?,
val userName:String?,
val userRoleName:String?,
val adminID:String?,
val name:String?,
val email:String?,
val phoneNumber:String?,
val adminType:String?,
val batchID:String?,
val centerID:String?,
val batchName:String?,
val centerName:String?,
val password:String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}