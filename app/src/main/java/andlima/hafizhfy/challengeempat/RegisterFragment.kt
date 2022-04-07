package andlima.hafizhfy.challengeempat

import andlima.hafizhfy.challengeempat.usertable.User
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class RegisterFragment : Fragment() {

    private var mDb : ArticleManagementDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDb = ArticleManagementDatabase.getInstance(requireContext())

        // Show password
        btn_show_new_pwd.setOnClickListener {
            showPassword(et_new_password)
        }

        // Show re-enter password
        btn_show_new_repwd.setOnClickListener {
           showPassword(et_reenter_password)
        }

        // Ketika registrasi sukses, bawa ke login supaya login pake akun baru
        btn_register.setOnClickListener {
            // Get input from EditText
            val name = et_new_name.text.toString()
            val email = et_new_email.text.toString()
            val password = et_new_password.text.toString()
            val repassword = et_reenter_password.text.toString()

            // Hide every popup
            hidePopUp(cv_name_popup)
            hidePopUp(cv_new_email_popup)
            hidePopUp(cv_re_pwd_popup)

            GlobalScope.async {
                val checkSameEmail = mDb?.userDao()?.findUser(email)

                when {
                    name == "" -> {
                        showPopUp(
                            cv_name_popup,
                            tv_name_popup,
                            "Fill all field to register"
                        )
                    }
                    checkSameEmail?.size!! > 0 -> {
                        showPopUp(
                            cv_new_email_popup,
                            tv_new_email_popup,
                            "Email already registered"
                        )
                    }
                    repassword != password -> {
                        showPopUp(
                            cv_re_pwd_popup,
                            tv_re_pwd_popup,
                            "Re-enter password not match"
                        )
                    }
                    else -> {
                        val result = mDb?.userDao()?.insertUser(User(null, name, email, repassword))

                        (requireContext() as MainActivity).runOnUiThread {
                            if (result != 0.toLong()) {
                                snackbar(it, "Register success, please login with new account")

                                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment)
                            }
                        }
                    }
                }
            }
        }

        // Ketika tidak jadi registrasi karena sudah punya akun
        btn_goto_login.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment)
//            closeFragment()
            onDestroy()
        }
    }

    // Function untuk menutup fragment setelah navigate agar tidak bisa back
    private fun closeFragment() {
        activity?.fragmentManager?.popBackStack()
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

    // Function to easy making SnackBar
    private fun snackbar(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackbar.setAction("OK") {
            snackbar.dismiss()
        }
        snackbar.show()
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