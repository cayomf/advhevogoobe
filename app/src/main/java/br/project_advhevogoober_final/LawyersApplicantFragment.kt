package br.project_advhevogoober_final

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.project_advhevogoober_final.Model.Global
import br.project_advhevogoober_final.Model.LawyerProfile
import br.project_advhevogoober_final.Model.Offer
import br.project_advhevogoober_final.Model.Solicitation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_lawyers_applicant.view.*

class LawyersApplicantFragment : Fragment() {

    var db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun onPostItemClick(lawyerProfile: LawyerProfile) {
        var intent = Intent(activity,ApplicantsProfileDetailsActivity::class.java)
        intent.putExtra("email", lawyerProfile.email)
        startActivity(intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val global = Global.getGlobalInstance()
        var offer: Offer = global.getOffer()
        val view: View =inflater!!.inflate(R.layout.fragment_lawyers_applicant,container,false)
        var finalLawyers = mutableListOf<LawyerProfile>()
        var lawyers = mutableListOf<LawyerProfile>()
        var adapter = LawyersApplicantRecyclerAdapter(finalLawyers, this::onPostItemClick)
                view.myApplicantLoad.visibility=View.VISIBLE

        db.collection("Solicitations").whereEqualTo("offerId", offer.idOffer).get().addOnSuccessListener {

            if (!it.isEmpty){
                var solicitations = mutableListOf<Solicitation>()

                for (doc in it) {
                    solicitations.add(doc.toObject(Solicitation::class.java))
                }

                db.collection("lawyers").get().addOnSuccessListener { docs ->
                    if (!docs!!.isEmpty) {

                        for (document in docs) {
                            var teste=document.toObject(Offer::class.java)
                            lawyers.add(document.toObject(LawyerProfile::class.java)!!)
                        }

                        for (sol in solicitations){
                            for (adv in lawyers){
                                if(adv.email == sol.userEmail){
                                    finalLawyers.add(adv)
                                }
                            }
                        }

                    }
                    view.myApplicantRecycler.layoutManager = LinearLayoutManager(activity)
                    view.myApplicantRecycler.adapter = adapter

                }
            }
            view.myApplicantLoad.visibility=View.GONE

        }
        return view
    }
}
