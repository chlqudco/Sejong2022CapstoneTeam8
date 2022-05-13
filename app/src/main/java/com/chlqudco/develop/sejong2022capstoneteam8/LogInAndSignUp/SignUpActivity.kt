package com.chlqudco.develop.sejong2022capstoneteam8.LogInAndSignUp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import com.chlqudco.develop.sejong2022capstoneteam8.Service.RetrofitService
import com.chlqudco.develop.sejong2022capstoneteam8.Service.SuccessEntity
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.SIGN_UP_ID
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.SIGN_UP_PASSWORD
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }
    private val sharedPreferences by lazy { this.getSharedPreferences("setting", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() = with(binding) {

        //레트로핏 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("http://112.220.188.174/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //회원가입 버튼 클릭 이벤트
        SignUpSignUpButton.setOnClickListener {
            //예외처리 1. 안 채운 곳이 있을 경우
            if (SignUpIdEditText.text.isNullOrEmpty() || SignUpPasswordEditText.text.isNullOrEmpty() || SignUpPasswordCheckEditText.text.isNullOrEmpty() || SignUpNameEditText.text.isNullOrEmpty()) {
                Toast.makeText(this@SignUpActivity, "입력을 제대로 하삼", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //예외처리 2. 비밀번호와 비밀번호 확인이 다른 경우
            if (SignUpPasswordEditText.text.toString() != SignUpPasswordCheckEditText.text.toString()) {
                Toast.makeText(this@SignUpActivity, "비밀번호 다르게 입력함", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //예외처리 3. 이메일 형식으로 입력 안한 경우
            if (SignUpIdEditText.text.contains('@').not() || SignUpIdEditText.text.contains('.')
                    .not() || SignUpIdEditText.text.split('@').size < 2
            ) {
                Toast.makeText(this@SignUpActivity, "ID는 이메일 형식으로 입력해 주세요", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            //회원 가입 시작
            val userId = SignUpIdEditText.text.toString()
            val password = SignUpPasswordEditText.text.toString()
            val username = SignUpNameEditText.text.toString()

            retrofit.create(RetrofitService::class.java)
                .also {
                    it.register(id = userId, pw = password, name = username)
                        .enqueue(object : Callback<SuccessEntity> {
                            override fun onResponse(call: Call<SuccessEntity>, response: Response<SuccessEntity>) {
                                response.body()?.let {

                                    //실패한 경우 1
                                    if (response.isSuccessful.not()) {
                                        Toast.makeText(this@SignUpActivity, "회원가입에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                                    } else {
                                        //성공 실패 여부 판단
                                        val isSuccess = it.isSuccessed

                                        //실패한 경우 2
                                        if (isSuccess.not()) {
                                            Toast.makeText(this@SignUpActivity, "회원가입에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this@SignUpActivity, "회원가입에 성공 했습니다.", Toast.LENGTH_SHORT).show()

                                            //정보 저장
                                            sharedPreferences.edit {
                                                putString(SIGN_UP_ID,userId)
                                                putString(SIGN_UP_PASSWORD,password)
                                                commit()
                                            }

                                            //로그인 화면으로 컴백
                                            finish()
                                        }
                                    }
                                }
                            }
                            // 실패한 경우 2
                            override fun onFailure(call: Call<SuccessEntity>, t: Throwable) {
                                Toast.makeText(this@SignUpActivity, "회원가입에 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
        }

    }
}