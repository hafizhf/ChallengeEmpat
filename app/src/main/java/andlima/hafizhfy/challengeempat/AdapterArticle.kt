/*
* RecyclerView adapter for recyclerview at fragment_home.xml
*/

package andlima.hafizhfy.challengeempat

import andlima.hafizhfy.challengeempat.articletable.Article
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_article_dialog.view.*
import kotlinx.android.synthetic.main.item_adapter_article.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class AdapterArticle(val listArticle : List<Article>)
    : RecyclerView.Adapter<AdapterArticle.ViewHolder>() {

    // Get database --------------------------------------------------------------------------------
    private var mDb : ArticleManagementDatabase? = null

    // ViewHolder ----------------------------------------------------------------------------------
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterArticle.ViewHolder {
        // Get item for RecyclerView
        val viewItem = LayoutInflater.from(parent.context).inflate(
            R.layout.item_adapter_article,
            parent,
            false
        )
        return ViewHolder(viewItem)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AdapterArticle.ViewHolder, position: Int) {
        // Put every one row data to one item with TextView at item_adapter_article.xml
        holder.itemView.tv_article_title.text = listArticle[position].title
        holder.itemView.tv_article_content.text = listArticle[position].content?.take(80) + "..."
        holder.itemView.tv_article_author.append(listArticle[position].author?.capitalize())

        // Action for selected/clicked item --------------------------------------------------------
        holder.itemView.item.setOnClickListener {
            mDb = ArticleManagementDatabase.getInstance(it.context)

            // Using DialogFragment
            val editDialog = EditArticleDialogFragment()

            // Send data to DialogFragment and show it
            val data = bundleOf("SELECTED_DATA" to listArticle[position])
            editDialog.arguments = data
            editDialog.show((it.context as MainActivity).supportFragmentManager, "edit_article")
        }

        // Delete button action --------------------------------------------------------------------
        holder.itemView.btn_delete_article.setOnClickListener {
            // Get database instance
            mDb = ArticleManagementDatabase.getInstance(it.context)

            // Show Dialog with two action
            AlertDialog.Builder(it.context)
                .setTitle("Delete Article")
                .setMessage("Are you sure want to delete this article?")
                .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->

                    GlobalScope.async {
                        // Delete selected data
                        val deleteArticle = mDb?.articleDao()?.deleteArticle(listArticle[position])

                        // Show Toast after delete action
                        (holder.itemView.context as MainActivity).runOnUiThread {
                            if (deleteArticle != 0) {
                                Toast.makeText(
                                    it.context,
                                    "Article deleted",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                                refreshWithIntent(it.context, holder.itemView.context)

                            } else {
                                Toast.makeText(
                                    it.context,
                                    "Delete article failed",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
                }
                .setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
                    // Dismiss Dialog if user cancel delete action
                    dialogInterface.dismiss()
                }
                .show()
        }

        // Edit button action ----------------------------------------------------------------------
        holder.itemView.btn_edit_article.setOnClickListener {
            // Using DialogFragment
            val editDialog = EditArticleDialogFragment()

            // Send data to DialogFragment and show it
            val data = bundleOf("SELECTED_DATA" to listArticle[position])
            editDialog.arguments = data
            editDialog.show((it.context as MainActivity).supportFragmentManager, "edit_article")
        }
    }

    override fun getItemCount(): Int {
        // Get data size
        return listArticle.size
    }

    // Function for instant refresh to get newest data on RecyclerView -----------------------------
    private fun refreshWithIntent(context1: Context, context2: Context) {
        val refreshWithIntent = Intent(context1, MainActivity::class.java)
        context1.startActivity(refreshWithIntent)
        (context2 as MainActivity).overridePendingTransition(0,0)
        context2.finish()
    }
}