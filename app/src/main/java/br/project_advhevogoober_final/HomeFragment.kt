package br.project_advhevogoober_final

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.imperiumlabs.geofirestore.GeoFirestore
import androidx.core.view.*
import br.project_advhevogoober_final.Model.*
import org.imperiumlabs.geofirestore.extension.getAtLocation

class HomeFragment:Fragment() {

    val TAG ="HomeFragment"
    var db = FirebaseFirestore.getInstance()
    val collectionReferenceOffers = db.collection("Offers")
    val geoFirestoreOffers = GeoFirestore(collectionReferenceOffers)
    val key = "oGaupp7uI2W88QMZHcpLQlcQTTRGwz0e"
    val user = FirebaseAuth.getInstance().currentUser
    var userLocation: GeoPoint? = null
    var config: Config? = null

    private fun onPostItemClick(offer: Offer) {
        var intent = Intent(activity, OfferDetailsActivity::class.java)
        intent.putExtra("offer", offer)
        startActivity(intent)
    }

    override fun onAttach(context: Context) {
        Log.d(TAG,"onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate")
        super.onCreate(savedInstanceState)
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG,"onCreateView")
        val view: View =inflater!!.inflate(R.layout.fragment_home,container,false)
        var offers = mutableListOf<Offer>()
        view.no_locals_text.isVisible = false
        view.btn_local_add.isVisible = false
        view.btn_post_create.isVisible = false

        db.collection("lawyers").document(user!!.uid).get().addOnSuccessListener { it ->
            if (it.exists()) {
                val documentObject = it.toObject(LawyerProfile::class.java)!!
                val documentConfig: Config? = documentObject.config
                config = documentConfig ?: Config(20.0, listOf(true, true, true, true, true, true))
                userLocation = documentObject.l
                onConfigAndLocationGet(offers, view)
                view.loading.visibility = View.GONE
                view.btn_post_create.isVisible = true
            }
        }.addOnFailureListener{
            Log.i("LAWYERS_RETRIEVE_ERROR", "Erro: $it")
        }

        db.collection("offices").document(user!!.uid).get().addOnSuccessListener {
            if (it.exists()){
                val documentObject = it.toObject(OfficeProfile::class.java)!!
                val documentConfig: Config? = documentObject.config
                config = documentConfig ?: Config(20.0, listOf(true, true, true, true, true, true))
                userLocation = documentObject.l
                onConfigAndLocationGet(offers, view)
                loading.visibility = View.GONE
                view.btn_post_create.isVisible = true
            }
        }.addOnFailureListener{
            Log.i("OFFICES_RETRIEVE_ERROR", "Erro: $it")
        }

        view.btn_post_create.setOnClickListener{
            val transaction = fragmentManager?.beginTransaction()
            val fragment = CreateOfferFragment()
            transaction?.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
            transaction?.replace(R.id.nav_host_fragment, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        view.btn_local_add.setOnClickListener {
            val transaction = fragmentManager?.beginTransaction()
            val fragment = AddLocalFragment()
            transaction?.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
            transaction?.replace(R.id.nav_host_fragment, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }

    private fun MutableList<Offer>.filterByListBool(listBool: List<Boolean>, filterBool: (List<Boolean>) -> List<Offer>?): MutableList<Offer> {
        var result: MutableList<Offer> = mutableListOf()
            val filteredElement: List<Offer>? = filterBool(listBool)
            if (filteredElement != null) {
                result.addAll(filteredElement)
            }
        return result
    }

    private fun onConfigAndLocationGet(offers: MutableList<Offer>, view: View) {
        var resultOffers = mutableListOf<Offer>()
        if (userLocation != null && config != null) {
            view.no_locals_text.visibility = View.GONE
            view.btn_local_add.visibility = View.GONE

            geoFirestoreOffers.getAtLocation(userLocation!!, (config!!.range!!) * 1000) { docs, ex ->
                if (docs!!.isNotEmpty() && ex == null) {
                    for (document in docs) {
//                       var teste=(document.toObject(Offer::class.java))
                        if(document.toObject(Offer::class.java)!!.offererId != user!!.uid){
                            offers.add(document.toObject(Offer::class.java)!!)
                        }
                    }

                    resultOffers = offers.filterByListBool(config!!.jurisdictions!!) {
                        var result: MutableList<Offer> = mutableListOf()

                        for (offer in offers) {
                            for (index in it.indices) {
                                if (it[index] && offer.jurisdiction!![index] == it[index]) {
                                    result.add(offer)
                                }
                            }
                        }
                        result
                    }

                    var adapter = OfferRecycleAdapter(resultOffers, this::onPostItemClick)

                    view.recycler_view_home.layoutManager = LinearLayoutManager(activity)
                    view.recycler_view_home.adapter = adapter
                }

                if (ex != null) {
                    Log.i("GETOFFERS_ERROR", "Erro ao procurar ofertas por raio: $ex")
                }
            }
        }

        if(userLocation == null) {
            view.no_locals_text.isVisible = true
            view.btn_local_add.isVisible = true
        }
    }
}


