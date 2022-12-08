package com.example.wheel_of_emotions

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class EmotionsTableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_emotions_table)
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
                tableRow.findViewById<TextView>(R.id.row_id).text = data?.getString(0)
                val emotionName = data?.getString(1)
                val emotionNameCell = tableRow.findViewById<TextView>(R.id.row_emotion)
                emotionNameCell.text = emotionName
                val emotionColor = Color.parseColor(MainActivity.Feeling().getColorHexByFeelingName(emotionName))
                // TODO find a way to maintain border color while changing background color
                // emotionNameCell.backgroundTintList = ColorStateList.valueOf(emotionColor)
                emotionNameCell.setBackgroundColor(emotionColor)
                tableRow.findViewById<TextView>(R.id.row_date).text = data?.getString(2)?.let { formatDate(it) }
                tableLayout.addView(tableRow)
            } while (data?.moveToNext() == true)
            data?.close()
            db.close()
        }
    }

    private fun formatDate(timestampString : String) : String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val timestamp = Timestamp(timestampString.toLong())
        val date = Date(timestamp.time)
        return dateFormat.format(date)
    }
}