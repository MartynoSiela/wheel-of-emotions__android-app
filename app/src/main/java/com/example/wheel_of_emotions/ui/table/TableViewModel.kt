package com.example.wheel_of_emotions.ui.table

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wheel_of_emotions.DBHelper
import com.example.wheel_of_emotions.R
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class TableViewModel : ViewModel() {

    private val _colorBorder = R.color.purple_500
    private val _colorBackground = R.color.white
    private val _cellShape = GradientDrawable()
    private val _cellBorderWidth = 2
    private val _cellCornerRadius = 5f
    private var _cellBorderColor = 0
    private var _tableCleared = MutableLiveData<Boolean>()
    private lateinit var _cellDefaultBackgroundColor : ColorStateList

    var tableCleared: MutableLiveData<Boolean>
        set(value) { _tableCleared = value}
        get() = _tableCleared
    val cellShape: GradientDrawable
        get() = _cellShape

    fun getEmotions(context: Context) : MutableList<ArrayList<String?>> {
        val emotionsList: MutableList<ArrayList<String?>> = mutableListOf()
        val db = DBHelper(context, null)
        val emotions = db.getEmotions()

        if (emotions?.count != 0) {
            emotions?.moveToFirst()
            do {
                val array = ArrayList<String?>()
                array.add(emotions?.getString(0))
                array.add(emotions?.getString(1))
                array.add(emotions?.getString(2))
                emotionsList.add(array)
            } while (emotions?.moveToNext() == true)
        }

        emotions?.close()
        db.close()

        return emotionsList
    }

    fun initiateCellShape(context: Context) {
        _cellBorderColor = ContextCompat.getColor(context, _colorBorder)
        _cellDefaultBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, _colorBackground))
        _cellShape.shape = GradientDrawable.RECTANGLE
        _cellShape.cornerRadius = _cellCornerRadius
        _cellShape.color = _cellDefaultBackgroundColor
        _cellShape.setStroke(_cellBorderWidth, _cellBorderColor)
    }

    fun mutateCellShape(backgroundColor: ColorStateList) : GradientDrawable {
        val mutatedCellShape = _cellShape.mutate().constantState?.newDrawable() as GradientDrawable
        mutatedCellShape.color = backgroundColor
        return mutatedCellShape
    }

    fun formatDate(timestampString : String) : String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val timestamp = Timestamp(timestampString.toLong())
        val date = Date(timestamp.time)
        return dateFormat.format(date)
    }

    fun clearTable(context: Context) {
        val db = DBHelper(context, null)
        db.clearTable(DBHelper.TABLE_NAME)
        db.close()
    }
}