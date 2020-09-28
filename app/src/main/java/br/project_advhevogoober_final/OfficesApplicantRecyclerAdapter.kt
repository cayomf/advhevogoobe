package br.project_advhevogoober_final

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.project_advhevogoober_final.Model.OfficeProfile
import kotlinx.android.synthetic.main.applicant_recycler_item.view.*


class OfficesApplicantRecyclerAdapter(private val offices: List<OfficeProfile>, private val callback: (OfficeProfile) -> Unit) :
    RecyclerView.Adapter<OfficesApplicantRecyclerAdapter.OfficeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):OfficeViewHolder {
        val v = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.applicant_recycler_item, parent, false)
        val viewHolder = OfficeViewHolder(v)
        viewHolder.itemView.setOnClickListener {
            val office = offices[viewHolder.adapterPosition]
            callback(office)
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return offices.size
    }

    override fun onBindViewHolder(holder: OfficeViewHolder, position: Int) {
        //val (name, surname, phone, ssn, oab_code, birthdate, messagees, email) = lawyers[position]
        var office: OfficeProfile = offices[position]

        holder.txtVwName.text = office.name
        holder.txtVwOabCnpj.text = office.businessId
        holder.txtVwEmail.text = office.email

    }

    class OfficeViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val txtVwName: TextView = itemView.applicant_date
        val txtVwOabCnpj: TextView = itemView.applicant_oab_cnpj
        val txtVwEmail: TextView = itemView.applicant_email

    }

}