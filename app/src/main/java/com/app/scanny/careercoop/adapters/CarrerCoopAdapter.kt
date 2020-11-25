package com.app.scanny.careercoop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.scanny.R
import com.app.scanny.careercoop.models.CcUserModel
import com.app.scanny.databinding.RvUpdatesLayoutBinding

class CarrerCoopAdapter(var context : Context, var itemList : List<CcUserModel>) : RecyclerView.Adapter<CarrerCoopAdapter.ViewHolder>()
{
    data class ViewHolder(var dataBinding : RvUpdatesLayoutBinding)  : RecyclerView.ViewHolder(dataBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.rv_updates_layout,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataBinding.root.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}