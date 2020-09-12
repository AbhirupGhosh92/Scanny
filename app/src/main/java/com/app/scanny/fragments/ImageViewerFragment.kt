package com.app.scanny.fragments

import android.database.DatabaseUtils
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.app.scanny.R
import com.app.scanny.adapters.CustomGridAdapter
import com.app.scanny.databinding.FragmentImageViewerBinding
import java.io.File


class ImageViewerFragment : Fragment() {

    private lateinit var dataBinding : FragmentImageViewerBinding
    private var fileList = arrayListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_image_viewer, container, false)
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        getImages()
    }

    private fun getImages()
    {
        var dir =  File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Pictures/Scanny")

        if(dir.listFiles().isNullOrEmpty().not())
        {
            fileList.addAll(dir.listFiles()!!)
            dataBinding.gvImages.adapter = CustomGridAdapter(requireContext(),dir.listFiles()?.asList()!!)
        }


    }
}