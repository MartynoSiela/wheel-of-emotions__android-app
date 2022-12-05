package com.example.wheel_of_emotions

import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import androidx.core.view.drawToBitmap
import kotlin.math.abs

open class OnTouchListener() : View.OnTouchListener {

    private val SWIPE_THRESHOLD = 2f
    private var initialX = 0f
    private var initialY = 0f
    private var previousX = 0f
    private var previousY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private var diffX = 0f
    private var diffY = 0f
    private var swipeH = "0"
    private var moved = false

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        val bitmap = v.drawToBitmap()

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                moved = false
                initialX = event.x
                initialY = event.y
                true
            }
            MotionEvent.ACTION_MOVE -> {
                moved = true
                currentX = event.x
                currentY = event.y
                diffX = currentX - previousX
                diffY = currentY - previousY
                when (swipeH) {
                    "LEFT" -> {
                        if (currentX > previousX) {
                            swipeH = "RIGHT"
                            initialX = previousX
                            diffX = currentX - initialX
                        } else {
                            //
                        }
                    }
                    "RIGHT" -> {
                        if (currentX < previousX) {
                            swipeH = "LEFT"
                            initialX = previousX
                            diffX = currentX - initialX
                        } else {
                            //
                        }
                    }
                    else -> {
                        if (currentX < initialX) {
                            swipeH = "LEFT"
                            diffX = currentX - initialX
                        } else if (currentX > initialX) {
                            swipeH = "RIGHT"
                            diffX = currentX - initialX
                        } else {
                            //
                        }
                    }
                }

                previousX = currentX
                previousY = currentY

                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight(diffX)
                        } else {
                            onSwipeLeft(diffX)
                        }
                    }
                } else {
                    if (abs(diffY) > SWIPE_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeDown(diffY)
                        } else {
                            onSwipeUp(diffY)
                        }
                    }
                }
                true
            }
            MotionEvent.ACTION_UP -> {
                if (!moved) {
                    val colorPixel = bitmap.getPixel(initialX.toInt(), initialY.toInt())
                    val colorSectionSelectedHex = String.format("#%02x%02x%02x", Color.red(colorPixel), Color.green(colorPixel), Color.blue(colorPixel))
                    getPixelColorName(Integer.decode(colorSectionSelectedHex))
                }
                onSwipeFinished(diffX)
                swipeH = "0"
                initialX = 0f
                initialY = 0f
                previousX = 0f
                previousY = 0f
                currentX = 0f
                currentY = 0f
                diffX = 0f
                diffY = 0f
                true
            }
            else -> {
                false
            }
        }
    }

    open fun getPixelColorName(pixel: Int) {

    }

    open fun onSwipeFinished(diffX: Float) {

    }

    open fun onSwipeUp(diffY: Float) {

    }

    open fun onSwipeDown(diffY: Float) {

    }

    open fun onSwipeLeft(diffX: Float) {

    }

    open fun onSwipeRight(diffX: Float) {}
}