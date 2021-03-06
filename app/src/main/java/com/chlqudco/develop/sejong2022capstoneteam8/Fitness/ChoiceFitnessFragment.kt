package com.chlqudco.develop.sejong2022capstoneteam8.Fitness

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.chlqudco.develop.sejong2022capstoneteam8.Mlkit.CameraXLivePreviewActivity
import com.chlqudco.develop.sejong2022capstoneteam8.R
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.FITNESS_CHOICE
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.FITNESS_COUNT
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.FITNESS_CURRENT_SET
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.FITNESS_INTERVAL
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.FITNESS_SET
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.LUNGE
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.LUNGE_DOWN
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.PULLUP
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.PULLUP_DOWN
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.PUSHUP
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.PUSHUP_DOWN
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.SETTING
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.SQUAT
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.SQUAT_DOWN
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

    //?????? ?????? value ?????????
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

            //?????? ?????? ?????? ?????? ?????????
            binding.ChoiceFitnessStartButton.setOnClickListener {

                //???????????? 1. ?????? ????????? ?????? ?????? ?????? ????????? ????????? ??????
                if (binding.choiceVoiceRadioGroup.checkedRadioButtonId == -1) {
                    context?.let { context -> showToastMessage(context, "????????? ????????? ?????????") }
                    return@setOnClickListener
                }

                //?????? ??????
                if (allPermissionsGranted()) {
                    startFitness()
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
                }
            }

        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startFitness()
            }else{
                Toast.makeText(context,"???????????? ???????????? ????????? ??? ????????????", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startFitness() {
        //????????? ????????? ??????
        saveRecordToSharedPreference()

        val intent = Intent(requireContext(), CameraXLivePreviewActivity::class.java)
        startActivity(intent)
    }

    //????????? ????????? ?????????
    private fun showToastMessage(context: Context, message: String) {
        if (sToast == null) {
            sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        } else {
            sToast!!.setText(message)
        }
        sToast?.show()
    }

    //?????? ??????
    private fun saveRecordToSharedPreference() {
        val sharedPreferences = context?.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
        sharedPreferences?.edit {
            binding?.let { binding ->
                putInt(FITNESS_SET, binding.ChoiceFitnessSetNumberPicker.value)
                putInt(FITNESS_CURRENT_SET,0)
                putInt(FITNESS_INTERVAL, binding.ChoiceFitnessTimeInterValNumberPicker.value)
                putInt(FITNESS_COUNT, binding.ChoiceFitnessCountNumberPicker.value)


                //?????? ?????? ??????????????? ??????
                when (binding.choiceVoiceRadioGroup.checkedRadioButtonId) {
                    R.id.fitness1 -> {
                        putString(FITNESS_CHOICE, PUSHUP_DOWN)
                    }
                    R.id.fitness2 -> {
                        putString(FITNESS_CHOICE, PULLUP_DOWN)
                    }
                    R.id.fitness3 -> {
                        putString(FITNESS_CHOICE, SQUAT_DOWN)
                    }
                    R.id.fitness4 -> {
                        putString(FITNESS_CHOICE, LUNGE_DOWN)
                    }
                    else -> {
                        putString(FITNESS_CHOICE,"null")
                    }
                }
            }
            commit()
        }
    }

    companion object{
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}