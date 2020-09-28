package br.project_advhevogoober_final

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.project_advhevogoober_final.Model.Offer
import br.project_advhevogoober_final.Model.Solicitation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_my_offers.view.*
import kotlinx.android.synthetic.main.fragment_my_solicitations.view.*

class MySolicitationsFragment : Fragment() {

    var db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    val collectionReference = db.collection("Offers")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private fun onPostItemClick(offer: Offer) {
        var intent = Intent(activity, OfferDetailsActivity::class.java)
        intent.putExtra("offer", offer)
        startActivity(intent)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View =inflater!!.inflate(R.layout.fragment_my_solicitations,container,false)
        var offers = mutableListOf<Offer>()
        var listOffers = mutableListOf<Offer>()
        var adapter = OfferRecycleAdapter(offers, this::onPostItemClick)
        view.mySolicitationsApplicantLoad.visibility=View.VISIBLE

        db.collection("Solicitations").whereEqualTo("userEmail",user!!.email).get().addOnSuccessListener { docs ->
            var solicitations = mutableListOf<Solicitation>()

            if (!docs!!.isEmpty) {
                for (document in docs) {
                    solicitations.add(document.toObject(Solicitation::class.java))
                }

                db.collection("Offers").get().addOnSuccessListener {
                    if(!it.isEmpty){
                        for (oferta in it){
                            listOffers.add(oferta.toObject(Offer::class.java))
                        }
                        for (sol in solicitations) {
                            for (oferta in listOffers){
                                if (oferta.idOffer == sol.offerId){
                                    offers.add(oferta)
                                }
                            }
                        }

                        view.mySolicitationsApplicantRecycler.layoutManager = LinearLayoutManager(activity)
                        view.mySolicitationsApplicantRecycler.adapter = adapter

                    }
                }
            }
            view.mySolicitationsApplicantLoad.visibility=View.GONE
        }
        return view
    }
}
