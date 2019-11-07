package orionedutech.`in`.lmstrainerapp.database.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import orionedutech.`in`.lmstrainerapp.database.entities.User
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
import orionedutech.`in`.lmstrainerapp.database.entities.Batch


@Database(entities = [User::class,Batch::class], version = 1,exportSchema = false)
abstract class MDatabase : RoomDatabase() {
    abstract fun getDao(): MDao

    abstract fun getDao1():BatchDao

    companion object {
        @Volatile
        private var instance: MDatabase? = null
        private val Lock = Any()
        operator fun invoke(c: Context) = instance ?: synchronized(Lock) {
            instance ?: buildDB(c).also {
                instance = it
            }
        }

        private fun buildDB(c: Context): MDatabase {
            val dbName = "lms_database.db"
            return Room.databaseBuilder(c.applicationContext, MDatabase::class.java, dbName)
                .addMigrations(migration)
                .build()
        }

        private val migration: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
    }
}