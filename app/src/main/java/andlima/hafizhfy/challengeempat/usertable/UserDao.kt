package andlima.hafizhfy.challengeempat.usertable

import androidx.room.*

// Article table interface to use SQLite query methods ---------------------------------------------
@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User) : Long

    @Query("SELECT * FROM User")
    fun getAllUser() : List<User>

    @Query("SELECT * FROM User WHERE email = :email" )
    fun findUser(email : String) : List<User>

    @Delete
    fun deleteUser(user: User) : Int

    @Update
    fun updateUser(user: User) : Int
}