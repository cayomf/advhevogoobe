package br.project_advhevogoober_final

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login_register.*

class LoginRegisterActivity : AppCompatActivity() {

    val AUTHUI_REQUEST_CODE=73
    val LOCATION_REQUEST_CODE = 31
    private var TAG ="LoginRegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
        btnLogReg.isEnabled = false
        checkLocationPermission()

        if(FirebaseAuth.getInstance().currentUser!=null){
            intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



    fun handleLoginRegister(view: View){
        val providers= arrayListOf(AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build())

        intent=Intent(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.common_google_signin_btn_icon_dark)
                .setTheme(R.style.AppTheme)
                .build()
        )
        startActivityForResult(intent,AUTHUI_REQUEST_CODE)
    }

    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("ronaldo", "ronaldinho");
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                setSnackbar()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            btnLogReg.isEnabled = true
        }
    }

    fun setSnackbar() {
        var snackbar = Snackbar.make(loginLayout, R.string.snackbar_explanation, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("OK") {
            snackbar.dismiss()
        }.show()
        var txtView: TextView = snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        txtView.maxLines = 5
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED)) {

                    var showRationale: Boolean = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!showRationale) {
                        setSnackbar()
                    } else if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[0]) || Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[0])) {
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                            LOCATION_REQUEST_CODE)
                        // user did NOT check "never ask again"
                        // this is a good place to explain the user
                        // why you need the permission and ask if he wants
                        // to accept it (the rationale)
                    }
                } else {
                    btnLogReg.isEnabled = true
                }
                return
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        setContentView(R.layout.layout_prep)
        when (requestCode){
            AUTHUI_REQUEST_CODE -> {
                if(resultCode== Activity.RESULT_OK){
                    //novo usuário ou usuário velho
                    var user=FirebaseAuth.getInstance().currentUser
                    if(user!!.metadata!!.creationTimestamp== user.metadata!!.lastSignInTimestamp){
                        intent=Intent(this,FirstTimeUserActivity::class.java)
                        startActivity(intent)
                        this.finish()
                    }
                    else{
                        intent=Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        this.finish()
                    }
                }
                else {
                    //login falhou
                    var response = IdpResponse.fromResultIntent(data)
                    if (response == null) {
                        Log.d(TAG, "onActivityResult: o usuário cancelou o pedido de login")
                    }
                    else{
                        Log.e(TAG,"OnActivityResult: ",response.error)
                    }
                }
            }
        }
    }
}
