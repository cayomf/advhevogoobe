package br.project_advhevogoober_final

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 5000 // 5 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        changeActivity()
    }

    private fun changeActivity(){
        Handler().postDelayed ({
            val launchNextActivity: Intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(launchNextActivity)
            overridePendingTransition(0,0)
        }, SPLASH_TIME_OUT)
    }
}
//overridePendingTransition(0,0)
//finish()