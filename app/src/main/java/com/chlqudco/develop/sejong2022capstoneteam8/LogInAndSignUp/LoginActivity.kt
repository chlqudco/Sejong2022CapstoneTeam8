package com.chlqudco.develop.sejong2022capstoneteam8.LogInAndSignUp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.chlqudco.develop.sejong2022capstoneteam8.MainActivity
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() = with(binding){
        LogIntmpButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //로그인 버튼 클릭 이벤트
        loginButton.setOnClickListener {
            //예외처리 1. 입력 안한 빈칸이 있는 경우
            if(LogInIdEditText.text.isNullOrEmpty() || LogInPasswordEditText.text.isNullOrEmpty()){
                Toast.makeText(this@LoginActivity,"입력을 제대로 해주삼",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //예외처리 2. 이메일 형식으로 입력 안한 경우
            if(LogInIdEditText.text.contains('@').not() || LogInIdEditText.text.contains('.').not() || LogInIdEditText.text.split('@').size <2){
                Toast.makeText(this@LoginActivity,"ID는 이메일 형식으로 입력해 주세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //로그인 기능
            Toast.makeText(this@LoginActivity,"로그인 기능 없음 병신아",Toast.LENGTH_SHORT).show()
        }

        //회원가입 클릭시 회원가입 페이지로 이동
        LogInSignUpButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}