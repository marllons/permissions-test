package com.marllons.permissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

object Permissions {

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    fun registerForActivityResult(activity: FragmentActivity, doAfter: () -> Unit) {
        resultLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                doAfter.invoke()
            }
        }
    }

    fun registerForActivityResult(fragment: Fragment, doAfter: () -> Unit) {
        resultLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                doAfter.invoke()
            }
        }
    }

    fun Context.arePermissionsGranted(permissions: Array<out String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    private fun Context.requestPermissionRationale(permissions: Array<out String>): Boolean {
        for (permission in permissions) {
            if (shouldShowRequestPermissionRationale(this as Activity, permission)) return false
        }
        return true
    }

    private fun Context.requestPermissionsCompat(permissions: Array<out String>, requestCode: Int) {
        requestPermissions((this as Activity), permissions, requestCode)
    }

    fun Context.checkAndRequestPermissions(necessaryPermissions: Array<out String>, requestCode: Int) {
        this.requestPermissionsCompat(necessaryPermissions, requestCode)
    }

    fun openSettingsPermissionsForActivityResult() {
        if (this::resultLauncher.isInitialized) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
            }
            resultLauncher.launch(intent)
        } else {
            throw java.lang.Exception("should register activity result calling fun registerForActivityResult(...)")
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        context: Context,
        responseCode: Int,
        onPermission: OnPermissionCallback
    ) {
        when (requestCode) {
            responseCode -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onPermission.onPermissionsGranted()
                } else {
                    if (context.requestPermissionRationale(permissions)) {
                        onPermission.onPermissionsDeniedFully()
                    } else {
                        onPermission.onPermissionsDenied()
                    }
                }
            }
            else -> {}
        }
    }

    interface OnPermissionCallback {
        fun onPermissionsGranted()
        fun onPermissionsDenied()
        fun onPermissionsDeniedFully()
    }
}