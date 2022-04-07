package andlima.hafizhfy.challengeempat

import andlima.hafizhfy.challengeempat.articletable.Article
import andlima.hafizhfy.challengeempat.articletable.ArticleDao
import andlima.hafizhfy.challengeempat.usertable.User
import andlima.hafizhfy.challengeempat.usertable.UserDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Article::class,
        User::class
    ],
    version = 1
)
abstract class ArticleManagementDatabase : RoomDatabase() {

    abstract fun userDao() : UserDao
    abstract fun articleDao() : ArticleDao

    companion object{
        // Instance --------------------------------------------------------------------------------
        private var INSTANCE : ArticleManagementDatabase? = null
        fun getInstance(context : Context):ArticleManagementDatabase? {
            if (INSTANCE == null){
                synchronized(ArticleManagementDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ArticleManagementDatabase::class.java,"ArticleManagement.db").build()
                }
            }
            return INSTANCE
        }

        // Destroy Instance ------------------------------------------------------------------------
        fun destroyInstance(){
            INSTANCE = null
        }
    }
}