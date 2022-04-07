package andlima.hafizhfy.challengeempat

import andlima.hafizhfy.challengeempat.articletable.Article
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_edit_article_dialog.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class EditArticleDialogFragment : DialogFragment() {

    private var mDb : ArticleManagementDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_article_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDb = ArticleManagementDatabase.getInstance(requireContext())

        val selectedData = arguments?.getParcelable<Article>("SELECTED_DATA")

        et_edit_title.setText(selectedData?.title)
        et_edit_content.setText(selectedData?.content)

        btn_save_edit.setOnClickListener {
            selectedData?.title = et_edit_title.text.toString()
            selectedData?.content = et_edit_content.text.toString()

            GlobalScope.async {
                val doEdit = selectedData?.let { it1 -> mDb?.articleDao()?.updateArticle(it1) }

                (requireContext() as MainActivity).runOnUiThread {
                    if (doEdit != 0) {
                        Toast.makeText(requireContext(), "Article updated", Toast.LENGTH_LONG).show()
                        refreshWithIntent(requireContext(), requireContext())
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Update article failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun refreshWithIntent(context1: Context, context2: Context) {
        val refreshWithIntent = Intent(context1, MainActivity::class.java)
        context1.startActivity(refreshWithIntent)
        (context2 as MainActivity).overridePendingTransition(0,0)
        context2.finish()
    }
}