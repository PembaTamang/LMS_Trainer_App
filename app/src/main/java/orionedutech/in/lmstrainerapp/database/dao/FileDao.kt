package orionedutech.`in`.lmstrainerapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import orionedutech.`in`.lmstrainerapp.database.entities.AppFiles

@Dao
interface FileDao {

    @Insert
    suspend fun insert(file: AppFiles)

}