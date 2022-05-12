package com.chlqudco.develop.sejong2022capstoneteam8.Fitness

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.content.edit
import com.chlqudco.develop.sejong2022capstoneteam8.Mlkit.CameraXLivePreviewActivity
import com.chlqudco.develop.sejong2022capstoneteam8.R
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.FITNESS_CURRENT_SET
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.FITNESS_INTERVAL
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.FITNESS_SET
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.SETTING
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.ActivitySetEndBinding

class SetEndActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySetEndBinding.inflate(layoutInflater) }
    private val sharedPreferences by lazy { getSharedPreferences(SETTING, Context.MODE_PRIVATE) }

    //휴식시간 재보쟈
    private var currentCountDownTimer: CountDownTimer? = null

    //째깍 째깍
    private val mediaPlayer by lazy { MediaPlayer.create(this,R.raw.setend ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initSound()
        initViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun initSound() {
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }


    @SuppressLint("SetTextI18n")
    private fun initViews() = with(binding) {
        val currentSet = sharedPreferences.getInt(FITNESS_CURRENT_SET,-1)
        val totalSet = sharedPreferences.getInt(FITNESS_SET, -1)
        val timeInterval = sharedPreferences.getInt(FITNESS_INTERVAL, -1)

        SetEndRemainSetTextView.text = "남은 Set : ${totalSet - currentSet -1}, 총 Set : $totalSet"
        SetEndProgressbar.text = "$timeInterval 초"
        SetEndProgressbar.setProgress(timeInterval)

        startCountDown()

        sharedPreferences.edit {
            putInt(FITNESS_CURRENT_SET, currentSet+1)
            commit()
        }

    }

    //카운트다운 타이머를 만들고 반환
    private fun createCountDownTimer(initialSeconds: Long): CountDownTimer {
        return object : CountDownTimer(initialSeconds, 1000) {
            //1초마다 뭐할지
            override fun onTick(millisUntilFinished: Long) {
                //분과 초의 텍스트 변경
                updateRemainTime(millisUntilFinished)
                //시크바의 위치? 변경
                updateProgressBar(millisUntilFinished)
            }

            //다 끝나면 뭐할지
            override fun onFinish() {
                completeCountDown()
            }
        }
    }

    private fun startCountDown() = with(binding) {
        //카운트 다운 타이머 받아온뒤 시작
        currentCountDownTimer = createCountDownTimer((sharedPreferences.getInt(FITNESS_INTERVAL, -1)*1000).toLong())
        currentCountDownTimer?.start()

    }

    private fun updateRemainTime(millisUntilFinished: Long) = with(binding) {
        //밀리세컨드 타입을 적절히 변환 시켜서 대입
        val remainSeconds = millisUntilFinished / 1000

        SetEndProgressbar.text = "%d초".format(remainSeconds)
    }

    private fun updateProgressBar(millisUntilFinished: Long) = with(binding) {
        //시크바 재설정
        SetEndProgressbar.setProgress((millisUntilFinished / 1000).toInt())
    }

    private fun completeCountDown() {
        //0초로 초기화해주면 되지용
        updateRemainTime(0)
        updateProgressBar(0)

        stopCountDown()

        val intent = Intent(this, CameraXLivePreviewActivity::class.java)
        startActivity(intent)

        finish()
    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null

    }
}
