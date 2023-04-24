package com.marllons.permissions

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.marllons.permissions.databinding.ActivityMainBinding
import com.marllons.permissions.Permissions.checkAndRequestPermissions
import com.marllons.permissions.Permissions.openSettingsPermissionsForActivityResult



class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val REQUEST_CODE = 100
    }

    private var necessaryPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        checkPermissions()
        Permissions.registerForActivityResult(this) { checkPermissions() }
    }

    private fun initViews() {
        binding.requestbtn.setOnClickListener(this)
        binding.allowPermission.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.requestbtn ->  checkPermissions()
            R.id.allow_permission -> openSettingsPermissionsForActivityResult()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Permissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this, REQUEST_CODE, object : Permissions.OnPermissionCallback {
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
    }

    private fun checkPermissions() {
        this.checkAndRequestPermissions(necessaryPermissions, REQUEST_CODE)
    }
}