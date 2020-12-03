package com.app.scanny.careercoop.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.scanny.R
import com.app.scanny.careercoop.models.CcUserModel
import com.app.scanny.databinding.RvUpdatesLayoutBinding
import com.google.android.material.chip.Chip


class CarrerCoopAdapter(
    var context: Context,
    var itemList: List<Pair<String, CcUserModel>>,
    var search: Boolean = false,var snippet : ((item : CcUserModel) -> Unit)? = null
) : RecyclerView.Adapter<CarrerCoopAdapter.ViewHolder>()
{
    data class ViewHolder(var dataBinding: RvUpdatesLayoutBinding)  : RecyclerView.ViewHolder(
        dataBinding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rv_updates_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {


            if(search.not())
            {
                holder.dataBinding.tvName.visibility = View.GONE
                holder.dataBinding.ivCall.visibility = View.GONE
                holder.dataBinding.ivEmail.visibility = View.GONE
            }
            else
            {
                holder.dataBinding.tvName.visibility = View.VISIBLE
                holder.dataBinding.ivCall.visibility = View.VISIBLE
                holder.dataBinding.ivEmail.visibility = View.VISIBLE
                holder.dataBinding.tvName.text = "Posted by ${itemList[position].second.name}"

                holder.dataBinding.ivCall.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${itemList[position].second.phone}")
                    context.startActivity(intent)
                }
                holder.dataBinding.ivEmail.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:")

                    intent.putExtra(Intent.EXTRA_EMAIL, itemList[position].second.email)
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    }
                }
            }

            holder.dataBinding.cgSkills.removeAllViews()
            holder.dataBinding.cgLocation.removeAllViews()
            for (item in itemList[position].second?.skills!!) {
                holder.dataBinding.cgSkills.addView(
                    (LayoutInflater.from(context).inflate(
                        R.layout.siple_chip,
                        holder.dataBinding.cgSkills,
                        false
                    ) as Chip)
                        .apply {
                            text = item.key
                            isCheckable = false
                        }
                )
            }

            for (item in itemList[position].second?.location!!) {
                holder.dataBinding.cgLocation.addView(
                    (LayoutInflater.from(context).inflate(
                        R.layout.siple_chip,
                        holder.dataBinding.cgLocation,
                        false
                    ) as Chip)
                        .apply {
                            text = item
                            isCheckable = false
                        }
                )
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }


        holder.dataBinding.root.setOnClickListener {

            if(search.not()) {

                var bundle =  Bundle()
                bundle.putParcelable("item", itemList[position].second)
                bundle.putString("id", itemList[position].first)
                bundle.putString("state", "update")

                (context as Activity).findNavController(R.id.nav_controller).navigate(
                    R.id.action_careerCoopHome_to_careerCoopHome3,
                    bundle
                )
            }
            else
            {
                var bundle =  Bundle()
                bundle.putParcelable("item", itemList[position].second)
                bundle.putString("id", itemList[position].first)
                bundle.putString("state", "show")

                (context as Activity).findNavController(R.id.nav_controller).navigate(
                    R.id.action_ccSearchFragment_to_careerCoopHome4,
                    bundle
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    private fun showDialog()
    {
        var dialog =  AlertDialog.Builder(context)
            .setView(
                LayoutInflater.from(context).inflate(
                    R.layout.edt_testimonials,
                    null,
                    false
                )
            )
            .create()

        dialog.show()
        dialog.findViewById<TextView>(R.id.tv_count).text = "0/250"
        var  temp = dialog?.findViewById<EditText>(R.id.tv_resp)
        temp?.doOnTextChanged { text, start, before, count ->
            dialog.findViewById<TextView>(R.id.tv_count).text = "${text?.length}/250"
        }
        dialog?.findViewById<Button>(R.id.add)?.setOnClickListener {
            dialog?.dismiss()
        }
    }
}