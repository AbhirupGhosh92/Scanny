package com.app.scanny.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.app.scanny.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class AuthFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var mAuth: FirebaseAuth
    private val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
    private  val RC_SIGN_IN = 556

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
         viewModel.authUser = mAuth.currentUser

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.auth_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

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
            goToNext()
        }

    }

    private fun goToNext()
    {
        findNavController().navigate(R.id.action_authFragment_to_chatHomeFragment)
        findNavController().popBackStack()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            RC_SIGN_IN -> {

                val response = IdpResponse.fromResultIntent(data)

                if (resultCode == Activity.RESULT_OK) {
                    // Successfully signed in
                    viewModel.authUser = FirebaseAuth.getInstance().currentUser

                    goToNext()

                    // ...
                } else {
                    Toast.makeText(
                        requireContext(),
                        "${response?.error?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}