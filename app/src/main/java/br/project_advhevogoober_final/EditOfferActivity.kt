package br.project_advhevogoober_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.RadioButton
import android.widget.Toast
import br.project_advhevogoober_final.API.RetrofitBuilder
import br.project_advhevogoober_final.Model.APIResultsObject
import br.project_advhevogoober_final.Model.Offer
import br.project_advhevogoober_final.Service.DAO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_edit_offer.*
import org.imperiumlabs.geofirestore.GeoFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class EditOfferActivity : AppCompatActivity() {

    private lateinit var listRadios: List<RadioButton>
    val collectionReference = FirebaseFirestore.getInstance().collection("Offers")
    val geoFirestore = GeoFirestore(collectionReference)
    val retrofit = RetrofitBuilder.getInstance()
    val service : DAO? = retrofit?.create(DAO::class.java)
    val key = "oGaupp7uI2W88QMZHcpLQlcQTTRGwz0e"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_offer)
        listRadios = listOf(
            edit_jurisdiction1,
            edit_jurisdiction2,
            edit_jurisdiction3,
            edit_jurisdiction4,
            edit_jurisdiction5,
            edit_jurisdiction6
        )
        edit_jurisdiction1.tag = 0
        edit_jurisdiction2.tag = 1
        edit_jurisdiction3.tag = 2
        edit_jurisdiction4.tag = 3
        edit_jurisdiction5.tag = 4
        edit_jurisdiction6.tag = 5
        var offer = intent.getSerializableExtra("offer") as Offer
        val OfferRef = collectionReference.document(offer.idOffer!!)
        val dateFormat = DateFormat.getDateFormat(this)
        val listBool = generateJurisdictions(offer)
        edit_city.setText(offer.city)
        edit_date.setText(dateFormat.format(offer.date))
        edit_description.setText(offer.description)
        edit_postalcode.setText(offer.postalCode)
        edit_price.setText(offer.price.toString())
        edit_requirements.setText(offer.requirements)
        edit_state.setText(offer.state)
        edit_street.setText(offer.street)
        btn_edit.setOnClickListener {
            setJurisdiction(listBool)
            var dateFormat= SimpleDateFormat("dd/MM/yyyy")
            var date=dateFormat.parse(edit_date.text.toString())
            val offer_teste = Offer(
                date,
                listBool.toList(),
                edit_price.text.toString().toDouble(),
                edit_street.text.toString(),
                edit_city.text.toString(),
                edit_state.text.toString(),
                edit_postalcode.text.toString(),
                offer.offerer,
                offer.postDate,
                edit_description.text.toString(),
                edit_requirements.text.toString(),
                offer.offererId,
                offer.idOffer
            )
            OfferRef.set(offer_teste).addOnSuccessListener {
                service?.show(
                    key,
                    edit_street.text.toString(),
                    edit_city.text.toString(),
                    edit_state.text.toString(),
                    edit_postalcode.text.toString()
                )!!
                    .enqueue(object : Callback<APIResultsObject> {
                    override fun onFailure(call: Call<APIResultsObject>, t: Throwable) {
                        Toast.makeText(this@EditOfferActivity, "Erro na mudança de local: " + t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(
                        call: Call<APIResultsObject>,
                        response: Response<APIResultsObject>
                    ) {
                        val lat : Double = response.body()?.results?.get(0)?.locations?.get(0)?.latLng?.lat!!
                        val long : Double = response.body()?.results?.get(0)?.locations?.get(0)?.latLng?.lng!!
                        geoFirestore.setLocation(OfferRef.id, GeoPoint(lat, long))
                    }

                })
                var intent=Intent(this@EditOfferActivity, MainActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener{
                Toast.makeText(this, R.string.erro_ao_salvar_oferta, Toast.LENGTH_LONG).show()
                var intent=Intent(this@EditOfferActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun generateJurisdictions(offer: Offer): MutableList<Boolean> {
        var jurisdictionIndex = -1
        for (bool in offer.jurisdiction!!) {
            if (bool) {
                jurisdictionIndex = offer.jurisdiction!!.indexOf(bool)
            }
        }
        val listBool = mutableListOf(false, false, false, false, false, false)
        for (radio in listRadios) {
            if (radio.tag == jurisdictionIndex) {
                radio.isChecked = true
                listBool[jurisdictionIndex] = true
            }
        }
        return listBool
    }

    private fun setJurisdiction(list: MutableList<Boolean>) {
        for (index in list.indices) {
            list[index] = false
        }
        for (radio in listRadios) {
            if (radio.isChecked) {
                list[listRadios.indexOf(radio)] = true
            }
        }
    }
}
