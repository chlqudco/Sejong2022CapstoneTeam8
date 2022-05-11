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

                //권한 처리
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
                Toast.makeText(context,"동의해야 카메라를 실행할 수 있습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startFitness() {
        //선택한 기록들 저장
        saveRecordToSharedPreference()

        val intent = Intent(requireContext(), CameraXLivePreviewActivity::class.java)
        startActivity(intent)
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
                putInt(FITNESS_SET, binding.ChoiceFitnessSetNumberPicker.value)
                putInt(FITNESS_CURRENT_SET,0)
                putInt(FITNESS_INTERVAL, binding.ChoiceFitnessTimeInterValNumberPicker.value)
                putInt(FITNESS_COUNT, binding.ChoiceFitnessCountNumberPicker.value)


                //무슨 운동 선택했는지 저장
                when (binding.choiceVoiceRadioGroup.checkedRadioButtonId) {
                    R.id.fitness1 -> {
                        putString(FITNESS_CHOICE,"pushups_down")
                    }
                    R.id.fitness2 -> {
                        putString(FITNESS_CHOICE,"턱걸이")
                    }
                    R.id.fitness3 -> {
                        putString(FITNESS_CHOICE,"squats_down")
                    }
                    R.id.fitness4 -> {
                        putString(FITNESS_CHOICE,"런지")
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