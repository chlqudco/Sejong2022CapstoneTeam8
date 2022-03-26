package com.chlqudco.develop.sejong2022capstoneteam8.mypage

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chlqudco.develop.sejong2022capstoneteam8.R
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.FragmentMyPageBinding
import java.util.*

class MyPageFragment : Fragment(R.layout.fragment_my_page) {
    private var binding: FragmentMyPageBinding? = null
    private val adapter = MyPageRecyclerAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMyPageBinding = FragmentMyPageBinding.bind(view)
        binding = fragmentMyPageBinding

        initViews()
    }

    private fun initViews(){

        //임시 데이터
        var data : MutableList<History> = mutableListOf()
        data.add(History("스쿼트",3,15))
        data.add(History("턱걸이",5,10))

        binding?.let { binding ->

            //리사이클러 뷰 설정
            binding.MyPageRecyclerView.adapter = adapter
            binding.MyPageRecyclerView.layoutManager = LinearLayoutManager(context)

            adapter.dataList = data

            //오늘 날짜 설정하기
            val now = System.currentTimeMillis()
            val date = Date(now)
            val sdf = SimpleDateFormat("yyyy년 MM월 dd일")
            val getTime = sdf.format(date)
            binding.MyPageDateTextView.text = "${getTime} 운동 기록"

            //원하는 날짜 클릭 이벤트 발생
            binding.MyPageCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->

                //날짜 부터 바꿔주기
                val m = String.format("%02d",month+1)
                binding.MyPageDateTextView.text = "${year}년 ${m}월 ${dayOfMonth}일 운동 기록"

                //서버에 기록 요청하기
            }
        }
    }

}