package com.example.proyectosmov.fragmentos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.example.proyectosmov.NewCompaniesActivity
import com.example.proyectosmov.R
import com.google.firebase.firestore.FirebaseFirestore
import ivb.com.materialstepper.stepperFragment
import java.util.*




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CompanyDataFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompanyDataFragment : stepperFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var rootView: View? = null
    var sectorSelected : String = ""

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
        //Actualizar Spinner
        rootView = inflater.inflate(R.layout.fragment_company_data, container, false)

        var list_of_items =getArrayOfSectores()
        var spinner : Spinner = rootView!!.findViewById<Spinner>(R.id.newCompanySector) as Spinner
        spinner?.adapter = ArrayAdapter(activity?.applicationContext!!, R.layout.support_simple_spinner_dropdown_item, list_of_items) as SpinnerAdapter
        spinner?.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
//                println("erreur")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position).toString()
                sectorSelected = type.toString()
//                Toast.makeText(activity,type, Toast.LENGTH_LONG).show()
//                prin+tln(type)
            }

        }

        return rootView
    }

    override fun onNextButtonHandler(): Boolean {
        var newCompanyCompanyName : EditText = rootView!!.findViewById<EditText>(R.id.newCompanyCompanyName) as EditText
        var companyName : String = newCompanyCompanyName.text.toString()
        var newCompanyPhoneNumber : EditText = rootView!!.findViewById<EditText>(R.id.newCompanyPhoneNumber) as EditText
        var companyPhoneNumber : String = newCompanyPhoneNumber.text.toString()

        if((companyName == "") or (companyPhoneNumber == "") or (companyPhoneNumber.length < 9) or (sectorSelected == "")){
            Toast.makeText(activity,"Todos los campos son obligatorios", Toast.LENGTH_LONG).show()
        } else {
            (activity as NewCompaniesActivity?)!!.activityCompany(companyName,sectorSelected,companyPhoneNumber)
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
         * @return A new instance of fragment CompanyDataFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CompanyDataFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun getArrayOfSectores(): Array<String> {
        return arrayOf(
            "Comercio Al Por Mayor Y Al Por Menor; Reparación De Vehículos De Motor Y Motocicletas",
            "Construcción",
            "Hostelería",
            "Actividades Profesionales, Científicas Y Técnicas",
            "Industria Manufacturera",
            "Actividades Inmobiliarias",
            "Transporte Y Almacenamiento",
            "Otros Servicios",
            "Actividades Administrativas Y Servicios Auxliares",
            "Información Y Comunicaciones",
            "Agricultura, Ganadería, Silvicultura Y Pesca",
            "Educación",
            "Actividades Financieras Y De Seguros",
            "Actividades Sanitarias Y De Servicios Sociales",
            "Actividades Artísticas, Recreativas Y De Entrenimiento",
            "Suministro De Energía Eléctrica, Gas, Vapor Y Aire Acondicionado",
            "Administración Pública Y Defensa; Seguridad Social Obligatoria",
            "Industrias Extractivas",
        )
    }
}