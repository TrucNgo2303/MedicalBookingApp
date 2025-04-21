package com.example.bookingmedicalapp.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    var email: String? = null
    var type: String? = null

    var full_name: String? = null
    var password: String? = null

    var patientId: Int? = null
    var fee: String? = null

    //Doctor Detail
    val doctor_id_1 = MutableLiveData<Int?>()
    val doctor_id_2 = MutableLiveData<Int?>()
    val doctor_id_3 = MutableLiveData<Int?>()
    val doctor_id_4 = MutableLiveData<Int?>()
    val doctor_id_5 = MutableLiveData<Int?>()

    val doctor_id_main = MutableLiveData<Int?>()

    //Specialist
    var specialistId = MutableLiveData<Int?>()

    var specialist_id_1 = MutableLiveData<Int?>()
    var specialist_id_2 = MutableLiveData<Int?>()
    var specialist_id_3 = MutableLiveData<Int?>()

    //For Book Appointment
    var doctor_name: String? = null
    var doctor_specialy: String? = null
    var doctor_qualification: String? = null
    var doctor_avatar: String? = null

    //For Bill
    var timeChoose: String? = null
    var dateChoose: String? = null
    var noteBill: String? = null


}
