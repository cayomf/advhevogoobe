package br.project_advhevogoober_final

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import br.project_advhevogoober_final.Model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_applicants_profile_details.*

class ApplicantsProfileDetailsActivity : AppCompatActivity() {
    private val db= FirebaseFirestore.getInstance()
    var storageReference= FirebaseStorage.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applicants_profile_details)

        progressBarApplicant.visibility= View.VISIBLE

        for (x in 0 until layoutProfileOffer.childCount ){
            var daodao: View =layoutProfileOffer.getChildAt(x)

            if (daodao !is ProgressBar){
                daodao.visibility= View.INVISIBLE
            }
        }

        var email = intent.getStringExtra("email")
        val global = Global.getGlobalInstance()
        var offer: Offer = global.getOffer()



        db.collection("lawyers").whereEqualTo("email", email).get().addOnSuccessListener {
            var lawyers = mutableListOf<LawyerProfile>()
            var lawyerProfile = LawyerProfile()
            if (!it.isEmpty) {
                for(prof in it){
                    lawyers.add(prof.toObject(LawyerProfile::class.java))
                }
                for (adv in lawyers){
                    lawyerProfile = adv
                }

                txtVwDNomeApplicant.text = lawyerProfile!!.name
                txtVwDSobrenomeApplicant.text = lawyerProfile!!.surname
                txtVwDTelefoneApplicant.text = lawyerProfile!!.phone
                txtVwDEmailApplicant.text = lawyerProfile!!.email
                txtVwDOABApplicant.text = lawyerProfile!!.oab_code
            }

        }.addOnFailureListener{
            Toast.makeText(this,R.string.erro_pegar_dados_firebase, Toast.LENGTH_LONG).show()
        }

        db.collection("offices").whereEqualTo("email", email).get().addOnSuccessListener {
            var offices = mutableListOf<OfficeProfile>()
            var officeProfile = OfficeProfile()
            if (!it.isEmpty) {
                for(prof in it){
                    offices.add(prof.toObject(OfficeProfile::class.java))
                }
                for (off in offices){
                    officeProfile = off
                }

                txtVwDNomeApplicant.text = officeProfile!!.name
                txtVwDTelefoneApplicant.text = officeProfile!!.phone
                txtVwDEmailApplicant.text = officeProfile!!.email
                textView6.isVisible=false
                txtVwDSobrenomeApplicant.isVisible=false
                textView11.isVisible = false
                txtVwDOABApplicant.isVisible = false

            }


        }.addOnFailureListener{
            Toast.makeText(this,R.string.erro_pegar_dados_firebase, Toast.LENGTH_LONG).show()
        }

        btn_accepted.setOnClickListener {
            db.collection("Solicitations").whereEqualTo("userEmail", email).whereEqualTo("offerId", offer.idOffer).get().addOnSuccessListener {
                var solicitations = mutableListOf<Solicitation>()
                var solicitation = Solicitation()
                for (doc in it){
                    solicitations.add(doc.toObject(Solicitation::class.java))
                }
                for (sol in solicitations){
                    solicitation = sol
                }
                val teste = Solicitation(
                    solicitation.userEmail,
                    solicitation.offerId,
                    true,
                    solicitation.solitationId
                )
                db.collection("Solicitations").document(solicitation.solitationId).set(teste).addOnSuccessListener {
                    Toast.makeText(this,"Usuário aceito", Toast.LENGTH_SHORT).show()
                }
            }
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }



        var tarefa=storageReference.child("profileImages/"+offer.offererId).getBytes(1024*1024)
        tarefa.addOnSuccessListener {
            if (it!=null){
                progressBarApplicant.visibility= View.GONE
                for (x in 0 until layoutProfileOffer.childCount ){
                    var daodao: View =layoutProfileOffer.getChildAt(x)
                    if (daodao !is ProgressBar){
                        daodao.visibility= View.VISIBLE
                    }
                }
                Toast.makeText(this,R.string.completou_com_sucesso, Toast.LENGTH_LONG).show()
                var imagem= BitmapFactory.decodeByteArray(it,0,it.size)
                imgVwPhotoApplicantPrefile.setImageBitmap(imagem)
            }
            else{
                Toast.makeText(this,R.string.erro_ao_carregar_imagem_perfil, Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this,R.string.erro_ao_carregar_imagem_perfil, Toast.LENGTH_LONG).show()
            progressBarApplicant.visibility= View.GONE
        }

        btnChat.setOnClickListener {
            var intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("offererId", offer.offererId)
            startActivity(intent)
        }
    }
}
