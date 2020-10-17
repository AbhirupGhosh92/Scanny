package com.app.scanny.bindasbol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.scanny.R
import com.app.scanny.bindasbol.models.BolModel
import com.app.scanny.databinding.ChatsItemViewHolderBinding
import com.app.scanny.repository.Repository
import com.google.android.gms.ads.AdRequest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatsAdapter(var context: Context, var chatItems  : List<Pair<String,BolModel>>,
                   var likeAction : (doc : Pair<String,BolModel>,likeState: Boolean) -> Unit,
                    var commentAction : (doc : Pair<String,BolModel>) -> Unit
                   )  : RecyclerView.Adapter<ChatsAdapter.ViewHolder>(){

    data class ViewHolder(var dataBinding : ChatsItemViewHolderBinding,var selected : Boolean = false) : RecyclerView.ViewHolder(dataBinding.root)
    private var dateFormat = SimpleDateFormat("dd/MM/yy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.chats_item_view_holder,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataBinding.tvBols.text = chatItems[position].second.bol
        holder.dataBinding.tvNickName.text  = chatItems[position].second.nickName
        holder.dataBinding.tvDate.text = dateFormat.format(chatItems[position].second.dateCreated?.toDate()!!)
        holder.dataBinding.tvLikes.text =  chatItems[position].second.likes.toString()
        holder.dataBinding.tvComments.text = chatItems[position].second.comments.toString()
        if(position%5 == 0 && position != 0)
        {
            var  adRequest =  AdRequest.Builder().build()
            holder.dataBinding.adView.loadAd(adRequest)
            holder.dataBinding.adView.visibility =  View.VISIBLE
        }
        else
        {
            holder.dataBinding.adView.visibility =  View.GONE
        }

        if(chatItems[position].second.likeList?.contains(Repository.mAuth.currentUser?.uid.toString())!!)
        {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                holder.dataBinding.imgLikeImage.setImageDrawable(ResourcesCompat.getDrawable(context.resources,R.drawable.ic_baseline_thumb_up_alt,null))
            }
        }
        else
        {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                holder.dataBinding.imgLikeImage.setImageDrawable(ResourcesCompat.getDrawable(context.resources,R.drawable.ic_baseline_thumb_up_alt_not_selected,null))
            }
        }

        holder.dataBinding.imgLikeImage.setOnClickListener {
            likeAction.invoke(chatItems[position],likeState(position))
        }

        holder.dataBinding.imgCommentImage.setOnClickListener {
            commentAction.invoke(chatItems[position])
        }
    }

    private fun likeState(position: Int) : Boolean
    {
        return chatItems[position].second.likeList?.contains(Repository.mAuth.currentUser?.uid.toString())?.not()!!
    }

    override fun getItemCount(): Int {

        return chatItems.size
    }

}