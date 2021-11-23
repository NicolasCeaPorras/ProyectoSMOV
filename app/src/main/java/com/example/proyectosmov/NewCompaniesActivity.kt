package com.example.proyectosmov

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.proyectosmov.fragmentos.AdministatorFragment
import com.example.proyectosmov.fragmentos.CompanyDataFragment
import com.example.proyectosmov.fragmentos.MainOfficeFragment
import com.example.proyectosmov.fragmentos.ConfirmRegisterFragment
import ivb.com.materialstepper.progressMobileStepper;
import com.example.proyectosmov.dominio.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.delay
import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.TimeUnit


class NewCompaniesActivity : progressMobileStepper() {

    var stepperFragmentList: MutableList<Class<*>> = ArrayList()
    var company : Company? = null
    var office : Office? = null
    var userAmin : User? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_new_companies)
//        supportActionBar!!.hide()
//    }

    override fun onStepperCompleted() {
        val db = FirebaseFirestore.getInstance()
        db.collection("companies").whereEqualTo("name", this.company!!.name).limit(1) .get().addOnCompleteListener({task ->
            if((task.isSuccessful) and (task.result?.isEmpty == false)){
                Toast.makeText(this,"La compañia con ese nombre ya existe", Toast.LENGTH_LONG).show()
            } else {
                db.collection("companies").add(this.company!!).addOnSuccessListener({
                    showMenu(this.userAmin!!.email.toString(),it.id.toString())
                })
            }
        })
        Log.i("NewCompaniesActivitt","LLega")
    }

    override fun init(): MutableList<Class<*>> {
        Log.i("NewCompaniesActivitt","asdafafsvadfsv")
        stepperFragmentList.add(CompanyDataFragment::class.java)
        stepperFragmentList.add(MainOfficeFragment::class.java)
        stepperFragmentList.add(AdministatorFragment::class.java)
        stepperFragmentList.add(ConfirmRegisterFragment::class.java)
        return stepperFragmentList
    }

    fun activityCompany(name : String, sector : String, phone_number : String){
        if (this.company == null){
            this.company = Company(name, sector, phone_number)
        } else {
            this.company!!.name = name
            this.company!!.sector = sector
            this.company!!.phone_number = phone_number
        }
    }

    fun activityAdminUser(name : String, user_name : String, email : String, password : String, phone_number : String){
        val md = MessageDigest.getInstance("MD5")
        val hashPassword =  BigInteger(1, md.digest(password.toByteArray())).toString(16).padStart(32, '0')
        if (this.userAmin == null){
            this.userAmin = User(name, user_name,"", email,hashPassword,phone_number,admin=true,active_input = null,holiday_periods = null, scheduled_tasks = null, time_records = null)
            this.company!!.users = mutableListOf<User>()
            this.company!!.users!!.add(this.userAmin!!)
        } else {
            this.userAmin!!.name = name
            this.userAmin!!.user_name = user_name
            this.userAmin!!.email = email
            this.userAmin!!.password = hashPassword
            this.userAmin!!.phone_number = phone_number
            this.company!!.users!![0] = this.userAmin!!
        }

    }
    fun activityOffice(address_apartment : String, address_country : String, address_location : String, address_postal_code : String, address_province : String, address_street : String, name : String){
        val md = MessageDigest.getInstance("MD5")
        val idHashOffice =  BigInteger(1, md.digest(name.toByteArray())).toString(16).padStart(32, '0')
        if (this.office == null){
            this.office = Office(address_apartment, address_country, address_location,address_postal_code,address_province,address_street,idHashOffice,name)
            this.company!!.offices = mutableListOf<Office>()
            this.company!!.offices!!.add(this.office!!)
        } else {
            this.office!!.address_apartment = address_apartment
            this.office!!.address_country = address_country
            this.office!!.address_location = address_location
            this.office!!.address_postal_code = address_postal_code
            this.office!!.address_province = address_province
            this.office!!.address_street = address_street
            this.office!!.idHashOffice = idHashOffice
            this.office!!.name = name
            this.company!!.offices!![0] = this.office!!
        }
    }
    private fun showMenu(email: String, companyId : String){
        val menuIntent = Intent(this, Menu::class.java).apply{
            putExtra("email", email)
            putExtra("companyId", companyId)
        }
        startActivity(menuIntent)
    }

}
