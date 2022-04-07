package andlima.hafizhfy.challengeempat.usertable

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

// User table for ArticleManagement.db -------------------------------------------------------------
@Entity
@Parcelize
data class User(
    @PrimaryKey(autoGenerate = true)
    var id : Int?,

    @ColumnInfo(name = "name")
    val name : String?,

    @ColumnInfo(name = "email")
    val email : String?,

    @ColumnInfo(name = "password")
    val password : String?
) : Parcelable
