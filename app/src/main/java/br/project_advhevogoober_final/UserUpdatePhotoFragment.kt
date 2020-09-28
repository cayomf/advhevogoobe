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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_user_update_photo.view.*
import java.io.ByteArrayOutputStream

class UserUpdatePhotoFragment: Fragment() {
    val TAG ="UserUpdatePhotoFragment"
    private val user= FirebaseAuth.getInstance().currentUser!!
    private lateinit var mPreferences: SharedPreferences
    private val PROFILE_TYPE_KEY:String="tipoP"
    private val mSharedPrefFile:String="br.project_advhevogoober_final"
    private var profileImage:ByteArray?=null
    var storageReference= FirebaseStorage.getInstance().reference
    private val db= FirebaseFirestore.getInstance()


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
        val view: View =inflater!!.inflate(R.layout.fragment_user_update_photo,container,false)

        view.btnSelectUpdatePhoto.setOnClickListener{
            val pickIntent = Intent()
            pickIntent.type = "image/*"
            pickIntent.action = Intent.ACTION_GET_CONTENT
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val pickTitle = getString(R.string.tire_ou_selecione_foto)
            val chooserIntent = Intent.createChooser(pickIntent, pickTitle)
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePhotoIntent))
            startActivityForResult(chooserIntent, 0)
        }

        view.btnSaveUpdatePhoto.setOnClickListener{
            if(profileImage!=null){
                var tarefa=storageReference.child("profileImages/"+user.uid).putBytes(profileImage!!)
                tarefa.addOnSuccessListener {
                    var profileType=(mPreferences.getBoolean(PROFILE_TYPE_KEY,true))
                    if(profileType){
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
                    Toast.makeText(activity,R.string.erro_ao_att_imagem,Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(activity,R.string.selecione_sua_nova_foto,Toast.LENGTH_LONG).show()
            }
        }
        return view
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
//                            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, imageUri)
//                            view!!.imageView2.setImageBitmap(bitmap)
                        }
                        data?.extras!!.get("data")!=null -> {
                            val photo = data?.extras!!.get("data") as Bitmap
                            val stream = ByteArrayOutputStream()
                            photo.compress(Bitmap.CompressFormat.PNG, 90, stream)
                            val image= stream.toByteArray()
                            profileImage = image
                            //view!!.imageView2.setImageBitmap(photo)
                        }
                        else -> {
                            Toast.makeText(activity,R.string.erro_ao_att_imagem, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}