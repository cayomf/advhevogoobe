package br.project_advhevogoober_final

import android.content.Intent
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
import br.project_advhevogoober_final.Model.Offer
import br.project_advhevogoober_final.Model.OfficeProfile
import br.project_advhevogoober_final.Service.DAO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_create_offer.*
import kotlinx.android.synthetic.main.fragment_create_offer.view.*
import kotlinx.android.synthetic.main.fragment_lawyer_choice.*
import org.imperiumlabs.geofirestore.GeoFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CreateOfferFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val user= FirebaseAuth.getInstance().currentUser!!
    val collectionReference = db.collection("Offers")
    val userRef = db.collection("Offers").document()
    val geoFirestore = GeoFirestore(collectionReference)
    val retrofit = RetrofitBuilder.getInstance()
    val service : DAO? = retrofit?.create(DAO::class.java)
    val key = "oGaupp7uI2W88QMZHcpLQlcQTTRGwz0e"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val view: View = inflater!!.inflate(R.layout.fragment_create_offer,container,false)
        var nome = ""
        db.collection("lawyers").document(user.uid).get().addOnSuccessListener {
            if (it.exists()) {
                var lawyerProfile = it.toObject(LawyerProfile::class.java)
                nome = lawyerProfile!!.name!!
            }
        }
        db.collection("offices").document(user.uid).get().addOnSuccessListener {
            if (it.exists()) {
                var officeProfile = it.toObject(OfficeProfile::class.java)
                nome = officeProfile!!.name!!
            }
        }
        view.btn_post.setOnClickListener {
            if(legalDoB() && jurisdictions.checkedRadioButtonId != -1 && editText_price.text.toString().toDoubleOrNull()!=null && editText_street.text.toString().isNotEmpty() &&
                    editText_city.text.toString().isNotEmpty() && editText_state.text.toString().isNotEmpty() && editText_postal_code.text.toString().isNotEmpty() && nome.isNotEmpty() &&
                    editText_description.text.toString().isNotEmpty() && editText_requirements.text.toString().isNotEmpty()){
                var dateFormat=SimpleDateFormat("dd/MM/yyyy")
                var date=dateFormat.parse(editText_date.text.toString())
                var jurisdictionList: MutableList<Boolean> = mutableListOf()
                when (jurisdictions.checkedRadioButtonId) {
                    R.id.jurisdiction1 -> jurisdictionList.addAll(listOf(true, false, false, false, false, false))
                    R.id.jurisdiction2 -> jurisdictionList.addAll(listOf(false, true, false, false, false, false))
                    R.id.jurisdiction3 -> jurisdictionList.addAll(listOf(false, false, true, false, false, false))
                    R.id.jurisdiction4 -> jurisdictionList.addAll(listOf(false, false, false, true, false, false))
                    R.id.jurisdiction5 -> jurisdictionList.addAll(listOf(false, false, false, false, true, false))
                    R.id.jurisdiction6 -> jurisdictionList.addAll(listOf(false, false, false, false, false, true))
                }
                val offer = Offer(
                    date,
                    jurisdictionList,
                    editText_price.text.toString().toDouble(),
                    editText_street.text.toString(),
                    editText_city.text.toString(),
                    editText_state.text.toString(),
                    editText_postal_code.text.toString(),
                    nome,
                    currentDate,
                    editText_description.text.toString(),
                    editText_requirements.text.toString(),
                    user.uid,
                    userRef.id
                )

                collectionReference.document(userRef.id).set(offer).addOnSuccessListener {
                    service?.show(key, offer.street, offer.city, offer.state, offer.postalCode)?.enqueue(object : Callback<APIResultsObject> {
                        override fun onFailure(call: Call<APIResultsObject>, t: Throwable) {
                            Toast.makeText(activity, R.string.erro_ao_salvar_oferta, Toast.LENGTH_LONG).show()
                            Log.i("Erro da request da API: ", t.toString())
                        }

                        override fun onResponse(call: Call<APIResultsObject>, response: Response<APIResultsObject>) {
                            val lat : Double = response.body()?.results?.get(0)?.locations?.get(0)?.latLng?.lat!!
                            val long : Double = response.body()?.results?.get(0)?.locations?.get(0)?.latLng?.lng!!
                            geoFirestore.setLocation(userRef.id, GeoPoint(lat, long))
                        }
                    })
                }.addOnFailureListener{
                    Toast.makeText(activity,R.string.erro_ao_salvar_oferta, Toast.LENGTH_LONG).show()
                }
                val intent= Intent(this.activity!!,CheckoutActivity::class.java)
                intent.putExtra("offer",offer)
                startActivity(intent)
            }
            else{
                Toast.makeText(this.activity,getString(R.string.preencha_os_campos_corretamente),Toast.LENGTH_LONG).show()
            }
        }
        return view
    }private fun legalDoB():Boolean{
        var dateFormat=SimpleDateFormat("dd/MM/yyyy")
        return try{
            var date=dateFormat.parse(editText_date.text.toString())
            true
        } catch (e: ParseException){
            Log.d(Log.DEBUG.toString(),"Not legal date")
            false
        }
    }
}
