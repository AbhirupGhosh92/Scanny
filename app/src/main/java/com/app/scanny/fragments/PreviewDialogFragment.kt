package com.app.scanny.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.app.scanny.R
import com.app.scanny.databinding.PreviewLayoutBinding
import com.bumptech.glide.Glide

class PreviewDialogFragment : DialogFragment() {

    private lateinit var dataBindinng : PreviewLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        dataBindinng = DataBindingUtil.inflate(inflater, R.layout.preview_layout,container,false)

        Glide.with(this)
            .load(arguments?.getString("image"))
            .into(dataBindinng.ivPreview)

        return dataBindinng.root
    }

}