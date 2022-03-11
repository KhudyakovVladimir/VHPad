package com.khudyakovvladimir.vhcloudnotepad.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.Exception
import android.content.ComponentName

class PermissionHelper(val context: Context) {

    lateinit var sharedPreferences: SharedPreferences
    var permissionsCheck : Boolean = false

     fun checkPermissions(): Boolean {

         addAutoStartup()
         disableBatterySaver()

        if (!isExternalStorageReadable() || !isExternalStorageWriteable()) {
            println("sdcard is not available")
            return false
        }
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.CAMERA,
                    Manifest.permission.VIBRATE
                ),1111)
            savePreference()
            return false
        }
        return true
    }

    private fun isExternalStorageWriteable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    private fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
    }

    fun checkPermissionsBoolean(): Boolean {
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("permissions"))
        {
            permissionsCheck = sharedPreferences.getBoolean("permissions", false)
            Log.d("TAG", "_____checkPermissionsBoolean() - permissionsCheck - $permissionsCheck")
        }
        return permissionsCheck
    }

    private fun savePreference() {
        Log.d("TAG", "_____PermissionHelper - savePreference()")
        sharedPreferences = context.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("permissions", true)
        editor.apply()
    }

    private fun addAutoStartup() {
        Log.d("TAG", "_____StartPermissionScreen___addAutoStartup()")
        try {
            //val intent = Intent(context, MainActivity::class.java)
            val intent = Intent()

            val manufacturer = Build.MANUFACTURER
            if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.AutobootManageActivity"
                )
            } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
            }
            val list: List<ResolveInfo> =
                context.packageManager!!.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (list.isNotEmpty()) {
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.d("TAG", "Activity NOT Found")
        }
    }

    private fun disableBatterySaver() {
        //for xiaomi
        try {
            val intent = Intent()
            intent.component = ComponentName(
                "com.miui.powerkeeper",
                "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
            )
            intent.putExtra("package_name", context.packageName)
            intent.putExtra("package_label", "VHCloudPad")

            val list: List<ResolveInfo> =
                context.packageManager!!.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (list.isNotEmpty()) {
                context.startActivity(intent)
            }
        }catch (e: Exception) {
            Log.d("TAG", "Activity NOT Found")
        }
    }
}