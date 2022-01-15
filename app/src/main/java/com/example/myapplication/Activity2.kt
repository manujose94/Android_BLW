package com.example.myapplication

import android.bluetooth.BluetoothAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.`class`.Device

class Activity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Declaring the textView for name from the layout file
        val tvName = findViewById<TextView>(R.id.nameTv)

        // Declaring the textView for MAC ID from the layout file
        val tvMac = findViewById<TextView>(R.id.macAddressTv)

        // Declaring the button from the layout file
        val btn = findViewById<Button>(R.id.btnGet)

        // Initializing the Bluetooth Adapter
        val bAdapter = BluetoothAdapter.getDefaultAdapter()

        // Button Action when clicked
        btn.setOnClickListener {

            // Checks if Bluetooth Adapter is present
            if (bAdapter == null) {
                Toast.makeText(applicationContext, "Bluetooth Not Supported", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Arraylist of all the bonded (paired) devices
                val pairedDevices = bAdapter.bondedDevices
                if (pairedDevices.size > 0) {
                    for (device in pairedDevices) {

                        // get the device name
                        val deviceName = device.name

                        // get the mac address
                        val macAddress = device.address

                        // append in the two separate views
                        tvName.append("$deviceName\n")
                        tvMac.append("$macAddress\n")
                    }
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // todo: goto back activity from here
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }
}