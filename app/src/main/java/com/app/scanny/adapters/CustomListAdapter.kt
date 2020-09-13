package com.app.scanny.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.scanny.R
import com.app.scanny.databinding.GridItemBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomListAdapter(var context : Context,var pathList: ArrayList<String>) : RecyclerView.Adapter<CustomListAdapter.ViewHolder>() {

   data class ViewHolder(var dataBindig : GridItemBinding) : RecyclerView.ViewHolder(dataBindig.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.grid_item,parent,false))
    }

    override fun getItemCount(): Int {
       return pathList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

                Glide.with(context)
                    .load(pathList[position])
                    .into(holder.dataBindig.ivItem)




    }

}