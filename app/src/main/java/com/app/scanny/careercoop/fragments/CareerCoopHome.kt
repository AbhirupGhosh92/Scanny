package com.app.scanny.careercoop.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.scanny.R
import com.app.scanny.bindasbol.viewmodels.BBSharedViewModel
import com.app.scanny.careercoop.viewodels.CcHomeViewModel
import com.app.scanny.databinding.FragmentCareerCoopHomeBinding
import com.app.scanny.repository.Repository
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth

class CareerCoopHome : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var mAuth : FirebaseAuth
    private val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
    private  val RC_SIGN_IN = 555
    private lateinit var dataBinding : FragmentCareerCoopHomeBinding
    private lateinit var viewModel : BBSharedViewModel
    private lateinit var ccHomeViewModel: CcHomeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding  = DataBindingUtil.inflate(inflater,R.layout.fragment_career_coop_home,container,false)
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[BBSharedViewModel::class.java]
        ccHomeViewModel = ViewModelProvider(this)[CcHomeViewModel::class.java]
        dataBinding.ccHomeViewodel = ccHomeViewModel
        if(viewModel.signedIn.not()) {

            MobileAds.initialize(requireContext())

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )
        }
        else
        {
            renderUi()
        }
    }

    private fun renderUi()
    {
        ccHomeViewModel.checkCcAccess().observe(viewLifecycleOwner, Observer {
            ccHomeViewModel.showForm = it.uid.isNullOrEmpty()
            ccHomeViewModel.notifyChange()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            RC_SIGN_IN -> {

                val response = IdpResponse.fromResultIntent(data)

                if (resultCode == Activity.RESULT_OK) {
                    // Successfully signed in

                    viewModel.signedIn = true
                    renderUi()

                    // ...
                } else {

                }
            }
        }
    }
}