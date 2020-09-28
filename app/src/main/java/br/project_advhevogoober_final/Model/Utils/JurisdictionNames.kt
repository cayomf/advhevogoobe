package br.project_advhevogoober_final.Model.Utils

import android.content.Context
import br.project_advhevogoober_final.R

object JurisdictionNames {

    private val names: List<Int> = listOf(
        R.string.jurisdiction1,
        R.string.jurisdiction2,
        R.string.jurisdiction3,
        R.string.jurisdiction4,
        R.string.jurisdiction5,
        R.string.jurisdiction6
    )

    private fun getNamesFromStringRes(ctx: Context): List<String> {
        val namesFromRes = mutableListOf<String>()

        for (res in names) {
            val name = ctx.getString(res)
            namesFromRes.add(name)
        }

        return namesFromRes
    }

    fun generateJurisdictionName(jurisdiction: List<Boolean>?, ctx: Context): String {
        var jurisdictionName = ""
        val namesFromRes = getNamesFromStringRes(ctx)
        for (bool in jurisdiction!!) {
            if (bool) {
                jurisdictionName = namesFromRes[jurisdiction.indexOf(bool)]
            }
        }
        return jurisdictionName
    }

}