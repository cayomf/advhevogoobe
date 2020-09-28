package br.project_advhevogoober_final

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.project_advhevogoober_final.Model.LawyerProfile
import br.project_advhevogoober_final.Model.Message
import br.project_advhevogoober_final.Model.OfficeProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.layout_message_from.view.*
import kotlinx.android.synthetic.main.layout_message_to.view.*
import java.util.*


class ChatActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private val user= FirebaseAuth.getInstance().currentUser!!
    val adapter = GroupAdapter<ViewHolder>()
    private var messages= mutableListOf<br.project_advhevogoober_final.Model.Message>()
    var lawyerProfile: LawyerProfile? =null
    var officeProfile: OfficeProfile? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        var offererId = intent.getStringExtra("offererId")

        db.collection("lawyers").document(offererId).get().addOnSuccessListener {
            if (it.exists()) {
                lawyerProfile=it.toObject(LawyerProfile::class.java)
                supportActionBar?.title=lawyerProfile!!.name
            }
        }.addOnFailureListener{
            Toast.makeText(this,R.string.erro_ao_carregar_perfil, Toast.LENGTH_LONG).show()
        }
        db.collection("offices").document(offererId).get().addOnSuccessListener {
            if (it.exists()){
                officeProfile=it.toObject(OfficeProfile::class.java)
                supportActionBar?.title=officeProfile!!.name
            }
        }.addOnFailureListener{
            Toast.makeText(this,R.string.erro_ao_carregar_perfil, Toast.LENGTH_LONG).show()
        }


        db.collection("user-messages").document(user.uid).collection(offererId).orderBy("data", Query.Direction.ASCENDING).addSnapshotListener{ snapshot, e->
            if(snapshot !=null && !snapshot.isEmpty){
                messages.clear()
                adapter.clear()
                messages.addAll(snapshot!!.toObjects(Message::class.java))
                for (message in messages){
                    if(message.idUserOrigin==user.uid){
                        adapter.add(ChatToItem(message.msgText!!))
                    }
                    else{
                        adapter.add(ChatFromItem(message.msgText!!))
                    }
                    val mLayoutManager = LinearLayoutManager(this)
                    mLayoutManager.stackFromEnd = true
                    my_recycler_view.adapter=adapter
                    my_recycler_view.layoutManager= mLayoutManager
                }
            }
        }

        btnSendMessage.setOnClickListener{
            val chatMessage= Message(eTxtVwSendMessage.text.toString(),Date(),user.uid,offererId)
            eTxtVwSendMessage.setText("")
            db.collection("user-messages").document(user.uid).collection(offererId).add(chatMessage)
            db.collection("user-messages").document(offererId).collection(user.uid).add(chatMessage)

            if(lawyerProfile!=null){
                if(lawyerProfile!!.messagees.isNullOrEmpty()){
                    var list= mutableListOf<String>()
                    list.add(user.uid)
                    lawyerProfile!!.messagees=list
                    db.collection("lawyers").document(offererId).set(lawyerProfile!!).addOnSuccessListener {
//                        Toast.makeText(this,"Id setado com sucesso",Toast.LENGTH_LONG).show()
                    }.addOnFailureListener{
                        Toast.makeText(this,it.toString(),Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    if(!lawyerProfile!!.messagees!!.contains(user.uid)){
                        lawyerProfile!!.messagees!!.add(user.uid)
                        db.collection("lawyers").document(offererId).set(lawyerProfile!!).addOnSuccessListener {
//                            Toast.makeText(this,"Id setado com sucesso",Toast.LENGTH_LONG).show()
                        }.addOnFailureListener{

                            Toast.makeText(this,it.toString(),Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            else if (officeProfile!=null){
                if(officeProfile!!.messagees.isNullOrEmpty()){
                    var list= mutableListOf<String>()
                    list.add(user.uid)
                    officeProfile!!.messagees=list
                    db.collection("offices").document(offererId).set(officeProfile!!).addOnSuccessListener {
//                        Toast.makeText(this,"Id setado com sucesso",Toast.LENGTH_LONG).show()
                    }.addOnFailureListener{
                        Toast.makeText(this,R.string.erro_ao_enviar_mensagem,Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    if(!officeProfile!!.messagees!!.contains(user.uid)){
                        officeProfile!!.messagees!!.add(user.uid)
                        db.collection("offices").document(offererId).set(officeProfile!!).addOnSuccessListener {
//                            Toast.makeText(this,"Id setado com sucesso",Toast.LENGTH_LONG).show()
                        }.addOnFailureListener{
                            Toast.makeText(this,R.string.erro_ao_enviar_mensagem,Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    class ChatFromItem(val text:String):Item<ViewHolder>(){

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.txtVwFrom.text=text
        }
        override fun getLayout(): Int {
            return R.layout.layout_message_from
        }
    }

    class ChatToItem(val text:String):Item<ViewHolder>(){

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.txtVwTo.text=text
        }
        override fun getLayout(): Int {
            return R.layout.layout_message_to
        }
    }
}
