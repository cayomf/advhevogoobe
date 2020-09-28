package br.project_advhevogoober_final.Service

import br.project_advhevogoober_final.Model.APIResultsObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DAO {

    @GET("v1/address")
    fun show(
        @Query("key") key : String,
        @Query("street") street : String,
        @Query("city") city : String,
        @Query("state") state : String,
        @Query("postalCode") postalCode : String
    ) : Call<APIResultsObject>
}