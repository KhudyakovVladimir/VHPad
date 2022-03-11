package com.khudyakovvladimir.vhcloudnotepad.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.vhcloudnotepad.R
import kotlin.properties.Delegates

class Settings: Fragment(){

    private lateinit  var sharedPreferences: SharedPreferences
    private lateinit var switch: Switch
    private lateinit var buttonApplyChanges: Button
    private var switchOff: Boolean = true
    private var textForSwitch = "On"
    private lateinit var spinnerLanguage: Spinner
    private lateinit var spinnerTextSize: Spinner
    private lateinit var textViewSettings: TextView
    private lateinit var textViewSound: TextView
    private lateinit var textViewLanguage: TextView
    private lateinit var textViewTextSize: TextView
    private lateinit var editTextCity: EditText
    private lateinit var editTextCountryCode: EditText

    private val arrayLanguage = arrayOf("English", "Russian", "other")
    private val arrayTextSize = arrayOf("small", "medium", "large")
    private var language by Delegates.notNull<Int>()
    private var textSize by Delegates.notNull<Int>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_layout, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewSettings = view.findViewById(R.id.textViewSettings)
        textViewSound = view.findViewById(R.id.textViewSound)
        textViewLanguage = view.findViewById(R.id.textViewLanguage)
        textViewTextSize = view.findViewById(R.id.textViewTextSize)
        buttonApplyChanges  = view.findViewById(R.id.buttonApplyChanges)
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage)
        spinnerTextSize = view.findViewById(R.id.spinnerTextSize)
        editTextCity = view.findViewById(R.id.editTextCity)
        editTextCountryCode = view.findViewById(R.id.editTextTextCountryCode)

        sharedPreferences = activity!!.applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        when(sharedPreferences.getString("language", "")) {
            "eng" -> {
                language = 0
                textViewSettings.text = "Settings"
                textViewSound.text = "Sound"
                textViewLanguage.text = "Language"
                textViewTextSize.text = "Text size"
                buttonApplyChanges.text = "apply"

            }
            "rus" -> {
                language = 1
                textViewSettings.text = "Настройки"
                textViewSound.text = "Звук"
                textViewLanguage.text = "Язык"
                textViewTextSize.text = "Размер текста"
                buttonApplyChanges.text = "применить"
            }
            "other" -> {
                language = 2
                textViewSettings.text = ""
                textViewSound.text = ""
                textViewLanguage.text = ""
                textViewTextSize.text = ""
                buttonApplyChanges.text = ""
            }
        }

        when(sharedPreferences.getString("textSize", "")) {
            "small" -> {
                textSize = 0

            }
            "medium" -> {
                textSize = 1
            }
            "large" -> {
                textSize = 2
            }
        }

        if (sharedPreferences.contains("city")) {
            editTextCity.setText(sharedPreferences.getString("city", ""))
            if(sharedPreferences.contains("countryCode")) {
                editTextCountryCode.setText(sharedPreferences.getString("countryCode", ""))
            }
        }

        buttonApplyChanges.visibility = View.VISIBLE
        buttonApplyChanges.setOnClickListener {
            if(editTextCity.text.toString() != "" && editTextCountryCode.text.toString() != "") {
                val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("city", editTextCity.text.toString())
                editor.putString("countryCode", editTextCountryCode.text.toString())
                editor.apply()
            }
            activity!!.recreate()
        }

        val adapterLanguage = ArrayAdapter(activity!!.applicationContext, R.layout.support_simple_spinner_dropdown_item, arrayLanguage)
        adapterLanguage.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerLanguage.adapter = adapterLanguage
        spinnerLanguage.setSelection(language)

        spinnerLanguage.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //..
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position) {
                    0 -> {
                        Log.d("TAG", "_____ 0")
                        val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("language", "eng")
                        editor.apply()
                    }
                    1 -> {
                        Log.d("TAG", "_____ 1")
                        val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("language", "rus")
                        editor.apply()
                    }
                    2 -> {
                        Log.d("TAG", "_____ 2")
                        val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("language", "other")
                        editor.apply()
                    }
                }
            }
        }

        val adapterTextSize = ArrayAdapter(activity!!.applicationContext, R.layout.support_simple_spinner_dropdown_item, arrayTextSize)
        adapterTextSize.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerTextSize.adapter = adapterTextSize
        spinnerTextSize.setSelection(textSize)

        spinnerTextSize.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //..
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = parent?.getItemAtPosition(position).toString()
                when(position) {
                    0 -> {
                        Log.d("TAG", "_____ 0")
                        val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("textSize", "small")
                        editor.apply()
                    }
                    1 -> {
                        Log.d("TAG", "_____ 1")
                        val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("textSize", "medium")
                        editor.apply()
                    }
                    2 -> {
                        Log.d("TAG", "_____ 2")
                        val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("textSize", "large")
                        editor.apply()
                    }
                }
            }
        }


        if (sharedPreferences.contains("mute")) {
            if(sharedPreferences.getBoolean("mute", false)) {
                switchOff = false
                textForSwitch = "Off"
            }
            if(!sharedPreferences.getBoolean("mute", false)) {
                switchOff = true
                textForSwitch = "On"
            }
        }

        switch = view.findViewById(R.id.switchSound)
        switch.isChecked = switchOff
        switch.text = textForSwitch
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            when(isChecked) {
                true -> {
                    switch.text = "On"
                    val v = activity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.EFFECT_TICK))
                    Log.d("TAG", "_____Settings___ true = ")
                    val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putBoolean("mute", false)
                    editor.apply()
                }
                false -> {
                    switch.text = "Off"
                    Log.d("TAG", "_____Settings___ true = ")
                    val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putBoolean("mute", true)
                    editor.apply()
                }
            }
        }

    }
}