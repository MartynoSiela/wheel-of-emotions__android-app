package com.example.wheel_of_emotions

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class EmotionsTableActivity : AppCompatActivity() {

    private var cellShape = GradientDrawable()
    private val cellBorderWidth = 2
    private val cellCornerRadius = 5f
    private var cellBorderColor = 0
    private lateinit var cellDefaultBackgroundColor : ColorStateList

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_emotions_table)

        cellBorderColor = ContextCompat.getColor(this, R.color.purple_500)
        cellDefaultBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
        initiateCellShape()

        val tableLayout = findViewById<TableLayout>(R.id.table_layout)
        val buttonShowWheel = findViewById<Button>(R.id.button_show_wheel)
        val buttonClearEmotions = findViewById<Button>(R.id.button_clear_emotions)

        writeTableRows(tableLayout)

        buttonShowWheel.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        buttonClearEmotions.setOnClickListener{
            val db = DBHelper(this, null)
            db.clearTable(DBHelper.TABLE_NAME)
            db.close()
            this.recreate()
        }
    }

    private fun writeTableRows(tableLayout : TableLayout) {
        val db = DBHelper(this, null)
        val data = db.getEmotions()
        if (data?.count != 0) {
            data?.moveToFirst()
            do {
                val tableRow: View = LayoutInflater.from(this).inflate(R.layout.table_row, null, false)
                val emotionIdCell = tableRow.findViewById<TextView>(R.id.column_id)
                val emotionNameCell = tableRow.findViewById<TextView>(R.id.column_emotion)
                val emotionDateCell = tableRow.findViewById<TextView>(R.id.column_date)

                val emotionId = data?.getString(0)
                val emotionName = data?.getString(1)
                val emotionDate = data?.getString(2)

                emotionIdCell.text = emotionId
                emotionNameCell.text = emotionName
                emotionDateCell.text = formatDate(emotionDate!!)

                val emotionColor = MainActivity.Feeling().getColorARGBByFeelingName(emotionName)
                val emotionColorStateList = ColorStateList.valueOf(emotionColor)

                ViewCompat.setBackground(emotionIdCell, cellShape)
                ViewCompat.setBackground(emotionDateCell, cellShape)
                ViewCompat.setBackground(emotionNameCell, mutateCellShape(emotionColorStateList))

                tableLayout.addView(tableRow)
            } while (data?.moveToNext() == true)
            data.close()
            db.close()
        }
    }

    private fun initiateCellShape() {
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
}