package com.example.wheel_of_emotions.ui.week

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeekViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is week Fragment"
    }
    val text: LiveData<String> = _text
}