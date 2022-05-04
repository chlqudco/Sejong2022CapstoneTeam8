package com.chlqudco.develop.sejong2022capstoneteam8.mypage

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chlqudco.develop.sejong2022capstoneteam8.R
import com.chlqudco.develop.sejong2022capstoneteam8.Service.RetrofitService
import com.chlqudco.develop.sejong2022capstoneteam8.Service.SelectedDateHistoryEntity
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.TOKEN
import com.chlqudco.develop.sejong2022capstoneteam8.SharedPreferenceKey.Companion.USER_NAME
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.FragmentMyPageBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

@SuppressLint("SimpleDateFormat")
class MyPageFragment : Fragment(R.layout.fragment_my_page) {
    private var binding: FragmentMyPageBinding? = null
    private val adapter = MyPageRecyclerAdapter()
    private val sharedPreferences by lazy { context?.getSharedPreferences("setting", Context.MODE_PRIVATE) }
    private val retrofit by lazy { Retrofit.Builder().baseUrl("http://112.220.188.174/").addConverterFactory(GsonConverterFactory.create()).build() }
    private val token by lazy {sharedPreferences?.getString(TOKEN,"")}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMyPageBinding = FragmentMyPageBinding.bind(view)
        binding = fragmentMyPageBinding

        initViews()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun initViews(){

        //이름 적용하기
        initName()

        binding?.let { binding ->

            //리사이클러 뷰 설정
            binding.MyPageRecyclerView.adapter = adapter
            binding.MyPageRecyclerView.layoutManager = LinearLayoutManager(context)

            //들어오자마자 오늘 날짜와 오늘 날짜 기록 보여줘야 함
            initDateAndHistory()

            //원하는 날짜 클릭 이벤트 발생
            binding.MyPageCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                //기록 지우기
                adapter.dataList.clear()
                adapter.notifyDataSetChanged()

                //일단 글자 다시 생기고
                binding.MyPageIsHistoryTextView.isVisible = true

                //현재 날짜
                val choiceDate = "$year"+String.format("%02d",month+1)+String.format("%02d",dayOfMonth)

                //날짜 부터 바꿔주기
                val m = String.format("%02d",month+1)
                val doM = String.format("%02d",dayOfMonth)
                binding.MyPageDateTextView.text = "${year}년 ${m}월 ${doM}일 운동 기록"

                //서버에 기록 요청하기
                getHistory(token!!, choiceDate)
            }
        }
    }


    private fun initDateAndHistory() {
        //날짜 설정
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyy년 MM월 dd일")
        val sdf2 = SimpleDateFormat("yyyyMMdd")
        val getTime = sdf.format(date)
        binding?.MyPageDateTextView?.text = "$getTime 운동 기록"

        //기록 불러오기
        getHistory(token!!,sdf2.format(date))
    }

    //이름 적용하기
    @SuppressLint("SetTextI18n")
    private fun initName() {
        val userName = sharedPreferences?.getString(USER_NAME,"알수없음")
        binding?.MyPageNameTextView?.text = "${userName}님 반갑습니다"
    }

    private fun getHistory(token:String, date: String) {
        retrofit.create(RetrofitService::class.java)
            .also {
                it.getSelectedDateHistory(token = token, date = date)
                    .enqueue(object : Callback<SelectedDateHistoryEntity>{
                        override fun onResponse(call: Call<SelectedDateHistoryEntity>, response: Response<SelectedDateHistoryEntity>) {
                            response.body()?.let{ entity->
                                //실패한 경우 1
                                if(response.isSuccessful.not()){
                                    //Toast.makeText(context, "기록 불러오기에 실패했습니다.1", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    //실패한 경우 2
                                    if(entity.isSuccessed.not()){
                                        //Toast.makeText(context, "기록 불러오기에 실패했습니다.2", Toast.LENGTH_SHORT).show()
                                        Log.e("jang",date)
                                    }
                                    //ㄹㅇ 성공한 경우
                                    else{

                                        //Toast.makeText(context, "기록 불러오기에 성공했습니다.", Toast.LENGTH_SHORT).show()
                                        //토큰 갱신 돼쓰문 갈아치우기
                                        if(entity.isTokenExpired){
                                            sharedPreferences?.edit {
                                                putString(TOKEN,entity.token)
                                                commit()
                                            }
                                        }

                                        //기록이 하나라도 있는 경우
                                        if(entity.lungeCount!=0 || entity.pullUpCount!=0 || entity.pushUpCount!=0 || entity.squatCount!=0){
                                            //글자 쳐 지우기
                                            binding?.MyPageIsHistoryTextView?.isVisible = false

                                            //모든 운동 살펴보면서 기록 추가
                                            if(entity.lungeCount!=0){
                                                adapter.dataList.add(History(name = "런  지", set = entity.lungeSet, count = entity.lungeCount))
                                            }
                                            if(entity.pullUpCount!=0){
                                                adapter.dataList.add(History(name = "턱걸이", set = entity.pullUpSet, count = entity.pullUpCount))
                                            }
                                            if(entity.pushUpCount!=0){
                                                adapter.dataList.add(History(name = "푸시업", set = entity.pushUpSet, count = entity.pushUpCount))
                                            }
                                            if(entity.squatCount!=0){
                                                adapter.dataList.add(History(name = "스쿼트", set = entity.squatSet, count = entity.squatCount))
                                            }
                                            adapter.notifyDataSetChanged()
                                        }
                                        else{

                                        }
                                    }

                                }
                            }
                        }

                        override fun onFailure(call: Call<SelectedDateHistoryEntity>, t: Throwable) {
                            //Toast.makeText(context, "기록 불러오기에 실패했습니다.3", Toast.LENGTH_SHORT).show()
                            Log.e("jang",t.toString())
                        }

                    })
            }
    }

}