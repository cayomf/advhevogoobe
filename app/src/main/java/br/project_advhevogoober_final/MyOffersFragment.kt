package br.project_advhevogoober_final

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import br.project_advhevogoober_final.Model.Offer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_my_offers.view.*
import kotlinx.android.synthetic.main.offer_recycle_item.*
import org.imperiumlabs.geofirestore.GeoFirestore

class MyOffersFragment : Fragment() {

    val TAG ="MyOffersFragment"
    var db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    val collectionReference = db.collection("Offers")
    val geoFirestore = GeoFirestore(collectionReference)
    val key = "oGaupp7uI2W88QMZHcpLQlcQTTRGwz0e"

    private fun onPostItemClick(offer: Offer) {
        var intent = Intent(activity, OfferDetailsActivity::class.java)
        intent.putExtra("offer", offer)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        Log.d(TAG,"onAttach")
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View =inflater!!.inflate(R.layout.fragment_my_offers,container,false)
        var offers = mutableListOf<Offer>()
        var adapter = OfferRecycleAdapter(offers, this::onPostItemClick)
        view.myOffersLoad.visibility=View.VISIBLE

        collectionReference.get().addOnSuccessListener { docs ->
            if (!docs!!.isEmpty) {
                for (document in docs) {
                    var teste=document.toObject(Offer::class.java)
                    if(teste.offererId == user!!.uid){
                        offers.add(document.toObject(Offer::class.java)!!)
                    }
                }
                view.myOfferRecycler.layoutManager = LinearLayoutManager(activity)
                view.myOfferRecycler.adapter = adapter
                view.myOffersLoad.visibility=View.GONE
            }
        }
        return view
    }
}
