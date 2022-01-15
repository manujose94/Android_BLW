package com.example.myapplication.ui.dashboard

import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentDashboardBinding
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

var devices = ArrayList<BluetoothDevice>()
var devicesMap = HashMap<String, BluetoothDevice>()
var mArrayAdapter: ArrayAdapter<String>? = null
val uuid: UUID = UUID.fromString("8989063a-c9af-463a-b3f1-f21d9b2b827b")
var message = ""

private var mContext: Context? = null
class DashboardFragment : Fragment() {
    private var textView: TextView? = null
    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mArrayAdapter = ArrayAdapter(mContext!!, R.layout.dialog_select_device)
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity?.registerReceiver(mReceiver, filter) // Don't forget to unregister during onDestroy
        this.textView = view.findViewById(R.id.textView)
        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener {view ->
            if (BluetoothAdapter.getDefaultAdapter() == null) {
                Snackbar.make(view, "Bluetooth is disabled", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            } else {
                devicesMap = HashMap()
                devices = ArrayList()
                mArrayAdapter!!.clear()

                val editText = view.findViewById<EditText>(R.id.editText)
                message = editText.text.toString()
                editText.text.clear()
                for (device in BluetoothAdapter.getDefaultAdapter().bondedDevices) {
                    devicesMap.put(device.address, device)
                    devices.add(device)
                    // Add the name and address to an array adapter to show in a ListView
                    mArrayAdapter!!.add((if (device.name != null) device.name else "Unknown") + "\n" + device.address + "\nPared")
                }

                // Start discovery process
                if (BluetoothAdapter.getDefaultAdapter().startDiscovery()) {
                    val dialog = SelectDeviceDialog()
                    dialog.show(childFragmentManager, "select_device")
                }
            }
        }

        BluetoothServerController(this).start()

    //progressBar = view.findViewById(R.id.loader)
        // You can declare your listview here
        //fetchJsonData().execute()
    }

    override fun onStart() {
        super.onStart()

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun appendText(text: String) {
        activity?.runOnUiThread(Runnable {
            this.textView?.text = this.textView?.text.toString() +"\n" + text
        })

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

    class BluetoothServerController(activity: DashboardFragment) : Thread() {
        private var cancelled: Boolean
        private val serverSocket: BluetoothServerSocket?
        private val activity = activity

        init {
            val btAdapter = BluetoothAdapter.getDefaultAdapter();
            if (btAdapter != null) {
                this.serverSocket = btAdapter.listenUsingRfcommWithServiceRecord("test", uuid)
                this.cancelled = false
            } else {
                this.serverSocket = null
                this.cancelled = true
            }

        }

        override fun run() {
            var socket: BluetoothSocket

            while(true) {
                if (this.cancelled) {
                    break
                }

                try {
                    socket = serverSocket!!.accept()
                } catch(e: IOException) {
                    break
                }

                if (!this.cancelled && socket != null) {
                    Log.i("server", "Connecting")
                    BluetoothServer(this.activity, socket).start()
                }
            }
        }

        fun cancel() {
            this.cancelled = true
            this.serverSocket!!.close()
        }
    }

    class BluetoothServer(private val activity: DashboardFragment, private val socket: BluetoothSocket): Thread() {
        private val inputStream = this.socket.inputStream
        private val outputStream = this.socket.outputStream

        override fun run() {
            try {
                val available = inputStream.available()
                val bytes = ByteArray(available)
                Log.i("server", "Reading")
                inputStream.read(bytes, 0, available)
                val text = String(bytes)
                Log.i("server", "Message received")
                Log.i("server", text)
                activity.appendText(text)
            } catch (e: Exception) {
                Log.e("client", "Cannot read data", e)
            } finally {
                inputStream.close()
                outputStream.close()
                socket.close()
            }
        }
    }

    class SelectDeviceDialog: DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(this.activity)
            builder.setTitle("Send message to")
            builder.setAdapter(mArrayAdapter) { _, which: Int ->
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                BluetoothClient(devices[which]).start()
            }

            return builder.create()
        }

        override fun onCancel(dialog: DialogInterface) {
            super.onCancel(dialog)
        }
    }


    // Create a BroadcastReceiver for ACTION_FOUND
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Get the BluetoothDevice object from the Intent
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val pairedDevice = devicesMap[device?.address]
                if (pairedDevice == null) {
                    var index = -1
                    for (i in devices.indices) {
                        val tmp = devices[i]
                        if (tmp.address == device?.address) {
                            index = i
                            break
                        }
                    }

                    if (index > -1) {
                        if (device?.name != null) {
                            mArrayAdapter?.insert(
                                (if (device.name != null) device.name else "Unknown") + "\n" + device?.address,
                                index
                            )
                        }
                    } else {
                        if (device != null) {
                            devices.add(device)
                        }
                        // 	Add the name and address to an array adapter to show in a ListView
                        mArrayAdapter?.add((if (device?.name != null) device?.name else "Unknown") + "\n" + device?.address)
                    }
                }
            }
        }
    }


    class BluetoothClient(device: BluetoothDevice): Thread() {
        private val socket = device.createRfcommSocketToServiceRecord(uuid)

        override fun run() {
            Log.i("client", "Connecting")
            this.socket.connect()

            Log.i("client", "Sending")
            val outputStream = this.socket.outputStream
            val inputStream = this.socket.inputStream
            try {
                outputStream.write(message.toByteArray())
                outputStream.flush()
                Log.i("client", "Sent")
            } catch(e: Exception) {
                Log.e("client", "Cannot send", e)
            } finally {
                outputStream.close()
                inputStream.close()
                this.socket.close()
            }
        }
}
