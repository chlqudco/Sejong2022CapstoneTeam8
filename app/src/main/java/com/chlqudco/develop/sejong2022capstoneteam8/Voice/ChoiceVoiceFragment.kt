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
            var currentVoice = sharedPreferences?.getInt(VOICE, -1)

            //예외처리 1. 목소리 한번도 안골랐던 경우 -> 여성1로 박제
            if (currentVoice == -1) {
                sharedPreferences?.edit {
                    putInt(VOICE, R.id.girl1RadioButton)
                    commit()
                }
                currentVoice = sharedPreferences?.getInt(VOICE, -1)
            }

            //선택한 음성 버튼 클릭
            when (currentVoice) {
                R.id.noVoiceRadioButton -> {
                    binding.noVoiceRadioButton.isChecked = true
                }
                R.id.girl1RadioButton -> {
                    binding.girl1RadioButton.isChecked = true
                }
                R.id.girl2RadioButton -> {
                    binding.girl2RadioButton.isChecked = true
                }
                R.id.man1RadioButton -> {
                    binding.man1RadioButton.isChecked = true
                }
            }

            //목소리 바꿨을 경우 새로 저장
            binding.choiceVoiceRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                sharedPreferences?.edit {
                    when (checkedId) {
                        R.id.noVoiceRadioButton -> {
                            putInt(VOICE, R.id.noVoiceRadioButton)
                        }
                        R.id.girl1RadioButton -> {
                            putInt(VOICE, R.id.girl1RadioButton)
                        }
                        R.id.girl2RadioButton -> {
                            putInt(VOICE, R.id.girl2RadioButton)
                        }
                        R.id.man1RadioButton -> {
                            putInt(VOICE, R.id.man1RadioButton)
                        }
                    }
                }

            }
        }
    }
}