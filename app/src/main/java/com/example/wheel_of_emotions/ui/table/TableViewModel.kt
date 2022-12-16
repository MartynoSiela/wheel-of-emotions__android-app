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
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.*

class TableViewModel : ViewModel() {

    private val _colorBorder = R.color.purple_500
    private val _colorBackground = R.color.white
    private val _cellShape = GradientDrawable()
    private val _cellBorderWidth = 2
    private val _cellCornerRadius = 5f
    private var _cellBorderColor = 0
    private var _tableUpdated = MutableLiveData<Boolean>()
    private lateinit var _cellDefaultBackgroundColor : ColorStateList

    private var _weekStartTimestamp: Long? = null
    private var _weekEndTimestamp: Long? = null
    private var _weekStartString: String? = null
    private var _weekEndString: String? = null
    private var _dateToFilterBy: LocalDateTime = LocalDateTime.now()
    private var _filter = MutableLiveData<String>()

    var tableUpdated: MutableLiveData<Boolean>
        set(value) { _tableUpdated = value}
        get() = _tableUpdated
    var filter: MutableLiveData<String>
        set(value) { _filter = value}
        get() = _filter
    val cellShape: GradientDrawable
        get() = _cellShape
    var dateToFilterBy: LocalDateTime
        set(value) { _dateToFilterBy = value}
        get() = _dateToFilterBy
    val weekStartString: String?
        get() = _weekStartString
    val weekEndString: String?
        get() = _weekEndString

    init {
        setDateValues(_dateToFilterBy)
    }

    fun getEmotions(context: Context) : MutableList<ArrayList<String?>> {
        val emotionsList: MutableList<ArrayList<String?>> = mutableListOf()
        val db = DBHelper(context, null)
        val emotions = when(_filter.value) {
            "week" -> db.getEmotionsByWeek(_weekStartTimestamp, _weekEndTimestamp)
            "none" -> db.getEmotions()
            else -> db.getEmotions()
        }

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

    fun setDateValues(date: LocalDateTime) {
        setWeekStartTimestamp(date)
        setWeekEndTimestamp(date)
        setWeekStartString(date)
        setWeekEndString(date)
    }

    private fun getWeekMonday(date: LocalDateTime) : ZonedDateTime {
        return date.atZone(ZoneId.systemDefault()).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    private fun getWeekSunday(date: LocalDateTime) : ZonedDateTime {
        return date.atZone(ZoneId.systemDefault()).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    }

    private fun setWeekStartTimestamp(date: LocalDateTime) {
        val monday = getWeekMonday(date)
        val mondayStart = LocalDateTime.of(monday.year, monday.month, monday.dayOfMonth, 0, 0, 1)
        _weekStartTimestamp = mondayStart.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
    }

    private fun setWeekEndTimestamp(date: LocalDateTime) {
        val sunday = getWeekSunday(date)
        val sundayEnd = LocalDateTime.of(sunday.year, sunday.month, sunday.dayOfMonth, 23, 59, 59)
        _weekEndTimestamp = sundayEnd.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
    }

    private fun setWeekStartString(date: LocalDateTime) {
        val monday = getWeekMonday(date)
        val day = monday.dayOfMonth
        val month = monday.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        _weekStartString = "$month $day"
    }

    private fun setWeekEndString(date: LocalDateTime) {
        val sunday = getWeekSunday(date)
        val day = sunday.dayOfMonth
        val month = sunday.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        _weekEndString = "$month $day"
    }

    fun getNextWeekDay(date: LocalDateTime) : LocalDateTime {
        val dayOfWeek = date.dayOfWeek
        return date.with(TemporalAdjusters.next(dayOfWeek))
    }

    fun getPreviousWeekDay(date: LocalDateTime) : LocalDateTime {
        val dayOfWeek = date.dayOfWeek
        return date.with(TemporalAdjusters.previous(dayOfWeek))
    }
}