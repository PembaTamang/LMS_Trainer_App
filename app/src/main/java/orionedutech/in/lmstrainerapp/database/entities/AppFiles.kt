package orionedutech.`in`.lmstrainerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_table")
data class AppFiles(
    val interna_Path: String? ,
    val external_Path : String?,
    val file_name: String?
){
    @PrimaryKey(autoGenerate = true)
    var id1: Int? = 0
}