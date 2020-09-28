package br.project_advhevogoober_final.Model

import java.io.Serializable

class Solicitation(
    var userEmail:String = "",
    var offerId:String="",
    var accepted:Boolean=false,
    var solitationId:String=""
):Serializable{
    constructor() : this("","",false,"")
}

