package com.example.wheel_of_emotions.ui.table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wheel_of_emotions.databinding.FragmentTableWithClearBinding

class TableWithClearFragment : Fragment() {
    private var _binding: FragmentTableWithClearBinding? = null
    private lateinit var _viewModel: TableViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _viewModel = ViewModelProvider(requireActivity())[TableViewModel::class.java]
        _binding = FragmentTableWithClearBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _viewModel.filter.value = "none"
        _viewModel.tableUpdated.value = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}