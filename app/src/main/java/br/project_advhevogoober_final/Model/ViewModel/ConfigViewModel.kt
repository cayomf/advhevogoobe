package br.project_advhevogoober_final.Model.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.project_advhevogoober_final.Model.Config

class ConfigViewModel() : ViewModel() {

    var sliderValue = MutableLiveData<Double>()

}