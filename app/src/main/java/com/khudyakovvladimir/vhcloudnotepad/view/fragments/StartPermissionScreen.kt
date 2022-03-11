package com.khudyakovvladimir.vhcloudnotepad.view.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vhcloudnotepad.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.khudyakovvladimir.vhcloudnotepad.utils.DBHelper
import com.khudyakovvladimir.vhcloudnotepad.utils.PermissionHelper

class StartPermissionScreen : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.start_permission_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonOk = view.findViewById<Button>(R.id.button)
        val permissionHelper = activity?.let { PermissionHelper(it) }
        val dbHelper = activity?.let { DBHelper(it) }

        val bottomNavigationView = activity!!.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.visibility = View.INVISIBLE

        buttonOk.setOnClickListener {
            permissionHelper?.checkPermissions()
            dbHelper?.createDatabase()

            val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putInt("startDestinationChanged", 1)
            editor.putString("language", "eng")
            editor.putString("textSize", "medium")
            editor.putString("city", "Moscow")
            editor.putString("countryCode", "RU")
            editor.apply()

            bottomNavigationView.visibility = View.VISIBLE

            findNavController().navigate(R.id.notes)
        }
    }
}