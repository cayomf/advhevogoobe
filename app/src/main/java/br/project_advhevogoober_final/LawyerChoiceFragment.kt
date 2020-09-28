package br.project_advhevogoober_final

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Log.DEBUG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.project_advhevogoober_final.Model.LawyerProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_lawyer_choice.*
import kotlinx.android.synthetic.main.fragment_lawyer_choice.view.*
import java.io.ByteArrayOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat


class LawyerChoiceFragment:Fragment() {

    val TAG = "LawyerChoiceFragment"
    private lateinit var mPreferences: SharedPreferences
    private val PROFILE_CHECK_KEY:String="teste4"
    private val mSharedPrefFile:String="br.project_advhevogoober_final"
    var storageReference= FirebaseStorage.getInstance().reference
    private val db= FirebaseFirestore.getInstance()
    private val user=FirebaseAuth.getInstance().currentUser!!
    lateinit var lawyer:LawyerProfile
    private var profileImage:ByteArray?=null

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        mPreferences=this.activity!!.getSharedPreferences(mSharedPrefFile, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        container?.removeAllViews()// fix milagroso
        val view: View = inflater!!.inflate(R.layout.fragment_lawyer_choice, container, false)
        var preferencesEditor:SharedPreferences.Editor=mPreferences.edit()
        preferencesEditor.putBoolean(PROFILE_CHECK_KEY,true)
        preferencesEditor.apply()
        view.btnSaveLawyer.setOnClickListener {
            val regexCpf : Regex = Regex( "^[0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}-?[0-9]{2}$")
            val regexCelTel : Regex = Regex("^([1-9]{2}) (?:[2-8]|9[1-9])[0-9]{3}-[0-9]{4}$")
            if (view.lawyer_name.text.toString() != "" &&
                view.lawyer_surname.text.toString() != "" &&
                view.lawyer_phone.text.toString() != "" &&
                view.lawyer_ssn.text.toString() != "" &&
                view.lawyer_oab_code.text.toString() != "" &&
                view.lawyer_birthdate.text.toString() != "" && legalDoB() && profileImage!=null && regexCpf.matches(view.lawyer_ssn.text.toString())
                && regexCelTel.matches(view.lawyer_phone.text.toString())
            ) {

                var dateFormat=SimpleDateFormat("dd/MM/yyyy")
                var date=dateFormat.parse(lawyer_birthdate.text.toString())

                lawyer=LawyerProfile(view.lawyer_name.text.toString(),view.lawyer_surname.text.toString(),view.lawyer_phone.text.toString(),view.lawyer_ssn.text.toString(),view.lawyer_oab_code.text.toString(),date,null,user.email)
                db.collection("lawyers").document(user.uid).set(lawyer).addOnSuccessListener {
                }.addOnFailureListener{
                    Toast.makeText(activity,R.string.erro_ao_criar_perfil,Toast.LENGTH_LONG).show()
                }
                var tarefa=storageReference.child("profileImages/"+user.uid).putBytes(profileImage!!)
                tarefa.addOnSuccessListener {
                    var preferencesEditor:SharedPreferences.Editor=mPreferences.edit()
                    preferencesEditor.putBoolean(PROFILE_CHECK_KEY,false)
                    preferencesEditor.apply()
                    var intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                    this.activity!!.finish()
                }
            } else {
                Toast.makeText(activity,R.string.preencha_os_campos_corretamente_e_foto, Toast.LENGTH_LONG).show()
            }
        }
        view.btnSelectPhotoLawyer.setOnClickListener{
            
            val pickIntent = Intent()
            pickIntent.type = "image/*"
            pickIntent.action = Intent.ACTION_GET_CONTENT
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val pickTitle=getString(R.string.tire_ou_selecione_foto)
            val chooserIntent = Intent.createChooser(pickIntent, pickTitle)
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePhotoIntent))
            startActivityForResult(chooserIntent, 0)
        }
        return view
    }

    private fun legalDoB():Boolean{
        var dateFormat=SimpleDateFormat("dd/MM/yyyy")
        return try{
            var date=dateFormat.parse(lawyer_birthdate.text.toString())
            true
        } catch (e:ParseException){
            Log.d(DEBUG.toString(),"Not legal date")
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode ) {
                0 -> if (resultCode === Activity.RESULT_OK) {
                    when {
                        data?.data!=null -> {
                            val imageUri: Uri? = data.data
                            var bytearray=this.activity!!.contentResolver.openInputStream(imageUri!!)?.buffered().use { it?.readBytes() }
                            profileImage = bytearray
                        }
                        data?.extras!!.get("data")!=null -> {
                            val photo = data?.extras!!.get("data") as Bitmap
                            val stream = ByteArrayOutputStream()
                            photo.compress(Bitmap.CompressFormat.PNG, 90, stream)
                            val image= stream.toByteArray()
                            profileImage = image
                        }
                        else -> {
                            Toast.makeText(activity,R.string.erro_ao_tirar_ou_selecionar_foto,Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
    override fun onPause() {
        super.onPause()
        var preferencesEditor:SharedPreferences.Editor=mPreferences.edit()
        var checkFirstTimeUser=(mPreferences.getBoolean(PROFILE_CHECK_KEY,true))
        if (!checkFirstTimeUser){}
        else{
            preferencesEditor.putBoolean(PROFILE_CHECK_KEY,true)
            preferencesEditor.apply()
        }
    }
}



