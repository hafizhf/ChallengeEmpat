package andlima.hafizhfy.challengeempat.articletable

import androidx.room.*

// Article table interface to use SQLite query methods ---------------------------------------------
@Dao
interface ArticleDao {

    @Insert
    fun insertArticle(article: Article) : Long

    @Query("SELECT * FROM Article")
    fun getAllArticle() : List<Article>

    @Delete
    fun deleteArticle(article: Article) : Int

    @Update
    fun updateArticle(article: Article) : Int

    @Query("UPDATE Article SET title = :newTitle, content = :newContent WHERE id = :articleId")
    fun cobaUpdate(articleId : Int?, newTitle : String?, newContent : String?) : Int
}