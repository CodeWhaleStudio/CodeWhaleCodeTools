package com.bluewhaleyt.codewhale.code.compiler.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import com.bluewhaleyt.codewhale.code.compiler.app.ui.theme.CodeWhaleCodeCompilerTheme

val ROOT_DIR = Environment.getExternalStorageDirectory().absolutePath + "/CodeWhaleCodeTools"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAllFileAccess(this)
        setContent {
            CodeWhaleCodeCompilerTheme {
                App()
            }
        }
    }
}

fun isGrantedExternalStorageAccess(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else false
}

fun requestAllFileAccess(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!isGrantedExternalStorageAccess()) {
            val intent: Intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.setData(uri)
            activity.startActivity(intent)
        }
    } else {
        activity.startActivity(Intent(activity, activity.javaClass))
        ActivityCompat.requestPermissions(
            activity,
            arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            2000
        )
    }
}