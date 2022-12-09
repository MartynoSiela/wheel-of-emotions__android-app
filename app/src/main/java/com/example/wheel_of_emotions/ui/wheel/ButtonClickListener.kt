package com.example.wheel_of_emotions.ui.wheel

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.wheel_of_emotions.DBHelper

class ButtonClickListener(
    private val context: Context,
    private val imageViewWheel: ImageView,
    private val buttonAddEmotions: Button
    ) : View.OnClickListener {

    override fun onClick(view: View?) {
        val db = DBHelper(context, null)
        val feelingId = SectionSelection.feelingIdPrevious
        db.addEmotion(feelingId, System.currentTimeMillis())
        Toast.makeText(context, "Emotion added", Toast.LENGTH_SHORT).show()

        // Revert section color for it to become unselected and disable button
        SectionSelection().changeSectionColor(context, imageViewWheel, feelingId, SectionSelection.colorValuePreviousRgbInt)
        buttonAddEmotions.isEnabled = false
    }
}