package com.chlqudco.develop.sejong2022capstoneteam8.Fitness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chlqudco.develop.sejong2022capstoneteam8.R
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.ActivitySetEndBinding

class SetEndActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySetEndBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() = with(binding){
        SetEndNextSetButton.setOnClickListener {
            finish()
        }
    }
}