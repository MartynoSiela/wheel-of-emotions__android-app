package com.example.wheel_of_emotions.ui.wheel

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.devs.vectorchildfinder.VectorChildFinder
import com.devs.vectorchildfinder.VectorDrawableCompat
import com.example.wheel_of_emotions.DBHelper
import com.example.wheel_of_emotions.Feeling
import com.example.wheel_of_emotions.R
import com.example.wheel_of_emotions.databinding.FragmentWheelBinding
import kotlin.math.abs

class WheelFragment : Fragment() {
    private var _binding: FragmentWheelBinding? = null
    private lateinit var wheel: ImageView
    private lateinit var emotions: ImageView
    private lateinit var buttonAddEmotions: Button
    private lateinit var viewModel: WheelViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[WheelViewModel::class.java]
        _binding = FragmentWheelBinding.inflate(inflater, container, false)
        wheel = binding.imageViewWheel
        emotions = binding.imageViewEmotions
        buttonAddEmotions = binding.buttonAddEmotion
        buttonAddEmotions.isEnabled = false
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonAddEmotions.setOnClickListener {
            val db = DBHelper(requireContext(), null)
            db.addEmotion(viewModel.emotionPreviousId, System.currentTimeMillis())
            Toast.makeText(context, "Emotion added", Toast.LENGTH_SHORT).show()
            changeWheelSectionColor(viewModel.emotionPreviousId)
            buttonAddEmotions.isEnabled = false
        }

        wheel.setOnTouchListener { viewListener: View, motionEvent: MotionEvent ->
            return@setOnTouchListener when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    drawWheelBitmap(false)
                    calculateWheelCenterCoordinates()
                    viewModel.setInitialTouchCoordinates(motionEvent)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    viewModel.calculateTouchMotionCoordinates(motionEvent)
                    doRotation()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!viewModel.moved) {
                        drawWheelBitmap(true)
                        doSelection(viewListener)
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun drawWheelBitmap(redraw: Boolean) {
        if (viewModel.bitmap == null || redraw) {
            viewModel.bitmap = wheel.drawToBitmap()
        }
    }

    private fun doRotation() {
        if (abs(viewModel.diffX) > abs(viewModel.diffY) && abs(viewModel.diffX) > viewModel.swipeThreshold) {
            if (viewModel.currentY > viewModel.centerY) onSwipe(-viewModel.diffX) else onSwipe(viewModel.diffX)
        } else
        if (abs(viewModel.diffX) < abs(viewModel.diffY) && abs(viewModel.diffY) > viewModel.swipeThreshold) {
            if (viewModel.currentX > viewModel.centerX) onSwipe(viewModel.diffY) else onSwipe(-viewModel.diffY)
        }
    }

    private fun doSelection(view: View) {
        val colorPixel = viewModel.getCurrentPixelValue()
        if (colorPixel != 0) {
            val colorFormatted = String.format("#%02x%02x%02x", Color.red(colorPixel), Color.green(colorPixel), Color.blue(colorPixel))
            val colorInt = Integer.decode(colorFormatted)

            val emotionId = if (colorInt == viewModel.colorSelectedInt) {
                Feeling().getFeelingByColor(viewModel.colorPreviousRgbInt)?.id
            } else {
                Feeling().getFeelingByColor(colorInt)?.id
            }
            changeWheelSectionColor(emotionId!!)
            toggleButtonStatus()
            view.playSoundEffect(SoundEffectConstants.CLICK)
        }
    }

    private fun calculateWheelCenterCoordinates() {
        if (viewModel.centerY == 0) {
            viewModel.centerX = (wheel.left + wheel.right) / 2
            viewModel.centerY = (wheel.top + wheel.bottom) / 2
        }
    }

    private fun onSwipe(angle: Float) {
        rotateImageWithoutAnimation(wheel, angle)
        rotateImageWithoutAnimation(emotions, angle)
    }

    private fun rotateImageWithoutAnimation(imageView: ImageView, angle: Float) {
        // Adjust the angle so that it is possible to rotate 90 degrees with full horizontal swipe
        // TODO refactor 1080 to get the actual width of the screen
        imageView.rotation = imageView.rotation + angle * 90 / 1080
    }

    private fun changeWheelSectionColor(emotionCurrentId: Int) {
        var sectionCurrent = getWheelSectionByEmotionId(emotionCurrentId)
        if (viewModel.colorPreviousNegativeInt == -1) {
            setSectionAsSelected(emotionCurrentId, sectionCurrent)
        } else if (viewModel.emotionPreviousId != emotionCurrentId) {
            val sectionPrevious = getWheelSectionByEmotionId(viewModel.emotionPreviousId)
            sectionPrevious.fillColor = viewModel.colorPreviousNegativeInt
            sectionCurrent = getWheelSectionByEmotionId(emotionCurrentId)
            setSectionAsSelected(emotionCurrentId, sectionCurrent)
        } else {
            setSectionAsUnselected(sectionCurrent)
        }
    }

    private fun getWheelSectionByEmotionId(id: Int): VectorDrawableCompat.VFullPath {
        val vector = VectorChildFinder(requireContext(), R.drawable.wheel, wheel)
        return vector.findPathByName(Feeling().getFeelingById(id)?.uniqueName)
    }

    private fun setSectionAsSelected(emotionId: Int, section: VectorDrawableCompat.VFullPath) {
        viewModel.colorPreviousNegativeInt = section.fillColor
        viewModel.colorPreviousRgbInt = Feeling().getFeelingById(emotionId)?.colorDec!!
        viewModel.emotionPreviousId = emotionId
        section.fillColor = viewModel.colorSelectedRgb
    }

    private fun setSectionAsUnselected(section: VectorDrawableCompat.VFullPath) {
        section.fillColor = viewModel.colorPreviousNegativeInt
        viewModel.colorPreviousNegativeInt = -1
        viewModel.colorPreviousRgbInt = -1
        viewModel.emotionPreviousId = -1
    }

    private fun toggleButtonStatus() {
        buttonAddEmotions.isEnabled = viewModel.colorPreviousNegativeInt != -1
    }
}