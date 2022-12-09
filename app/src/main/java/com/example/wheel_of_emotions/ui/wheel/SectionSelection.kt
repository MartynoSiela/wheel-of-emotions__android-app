package com.example.wheel_of_emotions.ui.wheel

import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import com.devs.vectorchildfinder.VectorChildFinder
import com.example.wheel_of_emotions.Feeling
import com.example.wheel_of_emotions.R

open class SectionSelection {

    companion object {
        val colorSelectedRgb = Color.rgb(255,255,255)
        val colorSelectedInt = 16777215
        var colorValuePreviousNegativeInt = -1
        var colorValuePreviousRgbInt = -1
        var feelingIdPrevious = 0
    }

    open fun changeSectionColor(context: Context, imageView: ImageView, feelingIdCurrent: Int, colorValueCurrent: Int) {

        val vector = VectorChildFinder(context, R.drawable.wheel, imageView)
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
}