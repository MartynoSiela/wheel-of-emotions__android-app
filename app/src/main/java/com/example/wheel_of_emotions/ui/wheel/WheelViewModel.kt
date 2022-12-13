package com.example.wheel_of_emotions.ui.wheel

import android.graphics.Bitmap
import android.graphics.Color
import android.view.MotionEvent
import androidx.lifecycle.ViewModel

class WheelViewModel : ViewModel() {

    private val _colorSelectedRgb = Color.rgb(255,255,255)
    private val _colorSelectedInt = 16777215
    private var _colorPreviousNegativeInt = -1
    private var _colorPreviousRgbInt = -1
    private var _emotionPreviousId = -1
    private var _moved = false
    private var _centerX = 0
    private var _centerY = 0
    private var _initialX = 0f
    private var _initialY = 0f
    private var _initialRawX = 0f
    private var _initialRawY = 0f
    private var _currentX = 0f
    private var _currentY = 0f
    private var _previousX = 0f
    private var _previousY = 0f
    private var _diffX = 0f
    private var _diffY = 0f
    private var _swipeHorizontal = "NULL"
    private var _swipeVertical = "NULL"
    private val _swipeThreshold = 2f
    private var _bitmap: Bitmap? = null

    val colorSelectedRgb: Int
        get() = _colorSelectedRgb
    val colorSelectedInt: Int
        get() = _colorSelectedInt
    var colorPreviousNegativeInt: Int
        set(value) {_colorPreviousNegativeInt = value}
        get() = _colorPreviousNegativeInt
    var colorPreviousRgbInt: Int
        set(value) {_colorPreviousRgbInt = value}
        get() = _colorPreviousRgbInt
    var emotionPreviousId: Int
        set(value) {_emotionPreviousId = value}
        get() = _emotionPreviousId
    val swipeThreshold: Float
        get() = _swipeThreshold
    val moved: Boolean
        get() = _moved
    val diffX: Float
        get() = _diffX
    val diffY: Float
        get() = _diffY
    val currentX: Float
        get() = _currentX
    val currentY: Float
        get() = _currentY
    var centerX: Int
        get() = _centerX
        set(value) { _centerX = value}
    var centerY: Int
        get() = _centerY
        set(value) { _centerY = value}
    var bitmap: Bitmap?
        get() = _bitmap
        set(value) { _bitmap = value}

    fun getCurrentPixelValue(): Int {
        return _bitmap?.getPixel(_initialX.toInt(), _initialY.toInt())!!
    }

    fun setInitialTouchCoordinates(event: MotionEvent) {
        _moved = false
        _initialX = event.x
        _initialY = event.y
        _initialRawX = event.rawX
        _initialRawY = event.rawY
        _previousX = event.rawX
        _previousY = event.rawY
    }

    fun calculateTouchMotionCoordinates(event: MotionEvent) {
        _moved = true
        _currentX = event.rawX
        _currentY = event.rawY
        _diffX = _currentX - _previousX
        _diffY = _currentY - _previousY

        // Set initial direction
        when {
            _currentX < _initialRawX -> {
                _swipeHorizontal = "LEFT"
            }
            _currentX > _initialRawX -> {
                _swipeHorizontal = "RIGHT"
            }
            _currentY < _initialRawY -> {
                _swipeVertical = "UP"
            }
            _currentY > _initialRawY -> {
                _swipeVertical = "DOWN"
            }
        }

        // Reverse direction
        when {
            _swipeHorizontal == "LEFT" && _currentX > _previousX -> {
                _swipeHorizontal = "RIGHT"
                _initialRawX = _previousX
            }
            _swipeHorizontal == "RIGHT" && _currentX < _previousX -> {
                _swipeHorizontal = "LEFT"
                _initialRawX = _previousX
            }
            _swipeVertical == "UP" && _currentY > _previousY -> {
                _swipeVertical = "DOWN"
                _initialRawY = _previousY
            }
            _swipeVertical == "DOWN" && _currentY < _previousY -> {
                _swipeVertical = "UP"
                _initialRawY = _previousY
            }
        }

        _previousX = _currentX
        _previousY = _currentY
    }
}