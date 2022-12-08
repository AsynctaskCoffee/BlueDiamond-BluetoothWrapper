package com.asynctaskcoffee.library

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import io.reactivex.rxjava3.core.Completable
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class Diamond(private val context: Context) : BluetoothDialogFragment.DeviceSelectedListener {
    private var btSocket: BluetoothSocket? = null
    private var isBtConnected = false
    private var myBluetooth: BluetoothAdapter? = null
    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    var connectionListener: ConnectionListener? = null

    fun isConnected() = isBtConnected

    fun run() {
        if (checkPermission()) {
            set()
        } else connectionListener?.onBluetoothConnectionFailed(Exception("You need bluetooth permission"))
    }

    private fun showSelectionDialog(list: ArrayList<BluetoothDevice>) {
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            val dialogFragment = BluetoothDialogFragment.newInstance(list)
            dialogFragment.setOnDeviceSelectedListener(this)
            dialogFragment.show(it, BluetoothDialogFragment.TAG)
        }
    }

    private fun set() {
        myBluetooth = BluetoothAdapter.getDefaultAdapter()
        if (myBluetooth == null) {
            connectionListener?.onBluetoothConnectionFailed(Exception("No bluetooth device is available"))
        } else if (myBluetooth?.isEnabled == false) {
            connectionListener?.onBluetoothConnectionFailed(Exception("Please turn on bluetooth"))
        } else {
            getDeviceList()?.run {
                val list: ArrayList<BluetoothDevice> = arrayListOf()
                forEach {
                    list.add(it)
                }
                showSelectionDialog(list)
            }
        }
    }

    private fun connect(address: String, uuid: UUID): Completable =
        Completable.create { progressHolder ->
            try {
                if (btSocket == null || !isBtConnected) {
                    val service =
                        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                    myBluetooth = service.adapter
                    btSocket = myBluetooth?.getRemoteDevice(address)
                        ?.createInsecureRfcommSocketToServiceRecord(uuid)
                    myBluetooth?.cancelDiscovery()
                    btSocket?.connect()
                    progressHolder.onComplete()
                    btSocket?.let { connectionListener?.onBluetoothConnected(address, it) }
                }
            } catch (e: Exception) {
                isBtConnected = false
                progressHolder.onError(e)
                connectionListener?.onBluetoothConnectionFailed(e)
            }
        }

    fun sendSignal(command: String) {
        try {
            if (btSocket == null || !isBtConnected) throw Exception("Bluetooth is not connected!").also {
                connectionListener?.onBluetoothConnectionFailed(it)
            }
            btSocket?.outputStream?.write(command.toByteArray())
        } catch (e: java.lang.Exception) {
            connectionListener?.onBluetoothConnectionFailed(e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceList(
    ): MutableSet<BluetoothDevice>? {
        return myBluetooth?.bondedDevices
    }

    private fun checkPermission(): Boolean {
        if (context.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            } != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    override fun setOnDeviceSelectedListener(address: String) {
        connect(address, uuid)
    }

    interface ConnectionListener {
        fun onBluetoothConnected(address: String, btSocket: BluetoothSocket)
        fun onBluetoothConnectionFailed(bluetoothConnectException: Exception)
    }
}
/**

char state = '4';
int motor1pin1 = 3;
int motor1pin2 = 4;
int motor2pin1 = 5;
int motor2pin2 = 6;
void setup() {
//Setting the pin mode and initial LOW
pinMode(motor1pin1, OUTPUT);
pinMode(motor1pin2, OUTPUT);
pinMode(motor2pin1, OUTPUT);
pinMode(motor2pin2, OUTPUT);
Serial.begin(9600);
}
void loop() {

if(Serial.available() > 0){
state = Serial.read();
}

if (state == '1') {
digitalWrite(motor1pin1, HIGH);
digitalWrite(motor1pin2, LOW);
digitalWrite(motor2pin1, HIGH);
digitalWrite(motor2pin2, LOW);
} else if (state == '5') {
digitalWrite(motor1pin2, HIGH);
digitalWrite(motor1pin1, LOW);
digitalWrite(motor2pin2, HIGH);
digitalWrite(motor2pin1, LOW);
} else if (state == '4') {
digitalWrite(motor1pin1, LOW);
digitalWrite(motor1pin2, LOW);
digitalWrite(motor2pin1, LOW);
digitalWrite(motor2pin2, LOW);
}else if (state == '3') {
digitalWrite(motor1pin2, LOW);
digitalWrite(motor1pin1, HIGH);
digitalWrite(motor2pin2, HIGH);
digitalWrite(motor2pin1, LOW);
}else if (state == '2') {
digitalWrite(motor1pin2, HIGH);
digitalWrite(motor1pin1, LOW);
digitalWrite(motor2pin2, LOW);
digitalWrite(motor2pin1, HIGH);
}
}**/