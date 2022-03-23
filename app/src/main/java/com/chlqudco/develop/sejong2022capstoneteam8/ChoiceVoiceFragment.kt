package com.chlqudco.develop.sejong2022capstoneteam8

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.FragmentChoiceVoiceBinding

class ChoiceVoiceFragment : Fragment(R.layout.fragment_choice_voice) {

    private var binding : FragmentChoiceVoiceBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChoiceVoiceBinding = FragmentChoiceVoiceBinding.bind(view)
        binding = fragmentChoiceVoiceBinding

        initViews()
    }

    private fun initViews(){
        binding?.let { binding ->
            binding.choiceVoiceRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId){
                    R.id.noVoiceRadioButton -> {
                        Toast.makeText(context,"무음을 선택하셨습니다",Toast.LENGTH_SHORT).show()
                    }
                    R.id.girl1RadioButton -> {
                        Toast.makeText(context,"여자1을 선택하셨습니다",Toast.LENGTH_SHORT).show()
                    }
                    R.id.girl2RadioButton -> {
                        Toast.makeText(context,"여자2를 선택하셨습니다",Toast.LENGTH_SHORT).show()
                    }
                    R.id.man1RadioButton -> {
                        Toast.makeText(context,"남자1을 선택하셨습니다",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}