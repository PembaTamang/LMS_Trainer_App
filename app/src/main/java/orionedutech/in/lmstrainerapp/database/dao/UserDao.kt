package orionedutech.`in`.lmstrainerapp.database.dao

import androidx.room.*
import orionedutech.`in`.lmstrainerapp.database.entities.User

@Dao
interface UserDao {

    //user table

    @Insert
    suspend fun insert(user: User)

    @Query("select userID from user_table")
    suspend fun getuserID(): String

    @Query("select * from user_table")
    suspend fun getuserDetails(): User


    @Query("select userID from user_table")
    suspend fun getUserID(): String

    @Query("select adminID from user_table")
    suspend fun getAdminID(): String

    @Query("select batchName from user_table")
    suspend fun getBatchName(): String

    @Query("select centerID from user_table")
    suspend fun getCenterID(): String


    @Query("select name from user_table")
    suspend fun getadminName(): String

    @Query("select password from user_table")
    suspend fun getadminPassword(): String

    @Delete
    suspend fun deleteUser(user: User)

    @Query("delete from user_table")
    suspend fun deleteUserTable()


    @Query( "update user_table set password = :pwd ")
    suspend fun updatePassword(pwd : String)

    @Query("select count(*) from user_table")
    suspend fun getUserTableCount(): Int

    @Transaction
    suspend fun insertUser(user: User) {
        deleteUserTable()
        insert(user)
    }

    @Transaction
    suspend fun userDataExists(): Boolean {
        return getUserTableCount()>0
    }

}