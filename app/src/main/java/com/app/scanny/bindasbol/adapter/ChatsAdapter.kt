package com.app.scanny.bindasbol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.scanny.R
import com.app.scanny.bindasbol.models.BolModel
import com.app.scanny.databinding.ChatsItemViewHolderBinding

class ChatsAdapter(var context: Context, var chatItems  : List<BolModel>)  : RecyclerView.Adapter<ChatsAdapter.ViewHolder>(){

    data class ViewHolder(var dataBinding : ChatsItemViewHolderBinding) : RecyclerView.ViewHolder(dataBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.chats_item_view_holder,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataBinding.tvBols.text = chatItems[position].bol
    }

    override fun getItemCount(): Int {
        return chatItems.size
    }

}