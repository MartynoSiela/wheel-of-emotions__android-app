package com.example.wheel_of_emotions

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.devs.vectorchildfinder.VectorChildFinder
import kotlin.math.abs
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element

class MainActivity : AppCompatActivity() {

    private lateinit var imageViewWheel : ImageView
    private lateinit var imageViewEmotions : ImageView
    private lateinit var buttonAddEmotions : Button
    private lateinit var buttonShowEmotions : Button

    // Color for selected section
    private val colorSelectedRgb = Color.rgb(255,255,255)
    private val colorSelectedInt = 16777215

    // Variables used for changing section color
    private var colorValuePreviousNegativeInt = -1
    private var colorValuePreviousRgbInt = -1
    private var feelingIdPrevious = 0

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

        Feeling().parseFeelingXml(this)

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
        // Get feeling id and color value of the currently selected section or the one that is being clicked
        val (feelingId, colorValue) = if (pixel == colorSelectedInt) {
            Pair(Feeling().getFeelingByColor(colorValuePreviousRgbInt)?.id, colorValuePreviousRgbInt)
        } else {
            Pair(Feeling().getFeelingByColor(pixel)?.id, pixel)
        }
        // Change color of currently clicked section and restore previously selected one
        feelingId?.let { changeSectionColor(imageViewWheel, it, colorValue) }

        // Enable or disable button to add an emotion based on clicked section status
        if (buttonAddEmotions.isEnabled && colorValuePreviousNegativeInt == -1) {
            buttonAddEmotions.isEnabled = false
        } else if (!buttonAddEmotions.isEnabled && colorValuePreviousNegativeInt != -1) {
            buttonAddEmotions.isEnabled = true
        }
    }

    private fun changeSectionColor(imageView: ImageView, feelingIdCurrent: Int, colorValueCurrent: Int) {
        val vector = VectorChildFinder(this, R.drawable.wheel, imageView)
        val sectionCurrent = vector.findPathByName(Feeling().getFeelingById(feelingIdCurrent)?.uniqueName)

        // No section is selected and a section is clicked
        if (colorValuePreviousNegativeInt == -1) {
            colorValuePreviousNegativeInt = sectionCurrent.fillColor
            colorValuePreviousRgbInt = colorValueCurrent
            feelingIdPrevious = feelingIdCurrent
            sectionCurrent.fillColor = colorSelectedRgb
        }
        // A section is selected but a different section is clicked
        else if (feelingIdPrevious != feelingIdCurrent) {
            val sectionPrevious = vector.findPathByName(Feeling().getFeelingById(feelingIdCurrent)?.uniqueName)
            sectionPrevious.fillColor = colorValuePreviousNegativeInt
            colorValuePreviousNegativeInt = sectionCurrent.fillColor
            colorValuePreviousRgbInt = colorValueCurrent
            feelingIdPrevious = feelingIdCurrent
            sectionCurrent.fillColor = colorSelectedRgb
        }
        // A section is selected and the same section is clicked
        else {
            sectionCurrent.fillColor = colorValuePreviousNegativeInt
            colorValuePreviousNegativeInt = -1
            colorValuePreviousRgbInt = -1
            feelingIdPrevious = 0
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
    }

    private open inner class ButtonAddEmotionListener : View.OnClickListener {
        override fun onClick(view: View) {
            // Add selected emotion to database and show confirmation
            val db = DBHelper(this@MainActivity, null)
            val feelingId = feelingIdPrevious
            db.addEmotion(feelingId, System.currentTimeMillis())
            Toast.makeText(this@MainActivity, "Emotion added", Toast.LENGTH_SHORT).show()

            // Revert section color for it to become unselected and disable button
            changeSectionColor(imageViewWheel, feelingIdPrevious, colorValuePreviousRgbInt)
            buttonAddEmotions.isEnabled = false
        }
    }

    private open inner class ButtonShowEmotionsListener : View.OnClickListener {
        override fun onClick(view: View) {
            val intent = Intent(this@MainActivity, EmotionsTableActivity::class.java)
            startActivity(intent)
        }
    }

    class Feeling {
        var id: Int = 0
            private set
        var uniqueName: String = ""
            private set
        var nameEn: String = ""
            private set
        var colorARGB: Int = 0
            private set
        private var position: String = ""
        private var originCenterLt: String? = ""
        private var originInnerLt: String? = ""
        private var nameLt: String = ""
        private var originCenterEn: String? = ""
        private var originInnerEn: String? = ""
        private var colorDec: Int = 0
        private var colorHex: String = ""
        private var colorR: Int = 0
        private var colorG: Int = 0
        private var colorB: Int = 0

        companion object {
            private val feelingsMap = mutableMapOf<Int, Feeling>()
        }

        private fun getContent(node: Element, tagName: String) : String {
            return node.getElementsByTagName(tagName).item(0).textContent
        }

        private fun getContentNullable(node: Element, tagName: String) : String? {
            val content = node.getElementsByTagName(tagName).item(0).textContent
            return if (content == "x") null else content
        }

        fun getFeelingById(id: Int) : Feeling? {
            return feelingsMap[id]
        }

        fun getFeelingByColor(color: Int) : Feeling? {
            return feelingsMap.values.firstOrNull() { feeling -> feeling.colorDec == color }
        }

        fun parseFeelingXml(context: Context) {

            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val inputStream = context.resources.openRawResource(R.raw.feelings)
            val doc = builder.parse(inputStream)
            val feelingNodes = doc.getElementsByTagName("feeling")
            for (i in 0 until feelingNodes.length) {
                val node = feelingNodes.item(i) as Element
                val feeling = Feeling()
                feeling.id = getContent(node, "id").toInt()
                feeling.uniqueName = getContent(node, "unique_name")
                feeling.position = getContent(node, "position")
                feeling.originCenterLt = getContentNullable(node, "origin_center_lt")
                feeling.originInnerLt = getContentNullable(node, "origin_inner_lt")
                feeling.nameLt = getContent(node, "name_lt")
                feeling.originCenterEn = getContentNullable(node, "origin_center_en")
                feeling.originInnerEn = getContentNullable(node, "origin_inner_en")
                feeling.nameEn = getContent(node, "name_en")
                feeling.colorDec = getContent(node, "color_dec").toInt()
                feeling.colorHex = getContent(node, "color_hex")
                feeling.colorR = getContent(node, "color_r").toInt()
                feeling.colorG = getContent(node, "color_g").toInt()
                feeling.colorB = getContent(node, "color_b").toInt()
                feeling.colorARGB = Color.argb(255, feeling.colorR, feeling.colorG, feeling.colorB)
                feelingsMap[feeling.id] = feeling
            }
        }
    }
}