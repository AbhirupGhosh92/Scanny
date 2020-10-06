package com.app.scanny.fragments

import android.os.Bundle
import android.os.Environment
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.app.scanny.R
import com.app.scanny.adapters.CustomListAdapter
import com.app.scanny.databinding.FragmentImageViewerBinding
import com.felipecsl.asymmetricgridview.library.Utils
import kotlinx.coroutines.*
import java.io.File


class ImageViewerFragment : Fragment() {

    private lateinit var dataBinding : FragmentImageViewerBinding
    private var fileList = arrayListOf<String>()

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

        dataBinding.listView.adapter = CustomListAdapter(requireContext(),fileList)
        {
            var bundle = Bundle()
            bundle.putString("image",fileList[it])

            var frag = PreviewDialogFragment()
            frag.arguments = bundle
            frag.show(requireFragmentManager(),"")
        }
        dataBinding.listView.itemAnimator = DefaultItemAnimator()
        dataBinding.listView.layoutManager = GridLayoutManager(requireContext(),4)
        dataBinding.listView.adapter?.notifyDataSetChanged()

        CoroutineScope(Dispatchers.Default).launch {
            getImages()
        }

    }

    override fun onResume() {
        super.onResume()
    }

    private suspend fun getImages()
    {
        var dir =  File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Pictures/Scanny")

        if(dir.listFiles().isNullOrEmpty().not())
        {
            fileList.clear()
            fileList.addAll(dir.listFiles()?.getPathsList()?.sorted()?.reversed()!!)
            delay(400)
            CoroutineScope(Dispatchers.Main).launch{
                dataBinding.listView.adapter?.notifyDataSetChanged()
            }
        }


    }

    private fun Array<File>.getPathsList() : Collection<String>
    {
        var temp = arrayListOf<String>()
        for(item in this)
        {
            temp.add(item.absolutePath)
        }

        return temp
    }
}