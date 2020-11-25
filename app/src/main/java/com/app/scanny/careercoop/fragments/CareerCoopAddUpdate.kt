package com.app.scanny.careercoop.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.app.scanny.R
import com.app.scanny.careercoop.models.CcUserModel
import com.app.scanny.databinding.FragmentCareerCoopAddUpdateBinding

class CareerCoopAddUpdate : Fragment() {

    private lateinit var dataBinding : FragmentCareerCoopAddUpdateBinding
    private  var payload : CcUserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       dataBinding =  DataBindingUtil.inflate(inflater,R.layout.fragment_career_coop_add_update, container, false)
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        payload = arguments?.getParcelable("payload")
        renderUi()
    }

    private fun renderUi()
    {
        if(payload != null)
        {
            dataBinding.edtNameTxt.setText(payload?.detailsModel?.name.toString())
            dataBinding.edtEmailTxt.setText(payload?.detailsModel?.email.toString())
            dataBinding.edtPhoneTxt.setText(payload?.detailsModel?.phone.toString())
        }
    }
}