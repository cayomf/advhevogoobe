package br.project_advhevogoober_final

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import br.project_advhevogoober_final.Model.LawyerProfile
import kotlinx.android.synthetic.main.activity_first_time_user.*

class FirstTimeUserActivity : AppCompatActivity() {

    val manager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time_user)
        loadChoiceFragment()
    }

    fun loadChoiceFragment() {
        //Inicia o fragment na tela
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.choice, ChoiceFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

}
