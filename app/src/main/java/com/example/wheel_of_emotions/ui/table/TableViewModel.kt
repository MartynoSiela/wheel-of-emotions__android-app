package com.example.wheel_of_emotions.ui.table

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TableViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is table Fragment"
    }
    val text: LiveData<String> = _text

    @SuppressLint("ClickableViewAccessibility")
    fun setClickListener(context: Context, button: Button, fragment: Fragment) {
        val clickListener = ClearButtonListener(context, button, fragment)
        button.setOnClickListener(clickListener)
    }
}