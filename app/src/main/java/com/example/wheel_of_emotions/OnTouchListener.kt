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
                true
            }
            MotionEvent.ACTION_MOVE -> {
                moved = true
                currentX = event.x
                currentY = event.y
                diffX = currentX - previousX
                diffY = currentY - previousY
                when (swipeHorizontal) {
                    "LEFT" -> {
                        if (currentX > previousX) {
                            swipeHorizontal = "RIGHT"
                            initialX = previousX
                            diffX = currentX - initialX
                        } else {
                            //
                        }
                    }
                    "RIGHT" -> {
                        if (currentX < previousX) {
                            swipeHorizontal = "LEFT"
                            initialX = previousX
                            diffX = currentX - initialX
                        } else {
                            //
                        }
                    }
                    else -> {
                        if (currentX < initialX) {
                            swipeHorizontal = "LEFT"
                            diffX = currentX - initialX
                        } else if (currentX > initialX) {
                            swipeHorizontal = "RIGHT"
                            diffX = currentX - initialX
                        } else {
                            //
                        }
                    }
                }
                when (swipeVertical) {
                    "UP" -> {
                        if (currentY > previousY) {
                            swipeVertical = "DOWN"
                            initialY = previousY
                            diffY = currentY - initialY
                        } else {
                            //
                        }
                    }
                    "DOWN" -> {
                        if (currentY < previousY) {
                            swipeVertical = "UP"
                            initialY = previousY
                            diffY = currentY - initialY
                        } else {
                            //
                        }
                    }
                    else -> {
                        if (currentY < initialY) {
                            swipeVertical = "UP"
                            diffY = currentY - initialY
                        } else if (currentY > initialY) {
                            swipeVertical = "DOWN"
                            diffY = currentY - initialY
                        } else {
                            //
                        }
                    }
                }

                previousX = currentX
                previousY = currentY

                if (abs(diffX) > abs(diffY)) {
                    if (currentY > centerY) {
                        if (abs(diffX) > swipeThreshold) {
                            if (diffX > 0) {
                                onSwipe(-diffX)
                            } else {
                                onSwipe(-diffX)
                            }
                        }
                    } else {
                        if (abs(diffX) > swipeThreshold) {
                            if (diffX > 0) {
                                onSwipe(diffX)
                            } else {
                                onSwipe(diffX)
                            }
                        }
                    }
                } else {
                    if (currentX > centerX) {
                        if (abs(diffY) > swipeThreshold) {
                            if (diffY > 0) {
                                onSwipe(diffY)
                            } else {
                                onSwipe(diffY)
                            }
                        }
                    } else {
                        if (abs(diffY) > swipeThreshold) {
                            if (diffY > 0) {
                                onSwipe(-diffY)
                            } else {
                                onSwipe(-diffY)
                            }
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
                swipeHorizontal = "0"
                swipeVertical = "0"
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

    open fun onSwipe(angle: Float) {}
}