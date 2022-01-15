package com.example.myapplication

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.myapplication.`class`.Device


class Activity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_1)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val myIntent = intent
        val derivedObject = myIntent.getSerializableExtra("device") as Device
        Log.i("[DEVICE]",derivedObject.toString())
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

    override fun onBackPressed() {

        //Display the name of our current Activity/class as it was declared in the source code
        val text = "Hello from " + this.javaClass.simpleName

        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }





}