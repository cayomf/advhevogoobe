package br.project_advhevogoober_final


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_user_reauthenticate.view.*

class UserReauthenticateFragment:Fragment() {

    val TAG ="UserReauthenticateFragment"
    private val user= FirebaseAuth.getInstance().currentUser!!


    override fun onAttach(context: Context) {
        Log.d(TAG,"onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate")
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG,"onCreateView")
        val view: View =inflater!!.inflate(R.layout.fragment_user_reauthenticate,container,false)
        view.btnReauthenticate.setOnClickListener{
            if(view.editTextREmail.text.toString().isNotEmpty() && view.editTextRPassword.text.toString().isNotEmpty()){
                var emailAuthProvider:AuthCredential=EmailAuthProvider.getCredential(view.editTextREmail.text.toString(),view.editTextRPassword.text.toString())
                user.reauthenticate(emailAuthProvider).addOnSuccessListener {
                    Toast.makeText(activity,R.string.voce_foi_reautenticado,Toast.LENGTH_LONG).show()
                    val manager = fragmentManager
                    val transaction = manager!!.beginTransaction()
                    val fragment = UserUpdateEmailFragment()
                    transaction.setCustomAnimations(R.anim.enter_left_to_right,R.anim.exit_left_to_right,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                    var f: Fragment? =manager.findFragmentById(R.id.nav_host_fragment)
                    if (f !is UserUpdateEmailFragment){
                        transaction.replace(R.id.nav_host_fragment, fragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                }.addOnFailureListener{
                    when (it) {
                        is FirebaseAuthInvalidUserException -> {
                            Toast.makeText(activity,R.string.email_ou_senha_incorretos,Toast.LENGTH_LONG).show()
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(activity,R.string.email_form_incorreto,Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(activity,R.string.erro_auth_desconhecido,Toast.LENGTH_LONG).show()
                        }
                    }
                }

            }
            else{
                Toast.makeText(activity,R.string.preencha_email_e_senha_corretamente,Toast.LENGTH_LONG).show()
            }
        }
        return view
    }
}