package br.project_advhevogoober_final.Model

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable
import java.util.*

data class Offer(
    var date: Date? = null,
    var jurisdiction: List<Boolean>? = null,
    var price: Double? = null,
    var street: String = "",
    var city: String = "",
    var state: String = "",
    var postalCode: String = "",
    var offerer: String = "",
    var postDate: String="",
    var description: String = "",
    var requirements: String ="",
    var offererId: String = "",
    var idOffer: String? = ""
): Serializable{
    constructor() : this(null,null,null,"","","","","", "", "", "","","")
}