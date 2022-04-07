package andlima.hafizhfy.challengeempat

import andlima.hafizhfy.challengeempat.articletable.Article
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.add_article_dialog.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_adapter_article.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    // Get database
    private var mDb : ArticleManagementDatabase? = null

    // Get shared preferences
    private val sharedPrefFile = "logininfo"

    // Used for double back to exit app
    private var doubleBackToExit = false

    // Used to show action option
    private var showOption = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if user click back button twice
        doubleBackExit()

        // Get database instance
        mDb = ArticleManagementDatabase.getInstance(requireContext())

        // Get something from shared preference
        val sharedPreferences : SharedPreferences =
            requireContext().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

//        tv_title.append(sharedPreferences.getString("email_key", "email_key"))
        val username = sharedPreferences.getString("name_key", "name_key")
        tv_greeting.append(username?.capitalize())

        btn_logout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure want to logout?")
                .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()

                    Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_loginFragment)
                    toast("You're logged out")
                }
                .setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                }
                .setCancelable(false)
                .show()
        }


        btn_show_option.setOnClickListener {
            isOptionShow()
        }

        fab_add_new_article.setOnClickListener {
            val newArticleDialog = LayoutInflater.from(requireContext()).inflate(
                R.layout.add_article_dialog, null, false
            )

            val alert = AlertDialog.Builder(requireContext())
                .setView(newArticleDialog)
                .create()

            newArticleDialog.btn_cancel.setOnClickListener {
                alert.dismiss()
            }

            newArticleDialog.btn_submit_article.setOnClickListener {
                GlobalScope.async {
                    val title = newArticleDialog.et_new_title.text.toString()
                    val content = newArticleDialog.et_new_content.text.toString()

                    val submit = mDb?.articleDao()?.insertArticle(Article(null, username, title, content))

                    (requireContext() as MainActivity).runOnUiThread {
                        if (submit != 0.toLong()) {
                            toast("New article submitted")
                            alert.dismiss()
                            onDestroy()
                            onResume()
                        } else {
                            toast("Submit failed")
                        }
                    }
                }
            }

            alert.show()
        }
    }

    fun getDataArticle() {
        rv_itemlist.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        GlobalScope.launch {
            val listData = mDb?.articleDao()?.getAllArticle()

            if (listData?.size!! > 0) {
                nothing_handler.visibility = View.GONE

                (requireContext() as MainActivity).runOnUiThread {
                    listData.let {
                        val adapter = AdapterArticle(it)
                        rv_itemlist.adapter = adapter
                    }
                }
            } else {
                nothing_handler.visibility = View.VISIBLE
            }

        }
    }

    // Function untuk menutup fragment setelah navigate agar tidak bisa back
    private fun closeFragment() {
        activity?.fragmentManager?.popBackStack()
    }

    // Function to exit app with double click on back button
    private fun doubleBackExit() {
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (doubleBackToExit) {
                    activity!!.finish()
                } else {
                    doubleBackToExit = true
                    Toast.makeText(requireContext(), "Press again to exit", Toast.LENGTH_SHORT).show()

                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        kotlin.run {
                            doubleBackToExit = false
                        }
                    }, 2000)
                }
            }
        })
    }

    // Function to easy making Toast
    private fun toast(message : String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onResume() {
        super.onResume()
        getDataArticle()
    }

    override fun onDestroy() {
        super.onDestroy()
        ArticleManagementDatabase.destroyInstance()
    }

    fun isOptionShow() {
        if (!showOption) {
            container_action.visibility = View.VISIBLE
            showOption = true
        } else {
            container_action.visibility = View.GONE
            showOption = false
        }
    }
}