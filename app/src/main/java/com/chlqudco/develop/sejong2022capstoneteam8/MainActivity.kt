package com.chlqudco.develop.sejong2022capstoneteam8

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.chlqudco.develop.sejong2022capstoneteam8.Fitness.ChoiceFitnessFragment
import com.chlqudco.develop.sejong2022capstoneteam8.LogInAndSignUp.LoginActivity
import com.chlqudco.develop.sejong2022capstoneteam8.LogInAndSignUp.SignUpActivity
import com.chlqudco.develop.sejong2022capstoneteam8.Voice.ChoiceVoiceFragment
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.ActivityMainBinding
import com.chlqudco.develop.sejong2022capstoneteam8.mypage.MyPageFragment

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val ChoiceFitnessFragment by lazy { ChoiceFitnessFragment() }
    private val ChoiceVoiceFragment by lazy { ChoiceVoiceFragment() }
    private val MyPageFragment by lazy { MyPageFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initFragment()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun initFragment() = with(binding){
        replaceFragment(ChoiceFitnessFragment)
        MainBottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.fitness -> replaceFragment(ChoiceFitnessFragment)
                R.id.voiceSelect -> replaceFragment(ChoiceVoiceFragment)
                R.id.myPage -> replaceFragment(MyPageFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) = with(binding){
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.MainFragmentContainer, fragment)
                commit()
            }
    }
}