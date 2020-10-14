package com.app.scanny.bindasbol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.scanny.R
import com.app.scanny.bindasbol.models.BolModel
import com.app.scanny.databinding.ChatsItemViewHolderBinding
import com.google.android.gms.ads.AdRequest
import java.text.SimpleDateFormat
import java.util.*

class ChatsAdapter(var context: Context, var chatItems  : List<BolModel>)  : RecyclerView.Adapter<ChatsAdapter.ViewHolder>(){

    data class ViewHolder(var dataBinding : ChatsItemViewHolderBinding) : RecyclerView.ViewHolder(dataBinding.root)
    private var dateFormat = SimpleDateFormat("dd/MM/yy")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.chats_item_view_holder,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataBinding.tvBols.text = chatItems[position].bol
        holder.dataBinding.tvNickName.text  = chatItems[position].nickName
        holder.dataBinding.tvDate.text = dateFormat.format(chatItems[position].dateCreated?.toDate()!!)
        holder.dataBinding.tvLikes.text =  chatItems[position].likes.toString()
        holder.dataBinding.tvComments.text = chatItems[position].comments.toString()
        if(position%5 == 0)
        {
            var  adRequest =  AdRequest.Builder().build()
            holder.dataBinding.adView.loadAd(adRequest)
            holder.dataBinding.adView.visibility =  View.VISIBLE
        }
        else
        {
            holder.dataBinding.adView.visibility =  View.GONE
        }
    }

    override fun getItemCount(): Int {
        return chatItems.size
    }

}