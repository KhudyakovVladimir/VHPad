package com.khudyakovvladimir.vhcloudnotepad.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.vhcloudnotepad.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.khudyakovvladimir.vhcloudnotepad.view.fragments.MainScreen

class  MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = ""
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        sharedPreferences = applicationContext!!.getSharedPreferences("settings", Context.MODE_PRIVATE)

        var startDestination = R.id.startPermissionScreen

        if(sharedPreferences.getInt("startDestinationChanged", 0) == 1) {
            startDestination = R.id.notes
        }

        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.main_graph)

        graph.startDestination = startDestination
        navHostFragment.navController.graph = graph

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setupWithNavController(navController)

        if (sharedPreferences.contains("language")) {
            if(sharedPreferences.getString("language", "") == "eng") {
                bottomNavigationView.menu.findItem(R.id.notes).title = "notes"
                bottomNavigationView.menu.findItem(R.id.newNote).title = "new"
                bottomNavigationView.menu.findItem(R.id.shopping).title = "shopping"
                navigationView.menu.findItem(R.id.notes).title = "notes"
                navigationView.menu.findItem(R.id.shopping).title = "shopping"
                navigationView.menu.findItem(R.id.weather).title = "weather"
                navigationView.menu.findItem(R.id.settings).title = "settings"
                navigationView.menu.findItem(R.id.about).title = "about"
            }

            if(sharedPreferences.getString("language", "") == "rus") {
                bottomNavigationView.menu.findItem(R.id.notes).title = "записки"
                bottomNavigationView.menu.findItem(R.id.newNote).title = "новая"
                bottomNavigationView.menu.findItem(R.id.shopping).title = "покупки"
                navigationView.menu.findItem(R.id.notes).title = "записки"
                navigationView.menu.findItem(R.id.shopping).title = "покупки"
                navigationView.menu.findItem(R.id.weather).title = "погода"
                navigationView.menu.findItem(R.id.settings).title = "настройки"
                navigationView.menu.findItem(R.id.about).title = "информация"
            }
        }

        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.newNote -> {
                    MainScreen.flagSaveAdapterPosition = false
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putInt("adapterPosition", 1)
                    editor.apply()
                    val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    v.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.EFFECT_TICK))
                    navController.navigate(R.id.newNote)
                }
                R.id.notes-> {
                    val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    v.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.EFFECT_TICK))
                    navController.navigate(R.id.notes)
                }
                R.id.shopping -> {
                    val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    v.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.EFFECT_TICK))
                    navController.navigate(R.id.shopping)
                }
            }
            true
        }
    }
}
