package com.app.scanny.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import com.app.scanny.R
import java.io.File


class CustomGridAdapter(var context : Context, var list: List<File>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var imageView : ImageView
        if(convertView == null)
        {
            var innflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            imageView = innflater.inflate(R.layout.grid_item,null).findViewById(R.id.iv_item)
            val myBitmap = BitmapFactory.decodeFile(list[position].absolutePath)
            imageView.setImageBitmap(myBitmap)

        }
        else
        {
            imageView = convertView as ImageView
        }

        return imageView
    }

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long = 0

    override fun getCount(): Int = list.size

}