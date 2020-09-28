package br.project_advhevogoober_final

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.project_advhevogoober_final.Model.ChatUser
import kotlinx.android.synthetic.main.layout_messenger.view.*

class ChatRoomsAdapter(private val users:List<ChatUser>,private val callback:(ChatUser)->Unit) : RecyclerView.Adapter<ChatRoomsAdapter.ChatRoomsViewHolder>() {

    class ChatRoomsViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtVwName: TextView =itemView.txtVwChatUserName
        val imgVwUser: ImageView? =itemView.imgVwChatUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomsViewHolder
    {
        val v = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.layout_messenger,parent,false)
        val viewHolder=ChatRoomsViewHolder(v)
        viewHolder.itemView.setOnClickListener {val user=users[viewHolder.adapterPosition]
            callback(user)}
        return viewHolder
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ChatRoomsAdapter.ChatRoomsViewHolder, position: Int) {
        val text=users[position]
        holder.txtVwName.text=text.name
        holder.imgVwUser!!.setImageBitmap(text.profileImage)
    }
}