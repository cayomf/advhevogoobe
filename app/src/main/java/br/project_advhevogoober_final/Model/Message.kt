package br.project_advhevogoober_final.Model

import java.util.*

data class Message(val msgText:String?=null, val data: Date?=null, val idUserOrigin:String?=null, val idUserReceiver: String?=null)