package com.example.myapplication.`class`
import java.io.Serializable

class Device  : Serializable {
    private lateinit var name: String
    private lateinit var mac: String
    fun getName(): String? {
        return name
    }
    @JvmName("setName1")
    fun setName(name: String?) {
        this.name = name!!
    }
    fun getMAC(): String? {
        return mac
    }
    @JvmName("setPhone1")
    fun setMAC(phone: String?) {
        this.mac = phone!!
    }

    override fun toString(): String {
        return "Device(name='$name', mac='$mac')"
    }


}