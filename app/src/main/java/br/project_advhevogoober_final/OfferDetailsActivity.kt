package br.project_advhevogoober_final

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import br.project_advhevogoober_final.Model.Global
//import br.project_advhevogoober_final.Model.Global
import br.project_advhevogoober_final.Model.Offer
import br.project_advhevogoober_final.Model.Solicitation
//import br.project_advhevogoober_final.Model.Solicitation
import br.project_advhevogoober_final.Model.Utils.JurisdictionNames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_offer_details.*
import java.text.SimpleDateFormat
import java.util.*


class OfferDetailsActivity : AppCompatActivity() {


    val user = FirebaseAuth.getInstance().currentUser!!
    var db = FirebaseFirestore.getInstance()
    val solicitatioRef = db.collection("Solicitations").document()
    val collectionReference = db.collection("Offers")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_details)
        var offer = intent.getSerializableExtra("offer") as Offer
        val dateFormat = DateFormat.getDateFormat(this)
        val jurisdictionName = JurisdictionNames.generateJurisdictionName(offer.jurisdiction!!, this)
        details_offerer.text=offer.offerer
        details_price.text = offer.price.toString()
        details_requirements.text=offer.requirements
        details_date.text = dateFormat.format(offer.date)
        details_description.text = offer.description
        details_jurisdiction.text=jurisdictionName
        details_location.text = offer.street

        db.collection("Solicitations").whereEqualTo("userEmail",user.email).whereEqualTo("offerId",offer.idOffer).get().addOnSuccessListener {
            if(it.size() == 0){

                btn_request.setOnClickListener {
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val currentDate = sdf.format(Date())

                    var solicitation = Solicitation(
                        user.email.toString(),
                        offer.idOffer.toString(),
                        false,
                        solicitatioRef.id
                    )
                    db.collection("Solicitations").document(solicitatioRef.id).set(solicitation)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Solicitação enviada!", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Solicitação não enviada!", Toast.LENGTH_LONG).show()
                        }

                }
            }else{
                btn_request.isEnabled = false
            }
        }

        if (offer.offererId != user.uid) {
            details_offerer.text = offer.offerer+getString(R.string.clique_para_detalhes)
//            details_offerer.setTextColor(Color.parseColor("#008000"));
            details_offerer.setOnClickListener {
                var intent = Intent(this, ProfileOfferDetailsActivity::class.java)
                intent.putExtra("id", offer.offererId)
                startActivity(intent)
            }
            btn_excluir.isVisible=false
            btn_excluir.isClickable=false
            btn_edit.isVisible=false
            btn_edit.isClickable=false
            btn_candidate.isClickable=false


        }else{
            btn_request.isEnabled=false
            btn_request.isVisible=false
            btn_candidate.isVisible=true
        }
//        else{
//            btnCheckOffererProfile.visibility=View.GONE
//        }
        btn_edit.setOnClickListener {
            var intent = Intent(this@OfferDetailsActivity,EditOfferActivity::class.java)
            intent.putExtra("offer",offer)
            startActivity(intent)
        }
        btn_excluir.setOnClickListener {
            collectionReference.document(offer.idOffer.toString()).delete().addOnSuccessListener {
                Toast.makeText(this@OfferDetailsActivity,"deletou",Toast.LENGTH_LONG).show()
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener{
                Toast.makeText(this@OfferDetailsActivity,"nao deletou",Toast.LENGTH_LONG).show()
            }
        }
        db.collection("Solicitations").whereEqualTo("offerId", offer.idOffer).get().addOnSuccessListener {docs ->
            var solicitation: Solicitation = Solicitation()
            var solicitations = mutableListOf<Solicitation>()
            if(docs.size() != 0){
                for (doc in docs){
                    if(doc.toObject(Solicitation::class.java).accepted){
                        btn_candidate.isEnabled = false
                    }
                }
            }
        }
        btn_candidate.setOnClickListener {
            val intent = Intent(this, AplicantsTabsActivity::class.java)
            val global = Global.getGlobalInstance()
            global.setOffer(offer)
            intent.putExtra("offer", offer)
            startActivity(intent)
        }
    }
}
