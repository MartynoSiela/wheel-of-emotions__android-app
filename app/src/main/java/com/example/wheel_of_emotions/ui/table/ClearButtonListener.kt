package com.example.wheel_of_emotions.ui.table

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class ClearButtonListener(
    private val context: Context,
    private val button: Button,
    private val fragment: Fragment
    ) : View.OnClickListener {

    override fun onClick(p0: View?) {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Confirmation")
            .setMessage("All previously stored emotions will be cleared. Are you sure you want to do this?")
            .setPositiveButton("Yes") { _, _ ->
                Table().clearTable(context)
                fragment.activity?.recreate()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
}