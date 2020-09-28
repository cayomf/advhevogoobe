package br.project_advhevogoober_final.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {
    companion object {

        private var INSTANCE: Retrofit? = null;

        fun getInstance() : Retrofit? {
            if (INSTANCE == null) {
                INSTANCE = Retrofit.Builder()
                    .baseUrl("https://open.mapquestapi.com/geocoding/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return INSTANCE
        }

    }
}