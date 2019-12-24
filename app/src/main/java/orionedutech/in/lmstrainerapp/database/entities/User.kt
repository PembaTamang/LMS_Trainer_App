package orionedutech.`in`.lmstrainerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
var userID:String?,
var userName:String?,
var userRoleName:String?,
var adminID:String? ,
var name:String? ,
var email:String?,
var phoneNumber:String?,
var adminType:String?,
var batchID:String?,
var centerID:String?,
var batchName:String?,
var centerName:String?,
var password:String?,
var pan:String?,
var aadhar:String?,
var user_doj:String?,
var user_dob:String?,
var last_qualification:String?,
var prof_qualification:String?,
var workExperice:String?,
var userprofileid: String?

) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}