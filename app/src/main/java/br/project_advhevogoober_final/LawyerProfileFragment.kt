package br.project_advhevogoober_final

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.project_advhevogoober_final.Model.LawyerProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_lawyer_profile.*
import kotlinx.android.synthetic.main.fragment_lawyer_profile.view.*
import android.widget.ProgressBar


class LawyerProfileFragment:Fragment() {
    val TAG ="LawyerProfileFragment"
    private val db= FirebaseFirestore.getInstance()
    private val user=FirebaseAuth.getInstance().currentUser!!
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
        val view: View =inflater!!.inflate(R.layout.fragment_lawyer_profile, container,false)
        view.btnUpdatePhotoLawyer.setOnClickListener{
        }
        return view
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarTest.visibility=View.VISIBLE

        lawyer_local_edit_button.visibility = View.INVISIBLE

        for (x in 0 until layoutLawyerProfile.childCount ){
            var daodao:View=layoutLawyerProfile.getChildAt(x)

            if (daodao !is ProgressBar){
                daodao.visibility=View.INVISIBLE
            }
        }

        lawyer_local_edit_button.setOnClickListener {
            val manager = fragmentManager
            val transaction = manager!!.beginTransaction()
            val fragment = EditLocalFragment()
            transaction.replace(R.id.nav_host_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        db.collection("lawyers").document(user.uid).get().addOnSuccessListener {
            if (it.exists()){
                var lawyerProfile=it.toObject(LawyerProfile::class.java)
                val dateFormat = DateFormat.getDateFormat(context)
                txtVwDNome.text=lawyerProfile!!.name
                txtVwDSobrenome.text=lawyerProfile!!.surname
                txtVwDTelefone.text=lawyerProfile!!.phone
                txtVwDEmail.text=lawyerProfile.email
                txtVwDCPF.text=lawyerProfile!!.ssn
                txtVwDOAB.text=lawyerProfile!!.oab_code
                txtVwDDataN.text=dateFormat.format(lawyerProfile.birthdate)
                if (lawyerProfile.l != null) {
                    lawyer_local_edit_button.visibility = View.VISIBLE
                }
            }
            else{
                Toast.makeText(activity,R.string.erro_ao_carregar_perfil,Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            Toast.makeText(activity,R.string.erro_pegar_dados_firebase,Toast.LENGTH_LONG).show()
        }
        var tarefa=storageReference.child("profileImages/"+user.uid).getBytes(1024*1024)
        tarefa.addOnSuccessListener {
            if (it!=null) {
                progressBarTest.visibility = View.GONE
                for (x in 0 until layoutLawyerProfile.childCount) {
                    var daodao: View = layoutLawyerProfile.getChildAt(x)

                    if (daodao !is ProgressBar) {
                        daodao.visibility = View.VISIBLE
                    }
                }
                var imagem = BitmapFactory.decodeByteArray(it, 0, it.size)
                imgVwPhotoLawyer.setImageBitmap(imagem)
                view.btnUpdateEmailLawyer.setOnClickListener {
                    val manager = fragmentManager
                    val transaction = manager!!.beginTransaction()
                    val fragment = UserUpdateEmailFragment()
                    transaction?.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                    transaction.replace(R.id.nav_host_fragment, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                view.btnUpdatePhotoLawyer.setOnClickListener {
                    val manager = fragmentManager
                    val transaction = manager!!.beginTransaction()
                    val fragment = UserUpdatePhotoFragment()
                    transaction.replace(R.id.nav_host_fragment, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            else{
                Toast.makeText(activity,R.string.erro_ao_carregar_imagem_perfil,Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            Toast.makeText(activity,R.string.erro_ao_carregar_imagem_perfil,Toast.LENGTH_LONG).show()
            progressBarTest.visibility= View.GONE
        }
    }
}