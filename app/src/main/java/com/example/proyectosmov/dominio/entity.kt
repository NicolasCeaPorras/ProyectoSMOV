package com.example.proyectosmov.dominio

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

//Data clases que representan el dominio para las consultas

data class Company(
    var name: String? = null,
    var sector: String? = null,
    var phone_number: String? = null,
    var offices: MutableList<Office>? = null,
    var users: MutableList<User>? = null
)
data class User(
    var name: String? = null,
    var user_name: String? = null,
    var jefe: String? = null,
    var email: String? = null,
    var password: String? = null,
    var phone_number: String? = null,
    var admin: Boolean? = null,
    var active_input: ActiveInput? = null,
    var holiday_periods: MutableList<HolidayPeriod>? = null,
    var scheduled_tasks: MutableList<ScheduledTask>? = null,
    var time_records: MutableList<TimeRecord>? = null

)
data class Office(
    var address_apartment: String? = null,
    var address_country: String? = null,
    var address_location: String? = null,
    var address_number: String? = null,
    var address_postal_code: String? = null,
    var address_province: String? = null,
    var address_street: String? = null,
    var idHashOffice: String? = null,
    var name: String? = null,
)
data class ActiveInput(
    var creation_date: Date? = null,
    var office_idHash: String? = null,
    var start_hour: Date? = null,
)
data class HolidayPeriod(
    var creation_date: Date? = null,
    var start_date: Date? = null,
    var end_date: Date? = null,
)
data class ScheduledTask(
    var creation_date: Date? = null,
    var description: String? = null,
    var task_date: Date? = null,
) {
    override fun toString(): String = description.toString()
}
data class TimeRecord(
    var creation_date: Date? = null,
    var start_hour: Date? = null,
    var end_hour: Date? = null,
    var office_idHash: String? = null
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
    var time_records_return: MutableList<TimeRecord> = mutableListOf()

    if(user.time_records != null){
        for(time_record in user.time_records!!){
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


fun gettasksByUserAndDate(user : User, date : Date) : MutableList<ScheduledTask>{
    var scheduled_tasks_return: MutableList<ScheduledTask> = mutableListOf()
    if(user.scheduled_tasks != null){
        for(task in user.scheduled_tasks!!){
            val formatDate = SimpleDateFormat("dd/MM/yyyy")
            val task_date_string : String = formatDate.format(date)
            val filter_date_string : String = formatDate.format(task.task_date!!)

            if(task_date_string == filter_date_string){
                scheduled_tasks_return.add(task)
            }
        }

    }
    return scheduled_tasks_return
}