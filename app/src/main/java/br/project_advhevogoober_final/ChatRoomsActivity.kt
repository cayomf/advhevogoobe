package br.project_advhevogoober_final

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import br.project_advhevogoober_final.Model.ChatUser
import br.project_advhevogoober_final.Model.LawyerProfile
import br.project_advhevogoober_final.Model.OfficeProfile
import br.project_advhevogoober_final.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat_rooms.*

class ChatRoomsActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private val user= FirebaseAuth.getInstance().currentUser!!
    private var userChatRooms= mutableListOf<User>()
    var storageReference= FirebaseStorage.getInstance().reference
    var userList= mutableListOf<ChatUser>()
    var adapter=ChatRoomsAdapter(userList,this::detalhesContato)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_rooms)
        db.collection("offices").document(user.uid).get().addOnSuccessListener {
            if(it.exists()) {
                var officeProfile=it.toObject(OfficeProfile::class.java)
                if (!officeProfile!!.messagees.isNullOrEmpty()){
                    for(messagee in officeProfile!!.messagees!!){
                        var userOffice=ChatUser()
                        var userLawyer=ChatUser()
                        db.collection("offices").document(messagee).get().addOnSuccessListener {result ->
                            if(result.exists()){
                                var chatOffice=result.toObject(OfficeProfile::class.java)
                                userOffice.name=chatOffice!!.name
                                userOffice.userId=messagee
                                var tarefa=storageReference.child("profileImages/"+messagee).getBytes(1024*1024)
                                tarefa.addOnSuccessListener {byteArray->
                                    var imagem= BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                                    userOffice.profileImage=imagem
                                    userList.add(userOffice)
                                    recycler_view_chat_rooms.adapter=adapter
                                    recycler_view_chat_rooms.layoutManager=LinearLayoutManager(this)
                                }
                            }
                        }
                        db.collection("lawyers").document(messagee).get().addOnSuccessListener {result ->
                            if (result.exists()){
                                var chatLawyer=result.toObject(LawyerProfile::class.java)
                                userLawyer.name=chatLawyer!!.name
                                userLawyer.userId=messagee
                                var tarefa=storageReference.child("profileImages/"+messagee).getBytes(1024*1024)
                                tarefa.addOnSuccessListener {byteArray->
                                    var imagem= BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                                    userLawyer.profileImage=imagem
                                    userList.add(userLawyer)
                                    recycler_view_chat_rooms.adapter=adapter
                                    recycler_view_chat_rooms.layoutManager=LinearLayoutManager(this)
                                }
                            }
                        }
                    }
                }
            }
        }
        db.collection("lawyers").document(user.uid).get().addOnSuccessListener {
            if(it.exists()) {
                var lawyerProfile=it.toObject(LawyerProfile::class.java)
                if (!lawyerProfile!!.messagees.isNullOrEmpty()){
                    for(messagee in lawyerProfile!!.messagees!!){
                        var userOffice=ChatUser()
                        var userLawyer=ChatUser()
                        db.collection("offices").document(messagee).get().addOnSuccessListener {result ->
                            if(result.exists()){
                                var chatOffice=result.toObject(OfficeProfile::class.java)
                                userOffice.name=chatOffice!!.name
                                userOffice.userId=messagee
                                var tarefa=storageReference.child("profileImages/"+messagee).getBytes(1024*1024)
                                tarefa.addOnSuccessListener {byteArray->
                                    var imagem= BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                                    userOffice.profileImage=imagem
                                    userList.add(userOffice)
                                    recycler_view_chat_rooms.adapter=adapter
                                    recycler_view_chat_rooms.layoutManager=LinearLayoutManager(this)
                                }
                            }
                        }
                        db.collection("lawyers").document(messagee).get().addOnSuccessListener {result ->
                            if (result.exists()){
                                var chatLawyer=result.toObject(LawyerProfile::class.java)
                                userLawyer.name=chatLawyer!!.name
                                userLawyer.userId=messagee
                                var tarefa=storageReference.child("profileImages/"+messagee).getBytes(1024*1024)
                                tarefa.addOnSuccessListener {byteArray->
                                    var imagem= BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                                    userLawyer.profileImage=imagem
                                    userList.add(userLawyer)
                                    recycler_view_chat_rooms.adapter=adapter
                                    recycler_view_chat_rooms.layoutManager=LinearLayoutManager(this)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    fun detalhesContato(user:ChatUser){
        var intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("offererId",user.userId)
        startActivity(intent)
    }
}
