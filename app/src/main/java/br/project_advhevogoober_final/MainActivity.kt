package br.project_advhevogoober_final

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import br.project_advhevogoober_final.R.id.toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val manager = supportFragmentManager
    val db = FirebaseFirestore.getInstance()
    private lateinit var mPreferences: SharedPreferences
    private val PROFILE_CHECK_KEY:String="teste4"
    private val PROFILE_TYPE_KEY:String="tipoP"
    private val mSharedPrefFile:String="br.project_advhevogoober_final"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var aaa = FirebaseAuth.getInstance().currentUser


        if (aaa == null) {
            intent = Intent(this, LoginRegisterActivity::class.java)
            var teste= intent
            startActivity(teste)
            finish()
        }

        mPreferences=getSharedPreferences(mSharedPrefFile, Context.MODE_PRIVATE)
        var checkFirstTimeUser=(mPreferences.getBoolean(PROFILE_CHECK_KEY,false))
        if (checkFirstTimeUser){
            intent=Intent(this,FirstTimeUserActivity::class.java)
            var teste= intent
            startActivity(intent)
            finish()
        }
        carregarHome()
        mPreferences=this.getSharedPreferences(mSharedPrefFile, Context.MODE_PRIVATE)
        val toolbar: Toolbar = findViewById(toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                val transaction = manager.beginTransaction()
                transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                val fragment = HomeFragment()
                var f: Fragment? =manager.findFragmentById(R.id.nav_host_fragment)
                if(f !is HomeFragment){
                    transaction.replace(R.id.nav_host_fragment, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            R.id.nav_myOffers ->{
                val transaction = manager.beginTransaction()
                transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                val fragment = MyOffersFragment()
                var f: Fragment? =manager.findFragmentById(R.id.nav_host_fragment)
                if(f !is MyOffersFragment){
                    transaction.replace(R.id.nav_host_fragment, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            R.id.nav_config -> {
                val transaction = manager.beginTransaction()
                transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                val fragment = ConfigFragment()
                var f: Fragment? =manager.findFragmentById(R.id.nav_host_fragment)
                if(f !is ConfigFragment){
                    transaction.replace(R.id.nav_host_fragment, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            R.id.nav_mySolicitations -> {
                val transaction = manager.beginTransaction()
                transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                val fragment = MySolicitationsFragment()
                var f: Fragment? =manager.findFragmentById(R.id.nav_host_fragment)
                if(f !is MySolicitationsFragment){
                    transaction.replace(R.id.nav_host_fragment, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            R.id.nav_profile -> {
                db.collection("lawyers").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
                    if (it.exists()) {
                        var preferencesEditor:SharedPreferences.Editor=mPreferences.edit()
                        preferencesEditor.putBoolean(PROFILE_TYPE_KEY,false)
                        preferencesEditor.apply()
                        val transaction = manager.beginTransaction()
                        transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                        val fragment = LawyerProfileFragment()
                        var f: Fragment? =manager.findFragmentById(R.id.nav_host_fragment)
                        if(f !is LawyerProfileFragment){
                            transaction.replace(R.id.nav_host_fragment, fragment)
                            transaction.addToBackStack(null)
                            transaction.commit()
                        }
                    }
                }.addOnFailureListener{
                    Toast.makeText(this,R.string.erro_ao_carregar_perfil,Toast.LENGTH_LONG).show()
                }
                db.collection("offices").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
                    if (it.exists()){
                        var preferencesEditor:SharedPreferences.Editor=mPreferences.edit()
                        preferencesEditor.putBoolean(PROFILE_TYPE_KEY,true)
                        preferencesEditor.apply()
                        val transaction = manager.beginTransaction()
                        transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                        val fragment = OfficeProfileFragment()
                        var f: Fragment? =manager.findFragmentById(R.id.nav_host_fragment)
                        if(f !is OfficeProfileFragment){
                            transaction.replace(R.id.nav_host_fragment, fragment)
                            transaction.addToBackStack(null)
                            transaction.commit()
                        }
                    }
                }.addOnFailureListener{
                    Toast.makeText(this,R.string.erro_ao_carregar_perfil,Toast.LENGTH_LONG).show()
                }
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                if (FirebaseAuth.getInstance().currentUser == null) {
                    intent = Intent(this, LoginRegisterActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            R.id.action_check_chat ->{
                intent = Intent(this, ChatRoomsActivity::class.java)
                startActivity(intent)
//                overridePendingTransition(R.anim.enter_right_to_left,R.anim.exit_right_to_left)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun carregarHome() {
        val transaction = manager.beginTransaction()
        val fragment = HomeFragment()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.setCustomAnimations(R.anim.nav_default_pop_enter_anim,R.anim.nav_default_pop_exit_anim)
        transaction.commit()
    }
}
//    fun tipoPerfil():Boolean?{
//        var tipo:Boolean?=null
//        db.collection("lawyers").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
//            tipo=true
//            return@addOnSuccessListener
//        }.addOnFailureListener{
//            Toast.makeText(this,"Não é um lawyer",Toast.LENGTH_LONG).show()
//        }
//        db.collection("offices").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
//            tipo=false
//            return@addOnSuccessListener
//        }
//
//        return tipo
//    }
//        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
//        val navView: NavigationView = findViewById(R.id.nav_view)
//        val navController = findNavController(R.id.nav_host_fragment)
//        // Passing each menu ID activity_offer_details a set of Ids because each
//        // menu should be considered activity_offer_details top level destinations.
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
//                R.id.nav_tools, R.id.nav_share, R.id.nav_send
//            ), drawerLayout
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }

