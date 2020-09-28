package br.project_advhevogoober_final

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import kotlinx.android.synthetic.main.fragment_user_update_email.*
import kotlinx.android.synthetic.main.fragment_user_update_email.view.*

class UserUpdateEmailFragment:Fragment() {

    val TAG ="UserUpdateEmailFragment"
    private val user= FirebaseAuth.getInstance().currentUser!!
    private lateinit var mPreferences: SharedPreferences
    private val PROFILE_TYPE_KEY:String="tipoP"
    private val mSharedPrefFile:String="br.project_advhevogoober_final"


    override fun onAttach(context: Context) {
        Log.d(TAG,"onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate")
        super.onCreate(savedInstanceState)
        mPreferences=this.activity!!.getSharedPreferences(mSharedPrefFile, Context.MODE_PRIVATE)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG,"onCreateView")
        val view: View =inflater!!.inflate(R.layout.fragment_user_update_email,container,false)
        view.btnSaveUpdatedEmail.setOnClickListener{
            if(eTxtNewEmail.text.toString().isNotEmpty()){
                user.updateEmail(eTxtNewEmail.text.toString()).addOnSuccessListener {
                    Toast.makeText(activity,R.string.email_foi_atualizado,Toast.LENGTH_LONG).show()
                    var checkFirstTimeUser=(mPreferences.getBoolean(PROFILE_TYPE_KEY,true))
                    if(checkFirstTimeUser){
                        val manager = fragmentManager
                        val transaction = manager!!.beginTransaction()
                        val fragment = OfficeProfileFragment()
                        transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                        transaction.replace(R.id.nav_host_fragment, fragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                    else{
                        val manager = fragmentManager
                        val transaction = manager!!.beginTransaction()
                        val fragment = LawyerProfileFragment()
                        transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                        transaction.replace(R.id.nav_host_fragment, fragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                }.addOnFailureListener{
                    if(it is FirebaseAuthRecentLoginRequiredException){
                        Toast.makeText(activity,R.string.necessario_reautenticar,Toast.LENGTH_LONG).show()
                        val manager = fragmentManager
                        val transaction = manager!!.beginTransaction()
                        val fragment = UserReauthenticateFragment()
                        transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                        transaction.replace(R.id.nav_host_fragment, fragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                    else if(it is FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(activity,R.string.email_form_incorreto,Toast.LENGTH_LONG).show()
                    }
                }
            }
            else{
                Toast.makeText(activity,R.string.preencha_email_corretamente,Toast.LENGTH_LONG).show()
            }
        }
        return view
    }
}