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

    @Query("select centerName from user_table")
    suspend fun getCenterName(): String

    @Query("select phoneNumber from user_table")
    suspend fun getPhone(): String

    @Query("select userprofileid from user_table")
    suspend fun getProfileID(): String

    @Query("select email from user_table")
    suspend fun getEmail(): String

    @Query("select batchID from user_table")
    suspend fun getBatchID(): String

    @Query("select centerID from user_table")
    suspend fun getCenterID(): String


    @Query("select name from user_table")
    suspend fun getadminName(): String

    @Query("select password from user_table")
    suspend fun getadminPassword(): String


    @Query("select pan from user_table")
    suspend fun getPan(): String


    @Query("select aadhar from user_table")
    suspend fun getAadhar(): String


    @Query("select user_dob from user_table")
    suspend fun getDob(): String

    @Query("select user_doj from user_table")
    suspend fun getDoj(): String

    @Query("select prof_qualification from user_table")
    suspend fun getPQualification(): String


    @Query("select last_qualification from user_table")
    suspend fun getLQualification(): String

    @Query("select workExperice from user_table")
    suspend fun getWorkExperience(): String

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
    @Update
    suspend fun update(user: User)

    @Transaction
   suspend fun updateProfileInfo(name:String,phone:String,center:String,email:String,dob:String){
        val user =getuserDetails()
        user.name = name
        user.centerName = center
        user.email = email
        user.user_dob = dob
        update(user)
    }

}