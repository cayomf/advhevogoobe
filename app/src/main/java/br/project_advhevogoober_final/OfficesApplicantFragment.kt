package br.project_advhevogoober_final

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.project_advhevogoober_final.Model.Global
import br.project_advhevogoober_final.Model.Offer
import br.project_advhevogoober_final.Model.OfficeProfile
import br.project_advhevogoober_final.Model.Solicitation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_offices_applicant.view.*

class OfficesApplicantFragment : Fragment() {

    var db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private fun onPostItemClick(officeProfile: OfficeProfile) {
        var intent = Intent(activity,ApplicantsProfileDetailsActivity::class.java)
        intent.putExtra("email", officeProfile.email)
        startActivity(intent)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val global = Global.getGlobalInstance()
        var offer: Offer = global.getOffer()
        val view: View =inflater!!.inflate(R.layout.fragment_offices_applicant,container,false)
        var finalOffice = mutableListOf<OfficeProfile>()
        var offices = mutableListOf<OfficeProfile>()
        var adapter = OfficesApplicantRecyclerAdapter(finalOffice, this::onPostItemClick)
        view.myApplicantOfficeLoad.visibility=View.VISIBLE

        db.collection("Solicitations").whereEqualTo("offerId", offer.idOffer).get().addOnSuccessListener {

            if (!it.isEmpty){
                var solicitations = mutableListOf<Solicitation>()

                for (doc in it) {
                    solicitations.add(doc.toObject(Solicitation::class.java))
                }

                db.collection("offices").get().addOnSuccessListener { docs ->
                    if (!docs!!.isEmpty) {

                        for (document in docs) {
                            var teste=document.toObject(Offer::class.java)
                            offices.add(document.toObject(OfficeProfile::class.java)!!)
                        }

                        for (sol in solicitations){
                            for (office in offices){
                                if(office.email == sol.userEmail){
                                    finalOffice.add(office)
                                }
                            }
                        }

                    }
                    view.myApplicantOfficeRecycler.layoutManager = LinearLayoutManager(activity)
                    view.myApplicantOfficeRecycler.adapter = adapter
                    view.myApplicantOfficeLoad.visibility=View.GONE
                }
            }
            view.myApplicantOfficeLoad.visibility=View.GONE

        }





        return view
    }
}
