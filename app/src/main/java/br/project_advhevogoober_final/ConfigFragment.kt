package br.project_advhevogoober_final

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.project_advhevogoober_final.Model.Config
import br.project_advhevogoober_final.Model.LawyerProfile
import br.project_advhevogoober_final.Model.OfficeProfile
import br.project_advhevogoober_final.Model.ViewModel.ConfigViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_config.*
import kotlinx.android.synthetic.main.fragment_config.view.*

class ConfigFragment:Fragment() {

    val TAG ="ConfigFragment"
    val user = FirebaseAuth.getInstance().currentUser!!
    val db = FirebaseFirestore.getInstance()
    private lateinit var sliderViewModel : ConfigViewModel
    private var config: Config? = null

    override fun onAttach(context: Context) {
        Log.d(TAG,"onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG,"onCreateView")
        val view: View = inflater.inflate(R.layout.fragment_config, container,false)

        config = getConfig(view)

        sliderViewModel = ViewModelProviders.of(this.activity!!).get(ConfigViewModel::class.java)

        view.save_configs.setOnClickListener {
            saveConfig(view)
        }

        setSliderChangeListener(view)

        setSliderTextValue(view)

        return view
    }

    private fun setSliderChangeListener(view: View) {
        view.mapsRange.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sliderViewModel.sliderValue.value = progress.toDouble()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    private fun setSliderTextValue(view: View) {
        sliderViewModel.sliderValue.observe(this.activity!!, Observer {
            if (it != null) {
                view.mapsRange.progress = it.toInt()
                view.rangeNumber.text = "$it km"
            }
        })
    }

    private fun getConfig(view: View): Config? {
        var result: Config? = null
        db.collection("lawyers").document(user.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val documentObject = it.toObject(LawyerProfile::class.java)
                if (documentObject!!.config != null) {
                    result = documentObject.config
                }
            }
        }.addOnFailureListener{
            Log.i("LAWYERS_RETRIEVE_ERROR", "Erro: $it")
        }
        db.collection("offices").document(user.uid).get().addOnSuccessListener {
            if (it.exists()){
                val documentObject = it.toObject(OfficeProfile::class.java)
                if (documentObject!!.config != null) {
                    result = documentObject.config
                    view.jurisdiction1.isChecked = result!!.jurisdictions!![0]
                    view.jurisdiction2.isChecked = result!!.jurisdictions!![1]
                    view.jurisdiction3.isChecked = result!!.jurisdictions!![2]
                    view.jurisdiction4.isChecked = result!!.jurisdictions!![3]
                    view.jurisdiction5.isChecked = result!!.jurisdictions!![4]
                    view.jurisdiction6.isChecked = result!!.jurisdictions!![5]
                    view.mapsRange.progress = (result!!.range!! * 10).toInt()
                    view.rangeNumber.text = "${(result!!.range!! * 10)} km"
                }
            }
        }.addOnFailureListener{
            Log.i("OFFICES_RETRIEVE_ERROR", "Erro: $it")
        }

        return result
    }

    private fun saveConfig(view: View) {
        db.collection("lawyers").document(user.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val config = setConfig(view)
                db.collection("lawyers").document(user.uid).update("config", config)
                changeFragment(HomeFragment())
            }
        }.addOnFailureListener{
            Log.i("LAWYERS_RETRIEVE_ERROR", "Erro: $it")
        }
        db.collection("offices").document(user.uid).get().addOnSuccessListener {
            if (it.exists()){
                val config = setConfig(view)
                db.collection("offices").document(user.uid).update("config", config)
                changeFragment(HomeFragment())
            }
        }.addOnFailureListener{
            Log.i("OFFICES_RETRIEVE_ERROR", "Erro: $it")
        }
    }

    private fun setConfig(view: View): Config {
        val range: Double = (view.mapsRange.progress / 10).toDouble()
        val jurisdictions = listOf(
            view.jurisdiction1.isChecked,
            view.jurisdiction2.isChecked,
            view.jurisdiction3.isChecked,
            view.jurisdiction4.isChecked,
            view.jurisdiction5.isChecked,
            view.jurisdiction6.isChecked
        )
        return Config(range, jurisdictions)
    }

    private fun changeFragment(fragment : Fragment) {
        val transaction = fragmentManager?.beginTransaction()
        val fragment = HomeFragment()
        transaction?.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
        transaction?.replace(R.id.nav_host_fragment, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

}