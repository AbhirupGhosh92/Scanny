package com.app.scanny.bindasbol.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.scanny.Constants
import com.app.scanny.R
import com.app.scanny.bindasbol.adapter.ChatsAdapter
import com.app.scanny.bindasbol.models.BolModel
import com.app.scanny.databinding.FragmentChatHomeBinding
import com.app.scanny.bindasbol.viewmodels.BBSharedViewModel
import com.app.scanny.bindasbol.viewmodels.ChatHomeViewModel
import com.app.scanny.enums.NavEnums
import com.app.scanny.repository.Repository
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


class ChatHomeFragment : Fragment() {

    private lateinit var viewModel: BBSharedViewModel
    private lateinit var chatViewModel : ChatHomeViewModel
    private var mAuth: FirebaseAuth? = null
    private val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
    private  val RC_SIGN_IN = 556
    private var chatItems = arrayListOf<Pair<String,BolModel>>()

    private lateinit var dataBinding: FragmentChatHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
    }


    override fun onStart() {
        super.onStart()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chat_home,
            container,
            false
        )
        return dataBinding.root
    }

    private fun checkName()
    {
        viewModel.checkAccess().observe(viewLifecycleOwner, {
            viewModel.userModel = it
            if(it == null) {
                findNavController().navigate(R.id.action_chatHomeFragment_to_enterUserDialogFragment)
            }
            else
            {
                viewModel.userModel  = it
                renderChats()
            }
        })
    }

    private fun renderChats()
    {
        dataBinding.rvChats.adapter = ChatsAdapter(requireContext(),chatItems,{it,likeState ->
            dataBinding.charHomeViewModel?.addLike(it.first,likeState,it.second)?.observe(viewLifecycleOwner,
                 {
                    Log.d("LikeAdded",it)
                })
        },{
            var bundle = Bundle()
            bundle.putString(Constants.BOL_ID,it.first)
            findNavController().navigate(R.id.action_chatHomeFragment_to_addBolBottomSheet,bundle)
        })
        dataBinding.rvChats.itemAnimator = DefaultItemAnimator()
        dataBinding.rvChats.layoutManager = LinearLayoutManager(requireContext())

        dataBinding.charHomeViewModel?.getAllBols()?.observe(viewLifecycleOwner, {
            chatItems.clear()
            chatItems.addAll(it)
            dataBinding.rvChats.adapter?.notifyDataSetChanged()
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(BBSharedViewModel::class.java)
        dataBinding.charHomeViewModel = ViewModelProvider(this).get(ChatHomeViewModel::class.java)

        dataBinding.charHomeViewModel?.snippet = {
            when(it)
            {
                NavEnums.NAV_ADD_BOLS -> {
                    findNavController().navigate(R.id.action_chatHomeFragment_to_addBolBottomSheet)
                }
            }
        }


        if(viewModel.signedIn.not()) {

            MobileAds.initialize(requireContext())

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )


        }
        else
        {
            renderChats()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            RC_SIGN_IN -> {

                val response = IdpResponse.fromResultIntent(data)

                if (resultCode == Activity.RESULT_OK) {
                    // Successfully signed in

                    viewModel.signedIn = true
                    checkName()

                    // ...
                } else {
                    dataBinding.txtError.visibility = View.VISIBLE
                }
            }
        }
    }
}