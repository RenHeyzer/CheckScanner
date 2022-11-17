package com.example.checkscanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build

class ScanBluetoothService(val context: Context) {

    val bluetoothManager : BluetoothManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.getSystemService(BluetoothManager::class.java)
    } else {
        TODO("VERSION.SDK_INT < M")
    }

    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    fun checkBluetoothIsEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    }
}