package andlima.hafizhfy.challengeempat

import andlima.hafizhfy.challengeempat.articletable.Article
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_article_detail_dialog.*

class ArticleDetailDialogFragment : DialogFragment() {

    private var mDb : ArticleManagementDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article_detail_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get data from AdapterArticle.kt ---------------------------------------------------------
        mDb = ArticleManagementDatabase.getInstance(requireContext())
        val selectedData = arguments?.getParcelable<Article>("SELECTED_DATA")

        // Put data to EditText (should be TextView) -----------------------------------------------
        et_read_title.setText(selectedData?.title)
        et_read_author.setText(selectedData?.author)
        et_read_content.setText(selectedData?.content)

    }
}