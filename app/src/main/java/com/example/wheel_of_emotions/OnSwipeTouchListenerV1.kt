package com.example.wheel_of_emotions

import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

// Found here: https://stackoverflow.com/a/19067893/4054411
// This code allows to 'reset' the swipe direction when the finger is not lifted during the swipe
// This is still glitchy as are the other implementations I've tried so far
open class OnSwipeTouchListenerV1() : View.OnTouchListener {

    private val SWIPE_THRESHOLD = 0f
    private var initialX = 0f
    private var initialY = 0f
    private var previousX = 0f
    private var previousY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private var diffX = 0f
    private var diffY = 0f
    private var swipeH = "0"

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.i("EVENT", "--------------------- START -------------------------")
                Log.i("EVENT", "${event.action} INITIAL|$initialX PREVIOUS|$previousX CURRENT|$currentX DIFF|$diffX")

                initialX = event.x
                initialY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                currentX = event.x
                currentY = event.y
                // These where original diff calculations
                // The issue with this is that diffs get bigger when swiping further
                // It is an issue because I'm trying to use the diff as an angle to rotate by
                // diffX = currentX - initialX; // ORIGINAL
                // diffY = currentY - initialY; // ORIGINAL
                diffX = currentX - previousX // TRY 1 (SHIT)
                diffY = currentY - previousY // TRY 1 (SHIT)

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
                        } else if (currentX > initialX) {
                            swipeH = "RIGHT"
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
                            Log.i("EVENT", "${event.action} INITIAL|$initialX PREVIOUS|$previousX CURRENT|$currentX DIFF|$diffX")
                            onSwipeRight(diffX)
                        } else {
                            Log.i("EVENT", "${event.action} INITIAL|$initialX PREVIOUS|$previousX CURRENT|$currentX DIFF|$diffX")
                            onSwipeLeft(diffX)
                        }
                    }
                } else {
                    if (abs(diffY) > SWIPE_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom(diffY)
                        } else {
                            onSwipeTop(diffY)
                        }
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                Log.i("EVENT", "--------------------- END -------------------------")
                swipeH = "0"
                initialX = 0f
                initialY = 0f
                previousX = 0f
                previousY = 0f
                diffX = 0f
                diffY = 0f

                return true
            }
            else -> {
                return false
            }
        }
    }

    open fun onSwipeRight(diffX: Float) {}

    open fun onSwipeLeft(diffX: Float) {}

    open fun onSwipeTop(diffY: Float) {}

    open fun onSwipeBottom(diffY: Float) {}
}