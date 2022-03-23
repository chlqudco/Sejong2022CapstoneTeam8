package com.chlqudco.develop.sejong2022capstoneteam8.Fitness

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.chlqudco.develop.sejong2022capstoneteam8.R
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.FragmentChoiceFitnessBinding

class ChoiceFitnessFragment : Fragment(R.layout.fragment_choice_fitness) {

    private var binding: FragmentChoiceFitnessBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChoiceFitnessBinding = FragmentChoiceFitnessBinding.bind(view)
        binding = fragmentChoiceFitnessBinding

        initViews()
    }

    private fun initViews() {
        binding?.let { binding ->
            binding.ChoiceFitnessSetNumberPicker.minValue = 1
            binding.ChoiceFitnessSetNumberPicker.maxValue = 10

            binding.ChoiceFitnessCountNumberPicker.minValue = 1
            binding.ChoiceFitnessCountNumberPicker.maxValue = 30
        }
    }
}