package com.example.wheel_of_emotions.ui.table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wheel_of_emotions.databinding.FragmentTableBinding

class TableFragment : Fragment() {
    private var _binding: FragmentTableBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tableViewModel =
            ViewModelProvider(this)[TableViewModel::class.java]

        _binding = FragmentTableBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val tableLayout = binding.tableLayout

        Table().initiateCellShape(this.requireContext())
        Table().writeTableRows(this.requireContext(), tableLayout)

        val buttonClear = binding.buttonClearEmotions
        tableViewModel.setClickListener(this.requireContext(), buttonClear, this)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}