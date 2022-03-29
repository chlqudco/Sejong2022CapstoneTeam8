package com.chlqudco.develop.sejong2022capstoneteam8.LogInAndSignUp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() = with(binding){

        //회원가입 버튼 클릭 이벤트
        SignUpSignUpButton.setOnClickListener {
            //예외처리 1. 안 채운 곳이 있을 경우
            if(SignUpIdEditText.text.isNullOrEmpty() || SignUpPasswordEditText.text.isNullOrEmpty() || SignUpPasswordCheckEditText.text.isNullOrEmpty() || SignUpNameEditText.text.isNullOrEmpty()){
                Toast.makeText(this@SignUpActivity,"입력을 제대로 하삼",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //예외처리 2. 비밀번호와 비밀번호 확인이 다른 경우
            if(SignUpPasswordEditText.text.toString() != SignUpPasswordCheckEditText.text.toString()){
                Toast.makeText(this@SignUpActivity,"비밀번호 다르게 입력함",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //예외처리 3. 이메일 형식으로 입력 안한 경우
            if(SignUpIdEditText.text.contains('@').not() || SignUpIdEditText.text.contains('.').not() || SignUpIdEditText.text.split('@').size <2){
                Toast.makeText(this@SignUpActivity,"ID는 이메일 형식으로 입력해 주세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //회원 가입 신청
            Toast.makeText(this@SignUpActivity,"회원가입 기능 없음 병신아",Toast.LENGTH_SHORT).show()
       }

        SignUpTmpButton.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}