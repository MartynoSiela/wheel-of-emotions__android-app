package com.example.wheel_of_emotions.ui.wheel

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wheel_of_emotions.databinding.FragmentWheelBinding

class WheelFragment : Fragment() {
    private var _binding: FragmentWheelBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val wheelViewModel =
            ViewModelProvider(this)[WheelViewModel::class.java]

        _binding = FragmentWheelBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val context = this.context

        val wheel = binding.imageViewWheel
        val emotions = binding.imageViewEmotions
        val button = binding.buttonAddEmotion
        button.isEnabled = false

        wheelViewModel.setTouchListener(context, wheel, emotions, button)
        wheelViewModel.setClickListener(context, wheel, button)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}