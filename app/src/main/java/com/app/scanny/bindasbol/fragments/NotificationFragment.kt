package com.app.scanny.bindasbol.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.scanny.Constants
import com.app.scanny.R
import com.app.scanny.bindasbol.adapter.ChatsAdapter
import com.app.scanny.bindasbol.models.BolModel
import com.app.scanny.bindasbol.viewmodels.BBSharedViewModel
import com.app.scanny.bindasbol.viewmodels.ChatHomeViewModel
import com.app.scanny.databinding.FragmentChatHomeBinding
import com.app.scanny.enums.NavEnums
import com.app.scanny.repository.Repository
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth

class NotificationFragment : Fragment() {

    private lateinit var viewModel: BBSharedViewModel
    private lateinit var chatViewModel : ChatHomeViewModel
    private var mAuth: FirebaseAuth? = null
    private val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build()
        ,AuthUI.IdpConfig.EmailBuilder().build())
    private  val RC_SIGN_IN = 556
    private var chatItems = arrayListOf<BolModel>()

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

    private fun renderChats()
    {
        if(Repository.mAuth.currentUser!=null)
        {
            dataBinding.ivDefault.visibility = View.GONE
        }

        dataBinding.fab.visibility = View.GONE

        dataBinding.rvChats.adapter = ChatsAdapter(requireContext(),chatItems,R.id.action_notificationFragment_to_showCommentsFragment,{it,likeState ->
            dataBinding.charHomeViewModel?.addLike(it.bolId!!,likeState,it)?.observe(viewLifecycleOwner,
                {
                    Log.d("LikeAdded",it)
                })
        },{
            var bundle = Bundle()
            bundle.putString(Constants.BOL_ID,it.bolId)
            findNavController().navigate(R.id.action_chatHomeFragment_to_addBolBottomSheet,bundle)
        })
        dataBinding.rvChats.itemAnimator = DefaultItemAnimator()
        dataBinding.rvChats.layoutManager = LinearLayoutManager(requireContext())

        dataBinding.charHomeViewModel?.getMyBols()?.observe(viewLifecycleOwner, {

            dataBinding.ivDefault.visibility = View.GONE

            if(it.isNullOrEmpty())
            {
                dataBinding.txtError.text = resources.getString(R.string.no_bols)
                dataBinding.txtError.visibility = View.VISIBLE
            }
            else
            {
                dataBinding.txtError.visibility = View.GONE
                chatItems.clear()
                chatItems.addAll(it)
                dataBinding.rvChats.adapter?.notifyDataSetChanged()
            }
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
        renderChats()
    }
}
