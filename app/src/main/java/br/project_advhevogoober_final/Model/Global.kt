package br.project_advhevogoober_final.Model

import android.app.Application


class Global {

    companion object{
        private var instance:Global? = null
        fun getGlobalInstance():Global{
            if (instance == null){
                instance = Global()
            }
            return instance!!
        }
    }
    var globalOffer:Offer = Offer()

    fun setOffer(globalOffer: Offer){
        this.globalOffer = globalOffer
    }

    fun getOffer(): Offer {
        return globalOffer
    }
}