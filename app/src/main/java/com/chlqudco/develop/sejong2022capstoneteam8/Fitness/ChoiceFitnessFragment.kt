package com.chlqudco.develop.sejong2022capstoneteam8.Fitness

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
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

            binding.ChoiceFitnessTimeInterValNumberPicker.minValue = 10
            binding.ChoiceFitnessTimeInterValNumberPicker.maxValue = 30

            binding.ChoiceFitnessStartButton.setOnClickListener {

                //세팅 저장
                val sharedPreferences = context?.getSharedPreferences("setting",Context.MODE_PRIVATE)
                sharedPreferences?.edit {
                    putInt("set",binding.ChoiceFitnessSetNumberPicker.value)
                    putInt("remainSet",binding.ChoiceFitnessSetNumberPicker.value)
                    putInt("interval",binding.ChoiceFitnessTimeInterValNumberPicker.value)
                    putInt("count",binding.ChoiceFitnessCountNumberPicker.value)
                    //무슨 운동 선택했는지 저장
                    commit()
                }

                //액티비티 시작
                val intent = Intent(context,DoFitnessActivity::class.java)
                startActivity(intent)
            }

        }


    }
}