package br.project_advhevogoober_final

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.project_advhevogoober_final.Model.Utils.JurisdictionNames
import br.project_advhevogoober_final.Model.Offer
import kotlinx.android.synthetic.main.offer_recycle_item.view.*

class OfferRecycleAdapter(private val offers: List<Offer>, private val callback: (Offer) -> Unit) :
    RecyclerView.Adapter<OfferRecycleAdapter.OfferViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val v = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.offer_recycle_item, parent, false)
        val viewHolder = OfferViewHolder(v)
        viewHolder.itemView.setOnClickListener {
            val offer = offers[viewHolder.adapterPosition]
            callback(offer)
        }
        return viewHolder
    }


    override fun getItemCount(): Int {
        return offers.size
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val (date, jurisdiction, price, location, offerer, postDate, description, requirements) = offers[position]
        val context = holder.itemView.context
        var jurisdictionName = JurisdictionNames.generateJurisdictionName(jurisdiction, context)
        val dateFormat = DateFormat.getDateFormat(context)
        holder.txtVwLocation.text = location
        holder.txtVwDate.text = dateFormat.format(date)
        holder.txtVwOfferer.text = offerer
        holder.txtVwPrice.text = price.toString()
        holder.txtVwJurisdiction.text = jurisdictionName
    }



    class OfferViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val txtVwLocation: TextView = itemView.offer_location
        val txtVwDate: TextView = itemView.offer_date
        val txtVwOfferer: TextView = itemView.offer_offerer
        val txtVwPrice: TextView = itemView.offer_price
        val txtVwJurisdiction: TextView = itemView.offer_jurisdiction
    }
}