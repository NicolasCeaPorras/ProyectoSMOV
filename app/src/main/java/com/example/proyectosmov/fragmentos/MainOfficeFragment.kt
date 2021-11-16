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
 * Use the [MainOfficeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainOfficeFragment : stepperFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_main_office, container, false)
        // Inflate the layout for this fragment
        return rootView

    }

    override fun onNextButtonHandler(): Boolean {
        var newCompanyOfficeName : String = rootView!!.findViewById<EditText>(R.id.newCompanyOfficeName).text.toString()
        var newCompanyOfficeAddressStreetr : String = rootView!!.findViewById<EditText>(R.id.newCompanyOfficeAddressStreetr).text.toString()
        var newCompanyOfficeAddressApartment : String = rootView!!.findViewById<EditText>(R.id.newCompanyOfficeAddressApartment).text.toString()
        var newCompanyOfficeAddressLocation : String = rootView!!.findViewById<EditText>(R.id.newCompanyOfficeAddressLocation).text.toString()
        var newCompanyOfficeAddressProvince : String = rootView!!.findViewById<EditText>(R.id.newCompanyOfficeAddressProvince).text.toString()
        var newCompanyOfficeAddressPostalCode : String = rootView!!.findViewById<EditText>(R.id.newCompanyOfficeAddressPostalCode).text.toString()
        var newCompanyOfficeAddressCountry : String = rootView!!.findViewById<EditText>(R.id.newCompanyOfficeAddressCountry).text.toString()

        if((newCompanyOfficeName == "") or (newCompanyOfficeAddressStreetr == "") or (newCompanyOfficeAddressPostalCode.length != 5) or (newCompanyOfficeAddressApartment == "") or (newCompanyOfficeAddressPostalCode == "")or (newCompanyOfficeAddressLocation == "")or (newCompanyOfficeAddressProvince == "")or (newCompanyOfficeAddressCountry == "")){
            Toast.makeText(activity,"Todos los campos son obligatorios", Toast.LENGTH_LONG).show()
        } else {
            (activity as NewCompaniesActivity?)!!.activityOffice(newCompanyOfficeAddressApartment, newCompanyOfficeAddressCountry, newCompanyOfficeAddressLocation, newCompanyOfficeAddressPostalCode,newCompanyOfficeAddressProvince, newCompanyOfficeAddressStreetr,newCompanyOfficeName)
        }
        return true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainOfficeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainOfficeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}