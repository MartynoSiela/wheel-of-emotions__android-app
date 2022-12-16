package com.example.wheel_of_emotions.ui.table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wheel_of_emotions.databinding.FragmentWeekViewButtonsBinding

class ButtonsWeekViewFragment : Fragment() {
    private var _binding: FragmentWeekViewButtonsBinding? = null
    private lateinit var _viewModel: TableViewModel
    private lateinit var _buttonBack: Button
    private lateinit var _buttonWeek: Button
    private lateinit var _buttonForward: Button

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _viewModel = ViewModelProvider(requireActivity())[TableViewModel::class.java]
        _binding = FragmentWeekViewButtonsBinding.inflate(inflater, container, false)

        _buttonBack = binding.buttonBack
        _buttonWeek = binding.buttonWeek
        _buttonForward = binding.buttonForward

        _buttonWeek.text = "${_viewModel.weekStartString} - ${_viewModel.weekEndString}"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _buttonForward.setOnClickListener {
            _viewModel.dateToFilterBy = _viewModel.getNextWeekDay(_viewModel.dateToFilterBy)
            _viewModel.setDateValues(_viewModel.dateToFilterBy)
            _buttonWeek.text = "${_viewModel.weekStartString} - ${_viewModel.weekEndString}"
            _viewModel.tableUpdated.value = true
        }

        _buttonBack.setOnClickListener {
            _viewModel.dateToFilterBy = _viewModel.getPreviousWeekDay(_viewModel.dateToFilterBy)
            _viewModel.setDateValues(_viewModel.dateToFilterBy)
            _buttonWeek.text = "${_viewModel.weekStartString} - ${_viewModel.weekEndString}"
            _viewModel.tableUpdated.value = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}