package com.example.wheel_of_emotions

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.devs.vectorchildfinder.VectorChildFinder
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var imageViewWheel : ImageView
    private lateinit var imageViewEmotions : ImageView
    private lateinit var buttonAddEmotions : Button
    private lateinit var buttonShowEmotions : Button
    private lateinit var mapColors : MutableMap<Int, String>

    // Color for selected section
    private val colorSelectedRgb = Color.rgb(255,255,255)
    private val colorSelectedInt = 16777215

    // Variables used for changing section color
    private var colorValuePreviousNegativeInt = -1
    private var colorValuePreviousRgbInt = -1
    private var colorNamePrevious = "null"

    // Variables used for rotation of the wheel and clicking on sections
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        // Map is created to link section names with their colors
        val values = arrayOf("angry", "disgusted", "sad", "happy", "surprised", "bad", "fearful", "aggressive", "frustrated", "distant", "critical", "disapproving", "disappointed_disgusted", "awful", "repelled", "hurt", "depressed", "guilty", "despair", "vulnerable", "lonely", "optimistic", "trusting", "peaceful", "powerful", "accepted", "proud", "interested", "content", "playful", "excited", "amazed", "confused", "startled", "tired", "stressed", "busy", "bored", "scared", "anxious", "insecure", "weak", "rejected", "threatened", "let_down", "humiliated", "bitter", "mad", "provoked", "hostile", "infuriated", "annoyed", "withdrawn", "numb", "sceptical", "dismissive", "judgemental", "embarrassed_disgusted", "appalled", "revolted", "nauseated", "detestable", "horrified", "hesitant", "embarrassed_sad", "disappointed_sad", "inferior_sad", "empty", "remorseful", "ashamed", "powerless", "grief", "fragile", "victimised", "abandoned", "isolated", "inspired", "hopeful", "intimate", "sensitive", "thankful", "loving", "creative", "courageous", "valued", "respected", "confident", "successful", "inquisitive", "curious", "joyful", "free", "cheeky", "aroused", "energetic", "eager", "awe", "astonished", "perplexed", "disillusioned", "dismayed", "shocked", "unfocussed", "sleepy", "out_of_control", "overwhelmed_bad", "rushed", "pressured", "apathetic", "indifferent", "helpless", "frightened", "overwhelmed_fearful", "worried", "inadequate", "inferior_fearful", "worthless", "insignificant", "excluded", "persecuted", "nervous", "exposed", "betrayed", "resentful", "disrespected", "ridiculed", "indignant", "violated", "furious", "jealous")
        val keys = arrayOf(16739436, 13948116, 9159423, 16777053, 13806079, 11141033, 16761483, 16745861, 16747146, 16748431, 16749716, 14145495, 14277081, 14474460, 14606046, 11129599, 10801151, 10472959, 10144511, 9816063, 9487871, 16777058, 16777063, 16777068, 16777073, 16777078, 16777083, 16777088, 16777094, 16777099, 14532351, 14334463, 14136319, 14003967, 11468718, 11796403, 12189625, 12517310, 16765609, 16764836, 16764319, 16763546, 16762773, 16762256, 16740721, 16742006, 16743291, 16744576, 16760253, 16758968, 16757683, 16756398, 16755113, 16753828, 16752543, 16751258, 15921906, 15790320, 15592941, 15461355, 15263976, 15132390, 14935011, 14803425, 11458047, 11786239, 12180223, 12508671, 12837119, 13165311, 13493759, 13822207, 14150399, 14478847, 14807295, 15135487, 16777190, 16777185, 16777180, 16777175, 16777170, 16777165, 16777160, 16777155, 16777150, 16777145, 16777139, 16777134, 16777129, 16777124, 16777119, 16777114, 16777109, 16777104, 14730239, 14862591, 15060223, 15258367, 15390719, 15588607, 15786495, 15918847, 15138790, 14811105, 14483420, 14155735, 13828050, 13500365, 13172680, 12844995, 16766382, 16766899, 16767673, 16768446, 16769219, 16769736, 16770509, 16771282, 16771799, 16772572, 16773345, 16773862, 16770790, 16769505, 16768220, 16766935, 16765650, 16764365, 16762823, 16761538)
        mapColors = mutableMapOf<Int, String>()
            .apply { for (i in keys.indices) this[keys[i]] = values[i] }

        imageViewWheel = findViewById(R.id.imageViewWheel)
        imageViewEmotions = findViewById(R.id.imageViewEmotions)
        buttonAddEmotions = findViewById(R.id.button_add_emotion)
        buttonShowEmotions = findViewById(R.id.button_show_emotions)
        buttonAddEmotions.isEnabled = false

        buttonAddEmotions.setOnClickListener(ButtonAddEmotionListener())
        buttonShowEmotions.setOnClickListener(ButtonShowEmotionsListener())
        imageViewWheel.setOnTouchListener(WheelTouchListener())
    }

    private fun onSwipe(angle: Float) {
        rotateImageWithoutAnimation(imageViewWheel, angle)
        rotateImageWithoutAnimation(imageViewEmotions, angle)
    }

    private fun getPixelColorName(pixel: Int) {
        // Get color name and value of the currently selected section or the one that is being clicked
        val (colorName, colorValue) = if (pixel == colorSelectedInt) {
            Pair(mapColors[colorValuePreviousRgbInt], colorValuePreviousRgbInt)
        } else {
            Pair(mapColors[pixel], pixel)
        }

        // Change color of currently clicked section and restore previously selected one
        if (colorName != null) {
            changeSectionColor(imageViewWheel, colorName, colorValue)
        }

        // Enable or disable button to add an emotion based on clicked section status
        if (buttonAddEmotions.isEnabled && colorValuePreviousNegativeInt == -1) {
            buttonAddEmotions.isEnabled = false
        } else if (!buttonAddEmotions.isEnabled && colorValuePreviousNegativeInt != -1) {
            buttonAddEmotions.isEnabled = true
        }
    }

    private fun changeSectionColor(imageView: ImageView, colorNameCurrent: String, colorValueCurrent: Int) {
        val vector = VectorChildFinder(this, R.drawable.wheel, imageView)
        val sectionCurrent = vector.findPathByName(colorNameCurrent)

        // No section is selected and a section is clicked
        if (colorValuePreviousNegativeInt == -1) {
            colorValuePreviousNegativeInt = sectionCurrent.fillColor
            colorValuePreviousRgbInt = colorValueCurrent
            colorNamePrevious = colorNameCurrent
            sectionCurrent.fillColor = colorSelectedRgb
        }
        // A section is selected but a different section is clicked
        else if (colorNamePrevious != colorNameCurrent) {
            val sectionPrevious = vector.findPathByName(colorNamePrevious)
            sectionPrevious.fillColor = colorValuePreviousNegativeInt
            colorValuePreviousNegativeInt = sectionCurrent.fillColor
            colorValuePreviousRgbInt = colorValueCurrent
            colorNamePrevious = colorNameCurrent
            sectionCurrent.fillColor = colorSelectedRgb
        }
        // A section is selected and the same section is clicked
        else {
            sectionCurrent.fillColor = colorValuePreviousNegativeInt
            colorValuePreviousNegativeInt = -1
            colorValuePreviousRgbInt = -1
            colorNamePrevious = "null"
        }
    }

    private fun rotateImageWithoutAnimation(imageView: ImageView, angle: Float) {
        // Adjust the angle so that it is possible to rotate 90 degrees with full horizontal swipe
        // TODO refactor 1080 to get the actual width of the screen
        imageView.rotation = imageView.rotation + angle * 90 / 1080
    }

    private fun rotateImageWithAnimation(imageView: ImageView, angle: Float) {
        imageView.isEnabled = false
        val rotation = imageView.animate().rotation(imageView.rotation + angle / 2)
        // rotation.interpolator = AccelerateDecelerateInterpolator()
        rotation.interpolator = FastOutLinearInInterpolator()
        rotation.setListener(object : Animator.AnimatorListener{
            override fun onAnimationEnd(animation: Animator?) {
                imageView.isEnabled = true
            }
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
        })
        rotation.start()
    }

    private open inner class WheelTouchListener : View.OnTouchListener {
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
    }

    private open inner class ButtonAddEmotionListener : View.OnClickListener {
        override fun onClick(view: View) {
            // Add selected emotion to database and show confirmation
            val db = DBHelper(this@MainActivity, null)
            val emotion = colorNamePrevious
            db.addEmotion(emotion, System.currentTimeMillis())
            Toast.makeText(this@MainActivity, "Emotion added", Toast.LENGTH_SHORT).show()

            // Revert section color for it to become unselected and disable button
            changeSectionColor(imageViewWheel, colorNamePrevious, colorValuePreviousRgbInt)
            buttonAddEmotions.isEnabled = false
        }
    }

    private open inner class ButtonShowEmotionsListener : View.OnClickListener {
        override fun onClick(view: View) {
            val intent = Intent(this@MainActivity, EmotionsTableActivity::class.java)
            startActivity(intent)
        }
    }
}