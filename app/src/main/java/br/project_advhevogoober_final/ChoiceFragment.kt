package br.project_advhevogoober_final

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_choice.view.*

class ChoiceFragment() :Fragment(), Parcelable {


    val TAG ="ChoiceFragment"
    private lateinit var mPreferences: SharedPreferences
    private val PROFILE_CHECK_KEY:String="teste4"
    private val mSharedPrefFile:String="br.project_advhevogoober_final"

    constructor(parcel: Parcel) : this() {
    }


    override fun onAttach(context: Context) {
        Log.d(TAG,"onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate")
        super.onCreate(savedInstanceState)
        mPreferences=this.activity!!.getSharedPreferences(mSharedPrefFile, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG,"onCreateView")
        container?.removeAllViews()
        val view: View = inflater.inflate(R.layout.fragment_choice, container,false)
        var preferencesEditor:SharedPreferences.Editor=mPreferences.edit()
        preferencesEditor.putBoolean(PROFILE_CHECK_KEY,true)
        preferencesEditor.apply()

        view.lawyer_choice_button.setOnClickListener{
            val transaction=fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
            val lawyerFragment=LawyerChoiceFragment()
            var f: Fragment? =fragmentManager!!.findFragmentById(R.id.nav_host_fragment)
            if(f !is LawyerChoiceFragment){
                transaction.replace(R.id.choice, lawyerFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

        view.office_choice_button.setOnClickListener{
            val transaction=fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
            val officeFragment=OfficeChoiceFragment()
            var f: Fragment? =fragmentManager!!.findFragmentById(R.id.nav_host_fragment)
            if(f !is OfficeChoiceFragment){
                transaction.replace(R.id.choice, officeFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        return view
    }

    override fun onPause() {
        super.onPause()
        var preferencesEditor:SharedPreferences.Editor=mPreferences.edit()
        var checkFirstTimeUser=(mPreferences.getBoolean(PROFILE_CHECK_KEY,true))
        if (!checkFirstTimeUser){}
        else{
            preferencesEditor.putBoolean(PROFILE_CHECK_KEY,true)
            preferencesEditor.apply()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChoiceFragment> {
        override fun createFromParcel(parcel: Parcel): ChoiceFragment {
            return ChoiceFragment(parcel)
        }

        override fun newArray(size: Int): Array<ChoiceFragment?> {
            return arrayOfNulls(size)
        }
    }
}
