package com.chlqudco.develop.sejong2022capstoneteam8.Fitness

import android.content.Context
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.edit
import androidx.core.view.isVisible
import com.chlqudco.develop.sejong2022capstoneteam8.Service.RetrofitService
import com.chlqudco.develop.sejong2022capstoneteam8.Service.SendHistoryEntity
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.FITNESS_CHOICE
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.FITNESS_COUNT
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
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.ActivityAllFitnessEndBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class AllFitnessEndActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAllFitnessEndBinding.inflate(layoutInflater) }
    private val sharedPreferences by lazy { this.getSharedPreferences(SETTING, Context.MODE_PRIVATE) }
    private val retrofit by lazy { Retrofit.Builder().baseUrl("http://112.220.188.174/").addConverterFactory(GsonConverterFactory.create()).build() }
    private val token by lazy {sharedPreferences?.getString(SharedPreferenceKey.TOKEN,"")}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sendRecord()

    }

    //기록 보내기
    private fun sendRecord() {

        //날짜 불러오기
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf2 = SimpleDateFormat("yyyyMMdd")
        val getTime = sdf2.format(date)

        //운동 종류 불러 오기
        val fitnesstype = sharedPreferences.getString(FITNESS_CHOICE,"")

        var push_up_set:Int = 0
        var push_up_count:Int = 0
        var pull_up_set:Int = 0
        var pull_up_count:Int = 0
        var squat_set:Int = 0
        var squat_count:Int = 0
        var lunge_set:Int = 0
        var lunge_count:Int = 0

        when(fitnesstype){
            PUSHUP_DOWN ->{
                push_up_set = sharedPreferences.getInt(FITNESS_SET,0)
                push_up_count = sharedPreferences.getInt(FITNESS_COUNT,0)
            }
            PULLUP_DOWN ->{
                pull_up_set = sharedPreferences.getInt(FITNESS_SET,0)
                pull_up_count = sharedPreferences.getInt(FITNESS_COUNT,0)
            }
            SQUAT_DOWN->{
                squat_set = sharedPreferences.getInt(FITNESS_SET,0)
                squat_count = sharedPreferences.getInt(FITNESS_COUNT,0)
            }
            LUNGE_DOWN->{
                lunge_set = sharedPreferences.getInt(FITNESS_SET,0)
                lunge_count = sharedPreferences.getInt(FITNESS_COUNT,0)
            }
        }

        //전송
        retrofit.create(RetrofitService::class.java)
            .also {
                it.sendHistory(
                    token = token!!,
                    date = getTime,
                    pushUpSet = push_up_set,
                    pushUpCount = push_up_count,
                    pullUpSet = pull_up_set,
                    pullUpCount = pull_up_count,
                    squatSet = squat_set,
                    squatCount = squat_count,
                    lungeSet = lunge_set,
                    lungeCount = lunge_count
                ).enqueue(object : Callback<SendHistoryEntity>{
                    override fun onResponse(call: Call<SendHistoryEntity>, response: Response<SendHistoryEntity>) {
                        Log.e("jang","일단 성공")
                        response.body()?.let { entity->
                            //실패 2
                            if (response.isSuccessful.not()){
                                Log.e("jang","실패2")
                            }else{
                                //실패 3
                                if (entity.isSuccessed.not()){
                                    Log.e("jang","실패3")
                                }else{
                                    //토큰 갱신 돼쓰문 갈아치우기
                                    if(entity.isTokenExpired){
                                        sharedPreferences?.edit {
                                            putString(SharedPreferenceKey.TOKEN,entity.token)
                                            commit()
                                        }
                                    }
                                    binding.AllFitnessEndProgressBar.isVisible = false
                                    binding.AllFitnessEndMainTextView.text ="기록이 전송되었습니다."
                                    Thread{
                                        Thread.sleep(2000)
                                        finish()
                                    }.start()
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<SendHistoryEntity>, t: Throwable) {
                        Log.e("jang","실패3")
                    }

                })
            }
    }

}