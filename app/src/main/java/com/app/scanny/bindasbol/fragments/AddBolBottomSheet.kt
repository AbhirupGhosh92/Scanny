package com.app.scanny.bindasbol.fragments

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.scanny.Constants
import com.app.scanny.R
import com.app.scanny.bindasbol.viewmodels.AddBolViewModel
import com.app.scanny.bindasbol.viewmodels.BBSharedViewModel
import com.app.scanny.databinding.FragmentAddBolBottomSheetBinding
import com.app.scanny.utils.ApplicationUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddBolBottomSheet : BottomSheetDialogFragment() {

    private lateinit var dataBinding : FragmentAddBolBottomSheetBinding
    private lateinit var sharedViewModel : BBSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =  DataBindingUtil.inflate(inflater,R.layout.fragment_add_bol_bottom_sheet,container,false)
        return  dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataBinding.addBolViewModel = ViewModelProvider(this).get(AddBolViewModel::class.java)
        sharedViewModel = ViewModelProvider(requireActivity()).get(BBSharedViewModel::class.java)

        dataBinding.addBolViewModel?.bolId = arguments?.getString(Constants.BOL_ID,"").toString()

        dataBinding.addBolViewModel?.nickname =  sharedViewModel.userModel?.nickName.toString()
        dataBinding.addBolViewModel?.bolResponse?.observe(viewLifecycleOwner, Observer {
            when(it)
            {
                "OK" -> {
                    dismiss()
                }
                else -> {

                }
            }


        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var myDialog = super.onCreateDialog(savedInstanceState)

        myDialog.setOnShowListener {
            var temp = it as BottomSheetDialog
            var view = temp.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            var behaviour = BottomSheetBehavior.from(view!!)
            behaviour.peekHeight = 0
            ObjectAnimator.ofInt(behaviour, "peekHeight", ApplicationUtils.getWindowHeight(requireContext())).apply {
                duration = 300
                start()
            }
            val layoutParams = view.layoutParams
            val windowHeight = ApplicationUtils.getWindowHeight(requireContext())
            if (layoutParams != null) {
                layoutParams.height = windowHeight
            }

            view.layoutParams = layoutParams
        }

        return myDialog

    }


}