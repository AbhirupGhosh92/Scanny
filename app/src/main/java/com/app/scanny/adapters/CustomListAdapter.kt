package com.app.scanny.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.scanny.R
import com.app.scanny.databinding.GridItemBinding
import com.bumptech.glide.Glide
import java.io.File


class CustomListAdapter(var context : Context,var pathList: ArrayList<String>,var snippet : (position : Int) -> Unit) : RecyclerView.Adapter<CustomListAdapter.ViewHolder>() {

    private  var staeArray =  ArrayList<Boolean>()

   data class ViewHolder(var dataBindig : GridItemBinding) : RecyclerView.ViewHolder(dataBindig.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(staeArray.size != pathList.size)
            staeArray = pathList.toBoolenFalse()
       return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.grid_item,parent,false))
    }

    override fun getItemCount(): Int {
       return pathList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

                Glide.with(context)
                    .load(pathList[position])
                    .into(holder.dataBindig.ivItem)


                holder.dataBindig.cbImg.isChecked = staeArray[position]

                holder.dataBindig.cbImg.setOnClickListener{
                    staeArray[position] = holder.dataBindig.cbImg.isChecked
                }

                holder.dataBindig.ivItem.setOnClickListener {
                    var file = File(pathList[position])
                    var intent = Intent(Intent.ACTION_VIEW) //
                        .setDataAndType(
                            if (VERSION.SDK_INT >= VERSION_CODES.N) FileProvider.getUriForFile(
                                context,
                                context.packageName.toString() + ".provider",
                                file) else Uri.fromFile(file),
                            "image/*"
                        ).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    context.startActivity(intent)
                }

                holder.dataBindig.ivItem.setOnLongClickListener {

                    if(staeArray.sum() > 0)
                    {
                        var temp = ArrayList<Uri>()
                        pathList.slice(staeArray.getTrueIndex()).forEach {
                            temp.add(if (VERSION.SDK_INT >= VERSION_CODES.N) FileProvider.getUriForFile(
                                context,
                                context.packageName.toString() + ".provider",
                                File(it)) else Uri.fromFile(File(it)))
                        }
                        var intent = Intent(Intent.ACTION_SEND_MULTIPLE) //
                            .setType("image/jpeg")
                            .putParcelableArrayListExtra(Intent.EXTRA_STREAM,temp)
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        context.startActivity(intent)
                    }
                    else
                    {

                        var file = File(pathList[position])
                        var intent = Intent(Intent.ACTION_SEND) //
                            .setType("image/jpeg")
                            .putExtra(Intent.EXTRA_STREAM, if (VERSION.SDK_INT >= VERSION_CODES.N) FileProvider.getUriForFile(
                                context,
                                context.packageName.toString() + ".provider",
                                file) else Uri.fromFile(file))


                        context.startActivity(intent)

//                    val intent = Intent(
//                        Intent.ACTION_VIEW, Uri.parse(
//                            "content://media/internal/images/media"
//                        )
//                    )

                        /** replace with your own uri */

//                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
//                        // Provide read access to files and sub-directories in the user-selected
//                        // directory.
//                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//
//                        // Optionally, specify a URI for the directory that should be opened in
//                        // the system file picker when it loads.
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pathList[position].toUri())
//                        }
//                    }
//
//                    var chooser = Intent.createChooser(intent,"Open Image")
//
//                    context.startActivity(chooser)
                    }

                    return@setOnLongClickListener true
                }


    }

    private fun<T> ArrayList<T>.sum() : Int
    {
        var temp = 0
        this.forEach {
           when(it)
           {
               is Int -> {
                   temp += it
               }

               is Boolean -> {
                   if(it == false)
                   {
                       temp+=0
                   }
                   else
                   {
                       temp+=1
                   }
               }

               else -> {

               }
           }
        }

        return temp
    }

    private fun<T> ArrayList<T>.toBoolenFalse() : ArrayList<Boolean>
    {
        var temp = ArrayList<Boolean>()

        this.forEach { x ->
            temp.add(false)
        }

        return temp
    }

    private fun<T> ArrayList<T>.getTrueIndex() : ArrayList<Int>
    {
        var temp = ArrayList<Int>()

        for(i in 0 until this.size )
        {
            if(this[i] == true)
            {
                temp.add(i)
            }

        }

        return temp
    }

}