package com.chlqudco.develop.sejong2022capstoneteam8.mypage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chlqudco.develop.sejong2022capstoneteam8.R
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.FragmentMyPageBinding

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
            binding.MyPageRecyclerView.adapter = adapter
            binding.MyPageRecyclerView.layoutManager = LinearLayoutManager(context)

            adapter.dataList = data
        }
    }

}