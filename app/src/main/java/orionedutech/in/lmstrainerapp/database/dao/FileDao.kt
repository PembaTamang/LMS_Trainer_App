package orionedutech.`in`.lmstrainerapp.database.dao

import androidx.room.*
import orionedutech.`in`.lmstrainerapp.database.entities.AppFiles
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG

@Dao
interface FileDao {

    @Insert
    fun insert(file: AppFiles)

    @Query("select serverUrl from file_table where serverUrl = :url")
    suspend fun getServerUrl(url: String): String

    @Transaction
    suspend fun urlExists(url: String): Boolean {
        val mUrl = getServerUrl(url)
        mLog.i(TAG, "url $mUrl")
        if (mUrl == null) {
            mLog.i(TAG, "returning false")
            return false
        }

        mLog.i(TAG, "returning true")
        return true
    }

    @Query("select internal_Path from file_table where serverUrl = :serverPath")
    suspend fun getInternalPath(serverPath : String) : String

    @Update fun update(file: AppFiles)

    @Transaction
    suspend fun insertFileData(file: AppFiles){
        val url : String  = file.serverUrl!!
        mLog.i(TAG,"server url $url")
        if(urlExists(url)){
            mLog.i(TAG,"updating")
            update(file)
        }else{
            mLog.i(TAG,"inserting")
            insert(file)
        }
    }

}