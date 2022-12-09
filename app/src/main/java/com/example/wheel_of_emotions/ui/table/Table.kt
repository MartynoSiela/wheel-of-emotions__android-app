package com.example.wheel_of_emotions.ui.table

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.example.wheel_of_emotions.DBHelper
import com.example.wheel_of_emotions.Feeling
import com.example.wheel_of_emotions.R
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class Table {

    companion object {
        val cellShape = GradientDrawable()
        val cellBorderWidth = 2
        val cellCornerRadius = 5f
        var cellBorderColor = 0
        lateinit var cellDefaultBackgroundColor : ColorStateList
    }

    fun writeTableRows(context: Context, tableLayout: TableLayout) {
        val db = DBHelper(context, null)
        val data = db.getEmotions()
        if (data?.count != 0) {
            data?.moveToFirst()
            do {
                val tableRow: View = LayoutInflater.from(context).inflate(R.layout.table_row, null, false)
                val emotionIdCell = tableRow.findViewById<TextView>(R.id.column_id)
                val emotionNameCell = tableRow.findViewById<TextView>(R.id.column_emotion)
                val emotionDateCell = tableRow.findViewById<TextView>(R.id.column_date)

                val emotionEntryId = data?.getString(0)
                val emotionId = data?.getString(1)?.toInt()
                val emotionDate = data?.getString(2)

                emotionIdCell.text = emotionEntryId
                emotionNameCell.text = Feeling().getFeelingById(emotionId!!)?.nameLt
                emotionDateCell.text = formatDate(emotionDate!!)

                val emotionColor = Feeling().getFeelingById(emotionId)?.colorARGB
                val emotionColorStateList = ColorStateList.valueOf(emotionColor!!)

                ViewCompat.setBackground(emotionIdCell, cellShape)
                ViewCompat.setBackground(emotionDateCell, cellShape)
                ViewCompat.setBackground(emotionNameCell, mutateCellShape(emotionColorStateList))
                tableLayout.addView(tableRow)
            } while (data?.moveToNext() == true)
            data.close()
            db.close()
        }
    }

    fun initiateCellShape(context: Context) {
        cellBorderColor = ContextCompat.getColor(context, R.color.purple_500)
        cellDefaultBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
        cellShape.shape = GradientDrawable.RECTANGLE
        cellShape.cornerRadius = cellCornerRadius
        cellShape.color = cellDefaultBackgroundColor
        cellShape.setStroke(cellBorderWidth, cellBorderColor)
    }

    private fun mutateCellShape(backgroundColor: ColorStateList) : GradientDrawable {
        val mutatedCellShape = cellShape.mutate().constantState?.newDrawable() as GradientDrawable
        mutatedCellShape.color = backgroundColor
        return mutatedCellShape
    }

    private fun formatDate(timestampString : String) : String {
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