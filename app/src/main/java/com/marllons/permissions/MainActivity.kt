package com.marllons.permissions

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.marllons.permissions.Permissions.checkPermissionsResultLauncher
import com.marllons.permissions.Permissions.settingsPermissionsResultLauncher
import com.marllons.permissions.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding


    private var necessaryPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        registerResultsLauncher()
        checkPermissionsResultLauncher(necessaryPermissions)
    }

    private fun initViews() {
        binding.requestbtn.setOnClickListener(this)
        binding.allowPermission.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.requestbtn -> checkPermissionsResultLauncher(necessaryPermissions)
            R.id.allow_permission -> settingsPermissionsResultLauncher()
        }
    }

    private fun registerResultsLauncher() {
        Permissions.registerCheckPermissionsResult(this, object : Permissions.OnPermissionCallback {
            override fun onPermissionsGranted() {
                binding.label.setText(R.string.permission_granted)
                binding.allowPermission.isVisible = false
            }

            override fun onPermissionsDenied() {
                binding.label.setText(R.string.permission_denied)
            }

            override fun onPermissionsDeniedFully() {
                binding.label.setText(R.string.permission_denied_forcefully)
                binding.allowPermission.isVisible = true
            }
        })

        Permissions.registerOpenSettingsPermissionsResult(this) {
            checkPermissionsResultLauncher(necessaryPermissions)
        }
    }
}