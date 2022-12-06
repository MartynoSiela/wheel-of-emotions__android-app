package com.example.wheel_of_emotions

import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import androidx.core.view.drawToBitmap
import kotlin.math.abs

open class OnTouchListener(private val centerX: Int, private val centerY: Int) : View.OnTouchListener {

    private val swipeThreshold = 2f
    private var initialX = 0f
    private var initialY = 0f
    private var previousX = 0f
    private var previousY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private var diffX = 0f
    private var diffY = 0f
    private var swipeHorizontal = "0"
    private var swipeVertical = "0"
    private var moved = false

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        val bitmap = v.drawToBitmap()

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                moved = false
                initialX = event.x
                initialY = event.y
                previousX = event.x
                previousY = event.y
                true
            }
            MotionEvent.ACTION_MOVE -> {
                moved = true
                currentX = event.x
                currentY = event.y
                diffX = currentX - previousX
                diffY = currentY - previousY

                // Set initial direction
                when {
                    currentX < initialX -> {
                        swipeHorizontal = "LEFT"
                    }
                    currentX > initialX -> {
                        swipeHorizontal = "RIGHT"
                    }
                    currentY < initialY -> {
                        swipeVertical = "UP"
                    }
                    currentY > initialY -> {
                        swipeVertical = "DOWN"
                    }
                }

                // Reverse direction
                when {
                    swipeHorizontal == "LEFT" && currentX > previousX -> {
                        swipeHorizontal = "RIGHT"
                        initialX = previousX
                    }
                    swipeHorizontal == "RIGHT" && currentX < previousX -> {
                        swipeHorizontal = "LEFT"
                        initialX = previousX
                    }
                    swipeVertical == "UP" && currentY > previousY -> {
                        swipeVertical = "DOWN"
                        initialY = previousY
                    }
                    swipeVertical == "DOWN" && currentY < previousY -> {
                        swipeVertical = "UP"
                        initialY = previousY
                    }
                }

                previousX = currentX
                previousY = currentY

                // Do rotation based on vertical or horizontal swipe
                if (abs(diffX) > abs(diffY) && abs(diffX) > swipeThreshold) {
                    if (currentY > centerY) onSwipe(-diffX) else onSwipe(diffX)
                } else
                if (abs(diffX) < abs(diffY) && abs(diffY) > swipeThreshold) {
                    if (currentX > centerX) onSwipe(diffY) else onSwipe(-diffY)
                }

                true
            }
            MotionEvent.ACTION_UP -> {

                // If the wheel was not moved then there was a click action to selected a section
                if (!moved) {
                    val colorPixel = bitmap.getPixel(initialX.toInt(), initialY.toInt())
                    val colorSectionSelectedHex = String.format("#%02x%02x%02x", Color.red(colorPixel), Color.green(colorPixel), Color.blue(colorPixel))
                    getPixelColorName(Integer.decode(colorSectionSelectedHex))
                }

                // Disabled animated rotation on finished swipe
                // onSwipeFinished(diffX)

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

    open fun onSwipe(angle: Float) {}
}