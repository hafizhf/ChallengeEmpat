package andlima.hafizhfy.challengeempat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    // Get database
    private var mDb : ArticleManagementDatabase? = null

    // Get shared preferences
    private val sharedPrefFile = "logininfo"

    // Used for double back to exit app
    private var doubleBackToExit = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
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

        // Check if user already logged in
        isLoggedIn(sharedPreferences)

        // Show password
        btn_show_pwd.setOnClickListener {
            showPassword(et_password)
        }

        // Login button action
        btn_login.setOnClickListener {
            val email = et_email.text.toString()
            val password = et_password.text.toString()

            // Hide error pop up
            hidePopUp(cv_email_popup)
            hidePopUp(cv_password_popup)

            GlobalScope.launch {
                val findUser = mDb?.userDao()?.findUser(email)

                (requireContext() as MainActivity).runOnUiThread {

                    when {
                        (email == "") && (password == "") -> {
                            showPopUp(
                                cv_email_popup,
                                tv_email_popup,
                                "Fill all field to login"
                            )
//                            toast("Input your email and password to login")
                        }
                        findUser?.size == 0 -> {
                            showPopUp(
                                cv_email_popup,
                                tv_email_popup,
                                "Email not registered"
                            )
//                            toast("Email not registered")
                        }
                        password == "" -> {
                            showPopUp(
                                cv_password_popup,
                                tv_password_popup,
                                "Please input password to login"
                            )
//                            toast("Please input password to login")
                        }
                        password != findUser?.get(0)?.password -> {
                            showPopUp(
                                cv_password_popup,
                                tv_password_popup,
                                "Wrong Password"
                            )
//                            toast("Wrong Password")
                        }
                        else -> {
                            val editor : SharedPreferences.Editor = sharedPreferences.edit()

                            editor.putString("name_key", findUser[0].name)
                            editor.putString("email_key", findUser[0].email)
                            editor.putString("pwd_key", findUser[0].password)
                            editor.apply()

                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment)
//                            onDestroy()
                        }
                    }
                }
            }
        }

        // Go to Register fragment button action
        btn_goto_register.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    // Function to show password on password EditText
    private fun showPassword(editText: EditText) {
        val hidden = PasswordTransformationMethod.getInstance()
        val show = HideReturnsTransformationMethod.getInstance()

        if (editText.transformationMethod == hidden) {
            editText.transformationMethod = show
        } else {
            editText.transformationMethod = hidden
        }
    }

    // Function to easy making Toast
    private fun toast(message : String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    // Function to check is user logged in
    private fun isLoggedIn(sharedPreferences: SharedPreferences) {
        val sharedEmail = sharedPreferences.getString("email_key", "email_key")
        val sharedPassword = sharedPreferences.getString("pwd_key", "pwd_key")
        if (sharedEmail != "email_key" && sharedPassword != "pwd_key") {
            view?.let {
                Navigation.findNavController(it).navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }
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

    // Function to show error pop up
    private fun showPopUp(cardViewID: CardView, textViewID: TextView, message: String) {
        cardViewID.visibility = View.VISIBLE
        textViewID.text = message
    }

    // Function to hide error pop up
    private fun hidePopUp(cardViewID: CardView) {
        cardViewID.visibility = View.GONE
    }
}