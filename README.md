# BlueDiamond-BluetoothWrapper for HC Bluetooth Modules

```java
/**TODO
- Readme will be updated soon.
- Arduino will be added.
- Sample app will be detailed.
*/
```

## Simple Usage

```kotlin
class MainActivity : AppCompatActivity(), Diamond.ConnectionListener {

    private var btSocket: BluetoothSocket? = null
    lateinit var diamond: Diamond

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        diamond = Diamond(this)
        diamond.run()
    }

    private fun sendCommand(command: String) {
        diamond.sendSignal(command)
    }

    override fun onBluetoothConnected(address: String, btSocket: BluetoothSocket) {
        this.btSocket = btSocket
        Toast.makeText(this@MainActivity, "Connected to $address", Toast.LENGTH_SHORT).show()
        sendCommand("Hello World")
    }

    override fun onBluetoothConnectionFailed(bluetoothConnectException: Exception) {
        Toast.makeText(this@MainActivity, bluetoothConnectException.message, Toast.LENGTH_SHORT)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        btSocket?.close()
    }
}
```
