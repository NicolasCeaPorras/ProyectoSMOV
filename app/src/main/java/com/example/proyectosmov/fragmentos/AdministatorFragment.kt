package com.example.proyectosmov.fragmentos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.proyectosmov.NewCompaniesActivity
import com.example.proyectosmov.R
import ivb.com.materialstepper.stepperFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdministatorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdministatorFragment : stepperFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_administator, container, false)
        // Inflate the layout for this fragment
        return rootView
    }

    override fun onNextButtonHandler(): Boolean {
        var newCompanyAdminName : String = rootView!!.findViewById<EditText>(R.id.newCompanyAdminName).text.toString()
        var newCompanyAdminPhoneNumber : String = rootView!!.findViewById<EditText>(R.id.newCompanyAdminPhoneNumber).text.toString()
        var newCompanyAdminEmail : String = rootView!!.findViewById<EditText>(R.id.newCompanyAdminEmail).text.toString()
        var newCompanyAdminUserName : String = rootView!!.findViewById<EditText>(R.id.newCompanyAdminUserName).text.toString()
        var newCompanyAdminPassword : String = rootView!!.findViewById<EditText>(R.id.newCompanyAdminPassword).text.toString()
        var newCompanyAdminVacances : Int = rootView!!.findViewById<EditText>(R.id.newCompanyAdminVacances).text.toString().toInt()


        if((newCompanyAdminName == "") or (newCompanyAdminPhoneNumber == "") or (newCompanyAdminPhoneNumber.length < 9) or (newCompanyAdminEmail == "") or (newCompanyAdminUserName == "")or (newCompanyAdminPassword == "")or (newCompanyAdminVacances == null)){
            Toast.makeText(activity,"Todos los campos son obligatorios", Toast.LENGTH_LONG).show()
            return false
        }
        if((newCompanyAdminPhoneNumber.length < 9)){
            Toast.makeText(activity,"Número de teléfono inválido", Toast.LENGTH_LONG).show()
            return false
        } else {
            (activity as NewCompaniesActivity?)!!.activityAdminUser(newCompanyAdminName,newCompanyAdminUserName,newCompanyAdminEmail,newCompanyAdminPassword,newCompanyAdminPhoneNumber,newCompanyAdminVacances)
            return true
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdministatorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdministatorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}