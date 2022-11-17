package com.example.checkscanner

import android.app.Activity
import android.app.Notification
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.checkscanner.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var codeScanner: CodeScanner
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var bluetoothService: BluetoothService
    private lateinit var scanBluetoothService: ScanBluetoothService
    private val listOfInn =
        listOf("19072006123456", "07122008123456", "12092003123456", "31082011123456")
    private var isCorrectInn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
        checkPermissions()
    }

    private fun initialize() {
        preferencesHelper = PreferencesHelper(this)
        scanBluetoothService = ScanBluetoothService(this)
    }

    private fun checkPermissions() {
        permissionForCamera()
        permissionForBluetooth()
    }

    private fun permissionForCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                cameraRequestCode
            )
        } else {
            startScanning()
        }
    }

    private fun startScanning() {
        codeScanner = CodeScanner(this, binding.qrScanner)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            listOfInn.forEach { inn ->
                if (inn == it.text) {
                    isCorrectInn = true
                }
                if (isCorrectInn) {
                    preferencesHelper.userInn = inn
                    runOnUiThread {
                        toast("Ваш ИНН: ${preferencesHelper.userInn}")
                    }
//                    bluetoothService.ConnectedThread().write(inn.toByteArray())
                    it.text.replace(it.text, "")
                    return@forEach
                }
            }
            if (!isCorrectInn) {
                runOnUiThread {
                    toast("Такого пользователя нет!")
                }
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.qrScanner.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun permissionForBluetooth() {
        if (scanBluetoothService.bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            registerForResult.launch(enableBtIntent)
        }
    }

    val registerForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
                startScanning()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val handler: Handler.Callback = Handler.Callback { message ->
        when (message.what) {
            MESSAGE_READ -> {
                val readBuff: ByteArray = message.obj as ByteArray
                val tempMessage = String(readBuff, 0, message.arg1)
                toast(tempMessage)
            }
            MESSAGE_WRITE -> {

            }
            MESSAGE_TOAST -> {

            }
        }
        true
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner?.startPreview()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::codeScanner.isInitialized) {
            codeScanner?.releaseResources()
        }
    }

    companion object {
        private const val cameraRequestCode = 999
    }
}