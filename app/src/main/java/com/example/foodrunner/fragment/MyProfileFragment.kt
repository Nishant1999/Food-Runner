package com.example.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.foodrunner.R

class MyProfileFragment : Fragment() {

    lateinit var name:TextView
    lateinit var mobileNo:TextView
    lateinit var email:TextView
    lateinit var address:TextView
    private var sharedPreferences: SharedPreferences? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)
        initialize(view)
        return view
    }

    private fun initialize(view:View){
        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        name = view.findViewById(R.id.name)
        mobileNo = view.findViewById(R.id.mobileNo)
        email = view.findViewById(R.id.email)
        address = view.findViewById(R.id.address)

        name.text = sharedPreferences?.getString("Name","user")
        mobileNo.text = sharedPreferences?.getString("email","email")
        email.text = sharedPreferences?.getString("Mobile_Number","mb")
        address.text = sharedPreferences?.getString("address","address")
    }

}