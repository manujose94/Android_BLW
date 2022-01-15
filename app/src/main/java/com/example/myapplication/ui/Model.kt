package com.example.myapplication.ui

import java.util.*
import kotlin.collections.ArrayList

class Model : Observable() {
    // declaring a list of integer
    val List: MutableList<Int>

    // constructor to initialize the list
    init {
        // reserving the space for list elements
        List = ArrayList(3)

        // adding elements into the list
        List.add(0)
        List.add(0)
        List.add(0)
    }

    // defining getter and setter functions
    // function to return appropriate count
    // value at correct index
    @Throws(IndexOutOfBoundsException::class)
    fun getValueAtIndex(the_index: Int): Int {
        return List[the_index]
    }

    // function to make changes in the activity button's
    // count value when user touch it
    @Throws(IndexOutOfBoundsException::class)
    fun setValueAtIndex(the_index: Int) {
        List[the_index] = List[the_index] + 1
        setChanged()
        notifyObservers()
    }
}