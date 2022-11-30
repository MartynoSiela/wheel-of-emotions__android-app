package com.example.wheel_of_emotions

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var detector: GestureDetectorCompat
    private val wheel = findViewById<ImageView>(R.id.imageView1)
    private val emotions = findViewById<ImageView>(R.id.imageView2)

    private fun rotateImage(imageView: ImageView, angle: Float) {
        imageView.animate().rotation(angle).start()
    }

    private fun onSwipeDown(diffY: Float, velocityY: Float) {
        // TODO
    }

    private fun onSwipeTop(diffY: Float, velocityY: Float) {
        // TODO
    }

    private fun onSwipeLeft(diffX: Float, velocityX: Float) {
        rotateImage(wheel, diffX * velocityX)
        rotateImage(emotions, diffX * velocityX)
    }

    private fun onSwipeRight(diffX: Float, velocityX: Float) {
        rotateImage(wheel, diffX * velocityX)
        rotateImage(emotions, diffX * velocityX)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        detector = GestureDetectorCompat(this, GestureListener())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (detector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        private val SWIPE_THRESHOLD = 0
        private val SWIPE_VELOCITY_THRESHOLD = 0

        override fun onFling(
            eventDown: MotionEvent?,
            eventMove: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffX = eventMove?.x?.minus(eventDown!!.x) ?: 0.0f
            val diffY = eventMove?.x?.minus(eventDown!!.y) ?: 0.0f

            return if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        this@MainActivity.onSwipeRight(diffX, velocityX)
                    } else {
                        this@MainActivity.onSwipeLeft(diffX, velocityX)
                    }
                    true
                } else {
                    super.onFling(eventDown, eventMove, velocityX, velocityY)
                }
            } else {
                if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        this@MainActivity.onSwipeTop(diffY, velocityY)
                    } else {
                        this@MainActivity.onSwipeDown(diffY, velocityY)
                    }
                    true
                } else {
                    super.onFling(eventDown, eventMove, velocityX, velocityY)
                }
            }
        }
    }
}