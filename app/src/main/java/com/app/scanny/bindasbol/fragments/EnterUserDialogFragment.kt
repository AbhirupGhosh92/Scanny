package com.app.scanny.bindasbol.fragments

import android.app.Dialog
import android.database.DatabaseUtils
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.scanny.R
import com.app.scanny.bindasbol.viewmodels.SignUpViewModel
import com.app.scanny.databinding.FragmentEnterUserDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class EnterUserDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)

    }

    private lateinit var dataBinding : FragmentEnterUserDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_enter_user_dialog,container,false)
        dataBinding.signUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.setCancelable(false)

        dataBinding.signUpViewModel?.errorText?.observe(viewLifecycleOwner, Observer {
            if(it.isNullOrEmpty())
            {
            }
            else {

                when(it)
                {
                    "OK" -> {
                        dismiss()
                    }
                    else -> {
                        dataBinding.tilNickName.error = it
                    }
                }


            }
        })

    }
}