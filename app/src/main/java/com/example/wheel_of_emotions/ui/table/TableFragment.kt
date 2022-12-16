package com.example.wheel_of_emotions.ui.table

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wheel_of_emotions.Feeling
import com.example.wheel_of_emotions.R
import com.example.wheel_of_emotions.databinding.FragmentTableBinding

class TableFragment : Fragment() {
    private var _binding: FragmentTableBinding? = null
    private lateinit var _viewModel: TableViewModel
    private lateinit var _table: TableLayout

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _viewModel = ViewModelProvider(requireActivity())[TableViewModel::class.java]
        _binding = FragmentTableBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _table = binding.tableLayout

        val emotions = _viewModel.getEmotions(requireContext())
        _viewModel.initiateCellShape(requireContext())
        writeTableRows(emotions)

        _viewModel.tableUpdated.observe(viewLifecycleOwner) {
            if (_viewModel.tableUpdated.value == true) {
                val uiHandler = Handler(Looper.getMainLooper())
                uiHandler.post { redrawTable() }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun redrawTable() {
        val manager = parentFragmentManager
        manager.beginTransaction().detach(this).commitNowAllowingStateLoss()
        manager.beginTransaction().attach(this).commitAllowingStateLoss()
        _viewModel.tableUpdated.value = false
    }

    private fun writeTableRows(emotions: MutableList<ArrayList<String?>>) {
        for (emotion in emotions) {
            val tableRow: View = LayoutInflater.from(context).inflate(R.layout.table_row, null, false)
            val emotionIdCell = tableRow.findViewById<TextView>(R.id.column_id)
            val emotionNameCell = tableRow.findViewById<TextView>(R.id.column_emotion)
            val emotionDateCell = tableRow.findViewById<TextView>(R.id.column_date)

            val emotionEntryId = emotion[0]
            val emotionId = emotion[1]!!.toInt()
            val emotionDate = emotion[2]

            emotionIdCell.text = emotionEntryId
            emotionNameCell.text = Feeling().getFeelingById(emotionId)?.nameLt
            emotionDateCell.text = _viewModel.formatDate(emotionDate!!)

            val emotionColor = Feeling().getFeelingById(emotionId)?.colorARGB
            val emotionColorStateList = ColorStateList.valueOf(emotionColor!!)

            ViewCompat.setBackground(emotionIdCell, _viewModel.cellShape)
            ViewCompat.setBackground(emotionDateCell, _viewModel.cellShape)
            ViewCompat.setBackground(emotionNameCell, _viewModel.mutateCellShape(emotionColorStateList))
            _table.addView(tableRow)
        }
    }
}