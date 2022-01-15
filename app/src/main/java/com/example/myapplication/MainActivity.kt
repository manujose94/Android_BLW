package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.`class`.Device
import com.example.myapplication.ui.Model
import org.jetbrains.anko.alert

import java.util.*
private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2
class MainActivity : AppCompatActivity() ,Observer, View.OnClickListener {

    // creating object of Model class
    private var myModel: Model? = null

    // creating object of Button class
    private var mButton1: Button? = null
    private var mButton2: Button? = null
    private var mButton3: Button? = null
    private var mb1: String? = null
    private var mb2: String? = null
    private var mb3: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // calling the action bar


        supportActionBar?.setIcon(R.drawable.ic_home_black_24dp)

        // creating relationship between the
        // observable Model and the
        // observer Activity
        myModel = Model()
        myModel!!.addObserver(this)

        // assigning button IDs to the objects
        mButton1 = findViewById(R.id.button)
        mButton2 = findViewById(R.id.button2)
        mButton3 = findViewById(R.id.button3)

        mb1 =getString(R.string.mb1)
        mb2 =getString(R.string.mb2)
        mb3 =getString(R.string.mb3)
        // transfer the control to Onclick() method
        // when a button is clicked by passing
        // argument "this"
        mButton1?.setOnClickListener(this)
        mButton2?.setOnClickListener(this)
        mButton3?.setOnClickListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted) {
            requestLocationPermission()
        }



// Check to see if the Bluetooth classic feature is available.
        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH) }?.also {
            Toast.makeText(this, "Bluetooth NOT SUPPORTED", Toast.LENGTH_SHORT).show()
            finish()
        }
// Check to see if the BLE feature is available.
        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }?.also {
            Toast.makeText(this, "Bluetooth le NOT SUPPORTED" ,Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /*******************************************
     * Private functions
     *******************************************/
    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)
    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }

    // calling setValueAtIndex() method
    // by passing appropriate arguments
    // for different buttons
    override fun onClick(v: View) {
        when (v.id) {
            R.id.button ->  {
                myModel?.setValueAtIndex(0)
                val intent = Intent(this@MainActivity, Activity1::class.java).apply {

                }
                val userInfo = Device()
                userInfo.setName("Bluetooth Sony")
                userInfo.setMAC("78:2b:46:50:bb:21")
                intent.putExtra("passData", myModel?.getValueAtIndex(0) )
                intent.putExtra("device",userInfo)
                startActivity(intent)

            }
            R.id.button2 -> {
                myModel?.setValueAtIndex(1)
                val intent = Intent(this@MainActivity, Activity2::class.java).apply {

                }
                startActivity(intent)
            }
            R.id.button3 -> myModel?.setValueAtIndex(2)
        }
    }


    /*******************************************
     * Properties
     *******************************************/
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }





    // function to update the view after
    // the values are modified by the model
    @SuppressLint("SetTextI18n")
    override fun update(arg0: Observable, arg1: Any?) {

        // changing text of the buttons
        // according to updated values
        mButton1!!.text =  mb1 +"["+myModel!!.getValueAtIndex(0) +"]"
        mButton2!!.text = mb2+"["+myModel!!.getValueAtIndex(1) +"]"
        mButton3!!.text = mb3+ "["+myModel!!.getValueAtIndex(2) +"]"
    }


    /*******************************************
     * Activity function overrides
     *******************************************/
    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }

    override fun onBackPressed() {

        //Display the name of our current Activity/class as it was declared in the source code
        val text = "Hello from " + this.javaClass.simpleName

        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()

    }

    // this event will enable the back
    // function to the button on press
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }


    private val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun requestLocationPermission() {
        if (isLocationPermissionGranted) {
            return
        }
        runOnUiThread {
            alert {
                title = "Location permission required"
                message = "Starting from Android N (10.0), the system requires apps to be granted " +
                        "location access in order to scan for BLE devices."
                isCancelable = false
                positiveButton(android.R.string.ok) {
                    requestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }.show()
        }
    }



    /*******************************************
     * Extension functions
     *******************************************/

    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

}

//On create for navigation Bottom

/**
binding = ActivityMainBinding.inflate(layoutInflater)
setContentView(binding.root)

val navView: BottomNavigationView = binding.navView

val navController = findNavController(R.id.nav_host_fragment_activity_main)
// Passing each menu ID as a set of Ids because each
// menu should be considered as top level destinations.
val appBarConfiguration = AppBarConfiguration(setOf(
R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
setupActionBarWithNavController(navController, appBarConfiguration)
navView.setupWithNavController(navController)
 **/