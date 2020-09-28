package br.project_advhevogoober_final

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.project_advhevogoober_final.Model.OfficeProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_office_profile.*
import kotlinx.android.synthetic.main.fragment_office_profile.txtVwDEmail
import kotlinx.android.synthetic.main.fragment_office_profile.txtVwDNome
import kotlinx.android.synthetic.main.fragment_office_profile.txtVwDTelefone
import kotlinx.android.synthetic.main.fragment_office_profile.view.*

class OfficeProfileFragment:Fragment() {
    val TAG ="OfficeProfileFragment"
    private val db= FirebaseFirestore.getInstance()
    private val user= FirebaseAuth.getInstance().currentUser!!
    var storageReference= FirebaseStorage.getInstance().reference

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
        val view: View =inflater!!.inflate(R.layout.fragment_office_profile, container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarOffice.visibility=View.VISIBLE
        office_local_edit_button.visibility = View.INVISIBLE

        for (x in 0 until layoutOfficeProfile.childCount ){
            var daodao:View=layoutOfficeProfile.getChildAt(x)

            if (daodao !is ProgressBar){
                daodao.visibility=View.INVISIBLE
            }
        }

        office_local_edit_button.setOnClickListener {
            val manager = fragmentManager
            val transaction = manager!!.beginTransaction()
            val fragment = EditLocalFragment()
            transaction.replace(R.id.nav_host_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        db.collection("offices").document(user.uid).get().addOnSuccessListener {
            if (it.exists()){
                var officeProfile=it.toObject(OfficeProfile::class.java)
                txtVwDNome.text=officeProfile!!.name
                txtVwDTelefone.text=officeProfile!!.phone
                txtVwDEmail.text=officeProfile.email
                txtVwDCNPJ.text=officeProfile!!.businessId
                if (officeProfile.l != null) {
                    office_local_edit_button.visibility = View.VISIBLE
                }
            }
            else{
                Toast.makeText(activity,R.string.erro_pegar_dados_firebase,Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            Toast.makeText(activity,it.message.toString(), Toast.LENGTH_LONG).show()
        }
        var tarefa=storageReference.child("profileImages/"+user.uid).getBytes(1024*1024)
        tarefa.addOnSuccessListener {
            if (it!=null){
                progressBarOffice.visibility= View.GONE
                for (x in 0 until layoutOfficeProfile.childCount ){
                    var daodao:View=layoutOfficeProfile.getChildAt(x)
                    if (daodao !is ProgressBar){
                        daodao.visibility=View.VISIBLE
                    }
                }
                var imagem= BitmapFactory.decodeByteArray(it,0,it.size)
                imgVwPhotoOffice.setImageBitmap(imagem)
                view.btnUpdateEmailOffice.setOnClickListener{
                    val manager = fragmentManager
                    val transaction = manager!!.beginTransaction()
                    val fragment = UserUpdateEmailFragment()
                    transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                    transaction.replace(R.id.nav_host_fragment, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                view.btnUpdatePhotoOffice.setOnClickListener{
                    val manager = fragmentManager
                    val transaction = manager!!.beginTransaction()
                    val fragment = UserUpdatePhotoFragment()
                    transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                    transaction.replace(R.id.nav_host_fragment, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            else{
                Toast.makeText(activity,R.string.erro_ao_carregar_imagem_perfil,Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            Toast.makeText(activity,it.message,Toast.LENGTH_LONG).show()
            progressBarOffice.visibility= View.GONE
        }
    }
}