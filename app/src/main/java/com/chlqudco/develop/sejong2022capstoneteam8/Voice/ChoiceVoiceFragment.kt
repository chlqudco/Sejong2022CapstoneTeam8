package com.chlqudco.develop.sejong2022capstoneteam8.Voice

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.chlqudco.develop.sejong2022capstoneteam8.R
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.SETTING
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.VOICE
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.FragmentChoiceVoiceBinding

class ChoiceVoiceFragment : Fragment(R.layout.fragment_choice_voice) {

    private var binding: FragmentChoiceVoiceBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChoiceVoiceBinding = FragmentChoiceVoiceBinding.bind(view)
        binding = fragmentChoiceVoiceBinding

        initViews()
    }

    private fun initViews() {


        //private val sharedPreferences by lazy { getSharedPreferences("setting", Context.MODE_PRIVATE) }

        binding?.let { binding ->

            //목소리 정보 불러오기
            val sharedPreferences = context?.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
            var currentVoice = sharedPreferences?.getInt(VOICE, 2)

            //선택한 음성 버튼 클릭
            when (currentVoice) {
                0 -> {
                    binding.noVoiceRadioButton.isChecked = true
                }
                1 -> {
                    binding.girlCyberRadioButton.isChecked = true
                }
                2 -> {
                    binding.kimEggRadioButton.isChecked = true
                }
                3 -> {
                    binding.englishRadioButton.isChecked = true
                }
            }

            //목소리 바꿨을 경우 새로 저장
            binding.choiceVoiceRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                sharedPreferences?.edit {
                    when (checkedId) {
                        R.id.noVoiceRadioButton -> {
                            putInt(VOICE, 0)
                        }
                        R.id.girlCyberRadioButton -> {
                            putInt(VOICE, 1)
                        }
                        R.id.kimEggRadioButton -> {
                            putInt(VOICE, 2)
                        }
                        R.id.englishRadioButton -> {
                            putInt(VOICE, 3)
                        }
                    }
                }
            }
        }
    }
}