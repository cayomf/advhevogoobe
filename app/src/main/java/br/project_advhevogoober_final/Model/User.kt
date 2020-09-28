package br.project_advhevogoober_final.Model

import com.google.firebase.firestore.GeoPoint

open class User(
    val name:String?=null,
    val config:Config? = null,
    val g: String? = null,
    val l: GeoPoint? = null,
    val email:String?=null,
    val street:String? = null,
    val city:String? = null,
    val state:String? = null,
    val postalCode: String? = null
)