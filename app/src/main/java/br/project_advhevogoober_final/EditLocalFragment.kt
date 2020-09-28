package br.project_advhevogoober_final

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import br.project_advhevogoober_final.API.RetrofitBuilder
import br.project_advhevogoober_final.Model.APIResultsObject
import br.project_advhevogoober_final.Model.LawyerProfile
import br.project_advhevogoober_final.Model.OfficeProfile

import br.project_advhevogoober_final.R
import br.project_advhevogoober_final.Service.DAO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_edit_local.view.*
import org.imperiumlabs.geofirestore.GeoFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class EditLocalFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    val collectionReferenceLawyers = db.collection("lawyers")
    val geoFirestoreLawyers = GeoFirestore(collectionReferenceLawyers)
    val collectionReferenceOffices = db.collection("offices")
    val geoFirestoreOffices = GeoFirestore(collectionReferenceOffices)
    val retrofit = RetrofitBuilder.getInstance()
    val service = retrofit?.create(DAO::class.java)
    val key = "oGaupp7uI2W88QMZHcpLQlcQTTRGwz0e"
    val user = FirebaseAuth.getInstance().currentUser!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_local, container, false)

        db.collection("lawyers").document(user.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val documentObject = it.toObject(LawyerProfile::class.java)
                view.local_street.setText(documentObject!!.street)
                view.local_city.setText(documentObject!!.city)
                view.local_state.setText(documentObject!!.state)
                view.local_postal_code.setText(documentObject!!.postalCode)
            }
        }.addOnFailureListener{
            Toast.makeText(activity,R.string.erro_ao_add_local, Toast.LENGTH_LONG).show()
            Log.i("LOCAL_ADD_ERROR", "Erro: $it")
        }
        db.collection("offices").document(user.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val documentObject = it.toObject(OfficeProfile::class.java)
                view.local_street.setText(documentObject!!.street)
                view.local_city.setText(documentObject!!.city)
                view.local_state.setText(documentObject!!.state)
                view.local_postal_code.setText(documentObject!!.postalCode)
            }
        }.addOnFailureListener{
            Toast.makeText(activity,R.string.erro_ao_add_local, Toast.LENGTH_LONG).show()
            Log.i("LOCAL_ADD_ERROR", "Erro: $it")
        }

        view.local_edit.setOnClickListener {
            if (
                !view.local_street.text.isNullOrEmpty() &&
                !view.local_city.text.isNullOrEmpty() &&
                !view.local_state.text.isNullOrEmpty() &&
                !view.local_postal_code.text.isNullOrEmpty()
            ) {
                service?.show(
                    key,
                    view.local_street.text.toString(),
                    view.local_city.text.toString(),
                    view.local_state.text.toString(),
                    view.local_postal_code.text.toString()
                )?.enqueue(object : Callback<APIResultsObject> {
                    override fun onFailure(call: Call<APIResultsObject>, t: Throwable) {
                        Toast.makeText(activity, R.string.erro_ao_add_local, Toast.LENGTH_LONG).show()
                        Log.i("Erro de chamada da API: ", t.toString())
                    }

                    override fun onResponse(call: Call<APIResultsObject>, response: Response<APIResultsObject>) {
                        val lat : Double = response?.body()?.results?.get(0)?.locations?.get(0)?.latLng?.lat!!
                        val long : Double = response?.body()?.results?.get(0)?.locations?.get(0)?.latLng?.lng!!
                        val geoPoint = GeoPoint(lat, long)

                        db.collection("lawyers").document(user.uid).get().addOnSuccessListener {
                            if (it.exists()) {
                                try {
                                    geoFirestoreLawyers.setLocation(user.uid, geoPoint)
                                    db.collection("lawyers").document(user.uid).update("street", view.local_street.text.toString())
                                    db.collection("lawyers").document(user.uid).update("city", view.local_city.text.toString())
                                    db.collection("lawyers").document(user.uid).update("state", view.local_state.text.toString())
                                    db.collection("lawyers").document(user.uid).update("postalCode", view.local_postal_code.text.toString())
                                    val manager = fragmentManager
                                    val transaction = manager!!.beginTransaction()
                                    val fragment = HomeFragment()
                                    transaction?.replace(R.id.nav_host_fragment, fragment)
                                    transaction?.addToBackStack(null)
                                    transaction?.commit()
                                } catch(e: Exception) {
                                    Log.i("GEOFIRESTORE_EXCEPTION", "Erro na inserção de localização: $e")
                                }
                            }
                        }.addOnFailureListener{
                            Toast.makeText(activity,R.string.erro_ao_add_local,Toast.LENGTH_LONG).show()
                            Log.i("LOCAL_ADD_ERROR", "Erro: $it")
                        }
                        db.collection("offices").document(user.uid).get().addOnSuccessListener {
                            if (it.exists()){
                                try {
                                    geoFirestoreOffices.setLocation(it.id, geoPoint)
                                    db.collection("offices").document(user.uid).update("street", view.local_street.text.toString())
                                    db.collection("offices").document(user.uid).update("city", view.local_city.text.toString())
                                    db.collection("offices").document(user.uid).update("state", view.local_state.text.toString())
                                    db.collection("offices").document(user.uid).update("postalCode", view.local_postal_code.text.toString())
                                    val manager = fragmentManager
                                    val transaction = manager!!.beginTransaction()
                                    val fragment = HomeFragment()
                                    transaction?.replace(R.id.nav_host_fragment, fragment)
                                    transaction?.addToBackStack(null)
                                    transaction?.commit()
                                } catch (e: Exception) {
                                    Log.i("GEOFIRESTORE_EXCEPTION", "Erro na inserção de localização: $e")
                                }
                            }
                        }.addOnFailureListener {
                            Toast.makeText(activity, R.string.erro_ao_add_local, Toast.LENGTH_LONG)
                                .show()
                            Log.i("LOCAL_ADD_ERROR", "Erro: $it")
                        }
                    }
                })
            } else {
                Toast.makeText(activity, R.string.preencha_os_campos_corretamente, Toast.LENGTH_LONG).show()
            }
        }

        return view
    }

}
