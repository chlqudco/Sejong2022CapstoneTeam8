package com.chlqudco.develop.sejong2022capstoneteam8.Fitness

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.chlqudco.develop.sejong2022capstoneteam8.R
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.FragmentChoiceFitnessBinding

class ChoiceFitnessFragment : Fragment(R.layout.fragment_choice_fitness) {

    private var binding: FragmentChoiceFitnessBinding? = null
    private var sToast: Toast? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChoiceFitnessBinding = FragmentChoiceFitnessBinding.bind(view)
        binding = fragmentChoiceFitnessBinding

        initNumberPickers()
        initViews()
    }

    //넘버 피커 value 초기화
    private fun initNumberPickers() {
        binding?.let { binding ->
            binding.ChoiceFitnessSetNumberPicker.minValue = 1
            binding.ChoiceFitnessSetNumberPicker.maxValue = 10

            binding.ChoiceFitnessCountNumberPicker.minValue = 1
            binding.ChoiceFitnessCountNumberPicker.maxValue = 30

            binding.ChoiceFitnessTimeInterValNumberPicker.minValue = 10
            binding.ChoiceFitnessTimeInterValNumberPicker.maxValue = 30
        }
    }

    private fun initViews() {
        binding?.let { binding ->

            //운동 시작 버튼 클릭 이벤트
            binding.ChoiceFitnessStartButton.setOnClickListener {

                //예외처리 1. 아무 운동도 체크 안한 경우 토스트 메세지 출력
                if (binding.choiceVoiceRadioGroup.checkedRadioButtonId == -1) {
                    context?.let { context -> showToastMessage(context, "운동을 선택해 주세요") }
                    return@setOnClickListener
                }

                //선택한 기록들 저장
                saveRecordToSharedPreference()

                //액티비티 시작
                val intent = Intent(context, DoFitnessActivity::class.java)
                startActivity(intent)
            }

        }
    }

    //토스트 메세지 띄우기
    private fun showToastMessage(context: Context, message: String) {
        if (sToast == null) {
            sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        } else {
            sToast!!.setText(message)
        }
        sToast?.show()
    }

    //세팅 저장
    private fun saveRecordToSharedPreference() {
        val sharedPreferences = context?.getSharedPreferences("setting", Context.MODE_PRIVATE)
        sharedPreferences?.edit {
            binding?.let { binding ->
                putInt("set", binding.ChoiceFitnessSetNumberPicker.value)
                putInt("remainSet", binding.ChoiceFitnessSetNumberPicker.value)
                putInt("interval", binding.ChoiceFitnessTimeInterValNumberPicker.value)
                putInt("count", binding.ChoiceFitnessCountNumberPicker.value)


                //무슨 운동 선택했는지 저장
                when (binding.choiceVoiceRadioGroup.checkedRadioButtonId) {
                    R.id.fitness1 -> {
                        putString("fitnessChoice","팔굽혀펴기")
                    }
                    R.id.fitness2 -> {
                        putString("fitnessChoice","턱걸이")
                    }
                    R.id.fitness3 -> {
                        putString("fitnessChoice","스쿼트")
                    }
                    R.id.fitness4 -> {
                        putString("fitnessChoice","런지")
                    }
                    else -> {
                        putString("fitnessChoice","null")
                    }
                }
            }
            commit()
        }
    }
}