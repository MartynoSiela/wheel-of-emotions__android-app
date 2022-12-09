package com.example.wheel_of_emotions.ui.wheel

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.drawToBitmap
import com.example.wheel_of_emotions.Feeling
import kotlin.math.abs

open class WheelTouchListener(
    private val context: Context,
    private val imageViewWheel: ImageView,
    private val imageViewEmotions: ImageView,
    private val buttonAddEmotions: Button
    ) : View.OnTouchListener {

    private val swipeThreshold = 2f
    private var initialX = 0f
    private var initialY = 0f
    private var initialRawX = 0f
    private var initialRawY = 0f
    private var previousX = 0f
    private var previousY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private var diffX = 0f
    private var diffY = 0f
    private var swipeHorizontal = "0"
    private var swipeVertical = "0"
    private var moved = false

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        // Get center of the wheel and adjust it based on actual view
        val bitmap = imageViewWheel.drawToBitmap()
        val centerX = bitmap.width / 2 + imageViewWheel.left
        val centerY = bitmap.height / 2 + imageViewWheel.top

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                moved = false
                initialX = event.x
                initialY = event.y
                initialRawX = event.rawX
                initialRawY = event.rawY
                previousX = event.rawX
                previousY = event.rawY
                true
            }
            MotionEvent.ACTION_MOVE -> {
                moved = true
                currentX = event.rawX
                currentY = event.rawY
                diffX = currentX - previousX
                diffY = currentY - previousY

                // Set initial direction
                when {
                    currentX < initialRawX -> {
                        swipeHorizontal = "LEFT"
                    }
                    currentX > initialRawX -> {
                        swipeHorizontal = "RIGHT"
                    }
                    currentY < initialRawY -> {
                        swipeVertical = "UP"
                    }
                    currentY > initialRawY -> {
                        swipeVertical = "DOWN"
                    }
                }

                // Reverse direction
                when {
                    swipeHorizontal == "LEFT" && currentX > previousX -> {
                        swipeHorizontal = "RIGHT"
                        initialRawX = previousX
                    }
                    swipeHorizontal == "RIGHT" && currentX < previousX -> {
                        swipeHorizontal = "LEFT"
                        initialRawX = previousX
                    }
                    swipeVertical == "UP" && currentY > previousY -> {
                        swipeVertical = "DOWN"
                        initialRawY = previousY
                    }
                    swipeVertical == "DOWN" && currentY < previousY -> {
                        swipeVertical = "UP"
                        initialRawY = previousY
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

                val colorPixel = bitmap.getPixel(initialX.toInt(), initialY.toInt())
                // If the wheel was not moved then there was a click action to selected a section
                if (!moved && colorPixel != 0) {
                    val colorSectionSelectedHex = String.format("#%02x%02x%02x", Color.red(colorPixel), Color.green(colorPixel), Color.blue(colorPixel))
                    getPixelColorName(Integer.decode(colorSectionSelectedHex))
                    view.playSoundEffect(SoundEffectConstants.CLICK)
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


    private fun onSwipe(angle: Float) {
        rotateImageWithoutAnimation(imageViewWheel, angle)
        rotateImageWithoutAnimation(imageViewEmotions, angle)
    }

    private fun rotateImageWithoutAnimation(imageView: ImageView, angle: Float) {
        // Adjust the angle so that it is possible to rotate 90 degrees with full horizontal swipe
        // TODO refactor 1080 to get the actual width of the screen
        imageView.rotation = imageView.rotation + angle * 90 / 1080
    }

    private fun getPixelColorName(pixel: Int) {
        // Get feeling id and color value of the currently selected section or the one that is being clicked
        val (feelingId, colorValue) = if (pixel == SectionSelection.colorSelectedInt) {
            Pair(Feeling().getFeelingByColor(SectionSelection.colorValuePreviousRgbInt)?.id, SectionSelection.colorValuePreviousRgbInt)
        } else {
            Pair(Feeling().getFeelingByColor(pixel)?.id, pixel)
        }
        // Change color of currently clicked section and restore previously selected one
        feelingId?.let { SectionSelection().changeSectionColor(context, imageViewWheel, it, colorValue) }

        // Enable or disable button to add an emotion based on clicked section status
        if (buttonAddEmotions.isEnabled && SectionSelection.colorValuePreviousNegativeInt == -1) {
            buttonAddEmotions.isEnabled = false
        } else if (!buttonAddEmotions.isEnabled && SectionSelection.colorValuePreviousNegativeInt != -1) {
            buttonAddEmotions.isEnabled = true
        }
    }
}