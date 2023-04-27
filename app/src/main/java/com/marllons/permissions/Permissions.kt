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
    private lateinit var resultLauncherPermissions: ActivityResultLauncher<Array<String>>

    @JvmStatic
    fun checkPermissionsResultLauncher(permissions: Array<String>) {
        if (this::resultLauncherPermissions.isInitialized) {
            resultLauncherPermissions.launch(permissions)
        } else {
            throw java.lang.Exception("fun registerCheckPermissionsResult(...) must be declared before fun checkPermissionsResultLauncher(...)")
        }
    }

    @JvmStatic
    fun settingsPermissionsResultLauncher() {
        if (this::resultLauncher.isInitialized) {
            resultLauncher.launch(getConfigIntent())
        } else {
            throw java.lang.Exception("fun registerOpenSettingsPermissionsResult(...) must be declared before fun settingsPermissionsResultLauncher(...)")
        }
    }

    private fun getConfigIntent() = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
    }

    @JvmStatic
    fun registerOpenSettingsPermissionsResult(activity: FragmentActivity, doAfter: () -> Unit) {
        resultLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                doAfter.invoke()
            }
        }
    }

    @JvmStatic
    fun registerOpenSettingsPermissionsResult(fragment: Fragment, doAfter: () -> Unit) {
        resultLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                doAfter.invoke()
            }
        }
    }

    @JvmStatic
    fun registerCheckPermissionsResult(activity: FragmentActivity, onPermission: OnPermissionCallback) {
        resultLauncherPermissions = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                onPermission.onPermissionsGranted()
            } else {
                val p = permissions.keys.toTypedArray()
                if ((activity as Context).requestPermissionRationale(p)) {
                    onPermission.onPermissionsDeniedFully()
                } else {
                    onPermission.onPermissionsDenied()
                }
            }
        }
    }

    @JvmStatic
    fun registerCheckPermissionsResult(fragment: Fragment, onPermission: OnPermissionCallback) {
        resultLauncherPermissions = fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                onPermission.onPermissionsGranted()
            } else {
                val p = permissions.keys.toTypedArray()
                if ((fragment.requireActivity()).requestPermissionRationale(p)) {
                    onPermission.onPermissionsDeniedFully()
                } else {
                    onPermission.onPermissionsDenied()
                }
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


    @Deprecated(
        message = "Should use checkPermissionsResultLauncher(...)",
        replaceWith = ReplaceWith(
            "Permission.checkPermissionsResultLauncher(Permission, OnPermissionCallback)",
            "br.com.gestorescolar.app.module.core.utils.Permission.checkPermissionsResultLauncher"
        )
    )
    private fun Context.requestPermissionsCompat(permissions: Array<out String>, requestCode: Int) {
        requestPermissions((this as Activity), permissions, requestCode)
    }

    @Deprecated(
        message = "Should use checkPermissionsResultLauncher(...)",
        replaceWith = ReplaceWith(
            "Permission.checkPermissionsResultLauncher(Permission, OnPermissionCallback)",
            "br.com.gestorescolar.app.module.core.utils.Permission.checkPermissionsResultLauncher"
        )
    )
    fun Context.checkAndRequestPermissions(necessaryPermissions: Array<out String>, requestCode: Int) {
        this.requestPermissionsCompat(necessaryPermissions, requestCode)
    }

    @Deprecated(
        message = "Should use checkPermissionsResultLauncher(...)",
        replaceWith = ReplaceWith(
            "Permission.checkPermissionsResultLauncher(Permission, OnPermissionCallback)",
            "br.com.gestorescolar.app.module.core.utils.Permission.checkPermissionsResultLauncher"
        )
    )
    fun onRequestPermissionsResult(
        context: Context,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        responseCode: Int,
        onPermission: OnPermissionCallback,
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