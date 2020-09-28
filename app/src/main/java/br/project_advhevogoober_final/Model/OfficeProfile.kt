package br.project_advhevogoober_final.Model

import java.io.Serializable

class OfficeProfile(
    name:String?=null,
    val phone:String?=null,
    val businessId:String?=null,
    var messagees:MutableList<String>?=mutableListOf(),
    email:String?=null)
    :User(name,null,null,null,email)