package com.app.scanny.careercoop.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.scanny.R
import com.app.scanny.careercoop.models.CcUserModel
import com.app.scanny.databinding.RvUpdatesLayoutBinding
import com.google.android.material.chip.Chip

class CarrerCoopAdapter(var context : Context, var itemList : List<Pair<String,CcUserModel>>) : RecyclerView.Adapter<CarrerCoopAdapter.ViewHolder>()
{
    data class ViewHolder(var dataBinding : RvUpdatesLayoutBinding)  : RecyclerView.ViewHolder(dataBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.rv_updates_layout,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {

            holder.dataBinding.cgSkills.removeAllViews()
            holder.dataBinding.cgLocation.removeAllViews()
            for (item in itemList[position].second.detailsModel?.skills!!) {
                holder.dataBinding.cgSkills.addView(
                    (LayoutInflater.from(context).inflate(R.layout.siple_chip, holder.dataBinding.cgSkills,false) as Chip)
                        .apply {
                            text = item
                            isCheckable = false
                        }
                )
            }

            for (item in itemList[position].second.detailsModel?.location!!) {
                holder.dataBinding.cgLocation.addView(
                    (LayoutInflater.from(context).inflate(R.layout.siple_chip, holder.dataBinding.cgLocation,false) as Chip)
                        .apply {
                            text = item
                            isCheckable = false
                        }
                )
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }


        holder.dataBinding.root.setOnClickListener {
            (context as Activity).findNavController(R.id.nav_controller)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}