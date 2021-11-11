package com.example.proyectosmov.dominio

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

//Data clases que representan el dominio para las consultas

data class Company(
    val name: String? = null,
    val sector: String? = null,
    val phone_number: String? = null,
    val offices: List<Office>? = null,
    val users: List<User>? = null
)
data class User(
    val name: String? = null,
    val user_name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val phone_number: String? = null,
    val admin: Boolean? = null,
    val active_input: ActiveInput? = null,
    val holiday_periods: List<HolidayPeriod>? = null,
    val scheduled_tasks: List<ScheduledTask>? = null,
    val time_records: List<TimeRecord>? = null

)
data class Office(
    val address_apartment: String? = null,
    val address_country: String? = null,
    val address_location: String? = null,
    val address_number: String? = null,
    val address_postal_code: String? = null,
    val address_province: String? = null,
    val address_street: String? = null,
    val idHashOffice: String? = null,
    val name: String? = null,
)
data class ActiveInput(
    val creation_date: Date? = null,
    val office_idHash: String? = null,
    val start_hour: Date? = null,
)
data class HolidayPeriod(
    val creation_date: Date? = null,
    val start_date: Date? = null,
    val end_date: Date? = null,
)
data class ScheduledTask(
    val creation_date: Date? = null,
    val description: String? = null,
    val task_date: Date? = null,
)
data class TimeRecord(
    val creation_date: Date? = null,
    val start_hour: Date? = null,
    val end_hour: Date? = null,
    val office_idHash: String? = null
)



//Funciones para filtar
// Dada una compañia, devolver el usuario por email:
fun getUserByEmail(comp : Company, email : String): User? {
    for (user in comp.users!!){
        if(user.email == email)
            return user
    }
    return null
}


fun getUserPresencia(month: Int, year: Int, user: User) :MutableList<TimeRecord>{
    val time_records_return: MutableList<TimeRecord> = mutableListOf()

    if(user.time_records != null){
        for(time_record in user.time_records){
            val formatDate = SimpleDateFormat("yyyy")
            val time_record_year : Int = formatDate.format(time_record.creation_date!!).toInt() //Lo hago de esta forma porque la siguiente linea no funcionaba bien
            //val time_record_year : Int = time_record.creation_date!!.year
            val time_record_month : Int = time_record.creation_date!!.month
            Log.i("PresenciaActivity", time_record_year.toString())
            Log.i("PresenciaActivity", year.toString())
            Log.i("PresenciaActivity", time_record_month.toString())
            Log.i("PresenciaActivity", month.toString())

            if((time_record_year == year ) and (time_record_month == month)){
                time_records_return.add(time_record)
            }
        }

    }
    return time_records_return
}
