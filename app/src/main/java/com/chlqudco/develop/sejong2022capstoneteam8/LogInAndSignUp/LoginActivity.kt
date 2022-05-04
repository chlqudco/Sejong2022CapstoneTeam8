package com.chlqudco.develop.sejong2022capstoneteam8.LogInAndSignUp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.edit
import com.chlqudco.develop.sejong2022capstoneteam8.MainActivity
import com.chlqudco.develop.sejong2022capstoneteam8.Service.LoginUserEntity
import com.chlqudco.develop.sejong2022capstoneteam8.Service.RetrofitService
import com.chlqudco.develop.sejong2022capstoneteam8.Service.SuccessEntity
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() = with(binding) {
        LogInSignUpButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        LogIntmpButton.setOnClickListener {
            finish()
        }

        //로그인 버튼 클릭 이벤트
        loginButton.setOnClickListener {
            //예외처리 1. 입력 안한 빈칸이 있는 경우
            if (LogInIdEditText.text.isNullOrEmpty() || LogInPasswordEditText.text.isNullOrEmpty()) {
                Toast.makeText(this@LoginActivity, "입력을 제대로 해주삼", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //예외처리 2. 이메일 형식으로 입력 안한 경우
            if (LogInIdEditText.text.contains('@').not() || LogInIdEditText.text.contains('.')
                    .not() || LogInIdEditText.text.split('@').size < 2
            ) {
                Toast.makeText(this@LoginActivity, "ID는 이메일 형식으로 입력해 주세요", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val userId = LogInIdEditText.text.toString()
            val password = LogInPasswordEditText.text.toString()

            //로그인 기능
            //레트로핏 객체 생성
            val retrofit = Retrofit.Builder()
                .baseUrl("http://112.220.188.174/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofit.create(RetrofitService::class.java)
                .also {
                    it.login(id = userId, pw = password)
                        .enqueue(object : Callback<LoginUserEntity> {
                            override fun onResponse(call: Call<LoginUserEntity>, response: Response<LoginUserEntity>) {
                                response.body()?.let {
                                    //실패한 경우 1
                                    if (response.isSuccessful.not()) {
                                        Toast.makeText(this@LoginActivity, "로그인에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                                    } else {
                                        //성공 실패 여부 판단

                                        //실패한 경우 2
                                        if (it.isSuccessed.not()) {
                                            Toast.makeText(this@LoginActivity, "로그인에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                                        }
                                        //성공
                                        else {
                                            Toast.makeText(this@LoginActivity, "로그인에 성공 했습니다.${it.token}", Toast.LENGTH_SHORT).show()
                                            //셰어드 프리퍼런스에 정보 저장
                                            val sharedPreferences = this@LoginActivity.getSharedPreferences("setting", Context.MODE_PRIVATE)
                                            sharedPreferences?.edit {
                                                putString(SharedPreferenceKey.TOKEN, it.token)
                                                putString(SharedPreferenceKey.USER_NAME, it.name)
                                                commit()
                                            }
                                            finish()
                                        }
                                    }
                                }
                            }
                            // 실패한 경우 2
                            override fun onFailure(call: Call<LoginUserEntity>, t: Throwable) {
                                Toast.makeText(this@LoginActivity, "로그인에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
        }

    }
}