package br.project_advhevogoober_final.Model

import java.util.*

class LawyerProfile(
    name:String?=null,
    val surname:String?=null,
    val phone:String?=null,
    val ssn:String?=null,
    val oab_code:String?=null,
    val birthdate:Date?=null,
    var messagees:MutableList<String>?=null,
    email:String?=null)
    :User(name,null,null,null,email)