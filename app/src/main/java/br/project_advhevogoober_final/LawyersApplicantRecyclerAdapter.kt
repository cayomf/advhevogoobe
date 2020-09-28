package br.project_advhevogoober_final

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.project_advhevogoober_final.Model.LawyerProfile
import kotlinx.android.synthetic.main.applicant_recycler_item.view.*

class LawyersApplicantRecyclerAdapter(private val lawyers: List<LawyerProfile>, private val callback: (LawyerProfile) -> Unit) :
    RecyclerView.Adapter<LawyersApplicantRecyclerAdapter.LawyerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LawyersApplicantRecyclerAdapter.LawyerViewHolder {
        val v = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.applicant_recycler_item, parent, false)
        val viewHolder = LawyerViewHolder(v)
                viewHolder.itemView.setOnClickListener {
            val lawyer = lawyers[viewHolder.adapterPosition]
            callback(lawyer)
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return lawyers.size
    }

    override fun onBindViewHolder(holder: LawyersApplicantRecyclerAdapter.LawyerViewHolder, position: Int) {
        //val (name, surname, phone, ssn, oab_code, birthdate, messagees, email) = lawyers[position]
        var lawyer: LawyerProfile = lawyers[position]

        holder.txtVwName.text = lawyer.name
        holder.txtVwOabCnpj.text = lawyer.oab_code
        holder.txtVwEmail.text = lawyer.email

    }
    class LawyerViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val txtVwName: TextView = itemView.applicant_date
        val txtVwOabCnpj: TextView = itemView.applicant_oab_cnpj
        val txtVwEmail: TextView = itemView.applicant_email

    }

}