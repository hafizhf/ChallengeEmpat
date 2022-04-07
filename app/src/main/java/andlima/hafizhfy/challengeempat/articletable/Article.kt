package andlima.hafizhfy.challengeempat.articletable

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

// Article table for ArticleManagement.db ----------------------------------------------------------
@Entity
@Parcelize
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id : Int?,

    @ColumnInfo(name = "author")
    val author : String?,

    @ColumnInfo(name = "title")
    var title : String?,

    @ColumnInfo(name = "content")
    var content : String?

) : Parcelable
