package com.app.scanny.bindasbol.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.scanny.R
import com.app.scanny.databinding.FragmentChatHomeBinding
import com.app.scanny.bindasbol.viewmodels.BBSharedViewModel
import com.app.scanny.repository.Repository
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


class ChatHomeFragment : Fragment() {

    var db = FirebaseFirestore.getInstance()
    private lateinit var viewModel: BBSharedViewModel
    private lateinit var mAuth: FirebaseAuth
    private val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
    private  val RC_SIGN_IN = 556

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
        Repository.checkAcces()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(it)
                {

                }
                else
                {
                    Toast.makeText(requireContext(),"No Account",Toast.LENGTH_SHORT).show()
                }
            },
            {

                it.printStackTrace()

            })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(BBSharedViewModel::class.java)

        if(viewModel.authUser == null)
        {
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
            checkName()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            RC_SIGN_IN -> {

                val response = IdpResponse.fromResultIntent(data)

                if (resultCode == Activity.RESULT_OK) {
                    // Successfully signed in
                    viewModel.authUser = FirebaseAuth.getInstance().currentUser

                    checkName()

                    // ...
                } else {
                    dataBinding.txtError.visibility = View.VISIBLE
                }
            }
        }
    }
}