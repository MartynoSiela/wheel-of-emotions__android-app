package com.example.wheel_of_emotions.ui.wheel

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WheelViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is wheel Fragment"
    }
    val text: LiveData<String> = _text

    @SuppressLint("ClickableViewAccessibility")
    fun setTouchListener(context: Context?, wheel: ImageView, emotions: ImageView, button: Button) {
        val touchListener = WheelTouchListener(context!!, wheel, emotions, button)
        wheel.setOnTouchListener(touchListener)
    }

    fun setClickListener(context: Context?, wheel: ImageView, button: Button) {
        val clickListener = ButtonClickListener(context!!, wheel, button)
        button.setOnClickListener(clickListener)
    }
}