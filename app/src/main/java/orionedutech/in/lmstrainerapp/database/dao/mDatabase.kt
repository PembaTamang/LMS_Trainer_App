package orionedutech.`in`.lmstrainerapp.database.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import orionedutech.`in`.lmstrainerapp.database.entities.User
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration



@Database(entities = [User::class], version = 1)
abstract class mDatabase : RoomDatabase() {
    abstract fun getDao(): MDao

    companion object {
        @Volatile
        private var instance: mDatabase? = null
        private val Lock = Any()
        operator fun invoke(c: Context) = instance ?: synchronized(Lock) {
            instance ?: buildDB(c).also {
                instance = it
            }
        }

        private fun buildDB(c: Context): mDatabase {
            val dbName = "lms_database.db"
            return Room.databaseBuilder(c.applicationContext, mDatabase::class.java, dbName)
                .addMigrations(migration)
                .build()
        }

        private val migration: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
            }
        }
    }
}
