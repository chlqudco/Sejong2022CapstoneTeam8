package com.chlqudco.develop.sejong2022capstoneteam8.mypage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chlqudco.develop.sejong2022capstoneteam8.databinding.ItemMyPageHistoryBinding

class MyPageRecyclerAdapter: RecyclerView.Adapter<MyPageRecyclerAdapter.Holder>() {
    var dataList = mutableListOf<History>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemMyPageHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(private val binding: ItemMyPageHistoryBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(history : History){
            binding.ItemHistoryTextView.text = " · ${history.name}  set : ${history.set.toString()} , count : ${history.count.toString()}"
        }
    }
}