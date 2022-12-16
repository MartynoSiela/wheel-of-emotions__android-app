package com.example.wheel_of_emotions.ui.table

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wheel_of_emotions.databinding.FragmentButtonClearTableBinding

class ButtonClearTableFragment : Fragment() {
    private var _binding: FragmentButtonClearTableBinding? = null
    private lateinit var _viewModel: TableViewModel
    private lateinit var _buttonClearTable: Button

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _viewModel = ViewModelProvider(requireActivity())[TableViewModel::class.java]
        _binding = FragmentButtonClearTableBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _buttonClearTable = binding.buttonClearTable
        _buttonClearTable.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Confirmation")
                .setMessage("All previously stored emotions will be cleared. Are you sure you want to do this?")
                .setPositiveButton("Yes") { _, _ ->
                    _viewModel.clearTable(requireContext())
                    _viewModel.tableUpdated.value = true
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}