package com.app.scanny.careercoop.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.scanny.BuildConfig
import com.app.scanny.R
import com.app.scanny.bindasbol.viewmodels.BBSharedViewModel
import com.app.scanny.databinding.FragmentCareerCoopHomeBinding
import com.app.scanny.repository.Repository
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.ads.MobileAds
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth

class CareerCoopHome : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var mAuth : FirebaseAuth
    private val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

    private  val RC_SIGN_IN = 555
    private lateinit var dataBinding : FragmentCareerCoopHomeBinding
    private lateinit var viewModel : BBSharedViewModel
    private lateinit var userType : Array<String>
    private lateinit var cityList : List<String>
    private lateinit var skillsList : List<String>
    private lateinit var simpleChip: Chip
    private  var proList = ArrayList<String>()
    private  var testList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        if(BuildConfig.DEBUG)
        {
            providers.add(AuthUI.IdpConfig.EmailBuilder().build())
        }
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
        dataBinding.ccHomeViewodel = viewModel

        userType = resources.getStringArray(R.array.user_type)

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

        viewModel.notifyChange()
        dataBinding.spUser.adapter = ArrayAdapter<String>(requireContext(),R.layout.spinner_text,userType)

        dataBinding.spUser.setSelection(if(  viewModel.isRecruiter) 0 else 1)

        dataBinding.spUser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.isRecruiter = p2 == 0
                viewModel.notifyChange()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }


        }

        viewModel.checkCcAccess().observe(viewLifecycleOwner, Observer {
            viewModel.showForm = it.uid.isNullOrEmpty()
            viewModel.notifyChange()
            dataBinding.edtNameTxt.setText(Repository.mAuth.currentUser?.displayName.toString())
            dataBinding.edtEmailTxt.setText(Repository.mAuth.currentUser?.email.toString())

        })

        if(Repository.getCityList().isNullOrEmpty().not() && Repository.getSkillsList().isNullOrEmpty().not())
        {
            cityList = Repository.getCityList()
            skillsList = Repository.getSkillsList()
        }

        viewModel.skillSelectLiveData.observe(viewLifecycleOwner, Observer {

            dataBinding.chipGroupSkills.removeAllViews()
            if(viewModel.skills.size == 3 && viewModel.skills.contains(it).not())
            {
                viewModel.skills.removeFirst()
            }
            if(viewModel.skills.contains(it).not())
                viewModel.skills.add(it)
            for (item in viewModel.skills)
            {

                dataBinding.chipGroupSkills.addView(
                    (requireActivity().layoutInflater.inflate(R.layout.siple_chip
                        , dataBinding.chipGroupSkills, false) as Chip).apply {
                        text = item
                        isCheckable = false
                        setOnClickListener {view ->
                            viewModel.skills.remove((view as Chip).text)
                            dataBinding.chipGroupSkills.removeView(view)
                        }
                    }
                )
            }


        })

        viewModel.citySelecteLiveData.observe(viewLifecycleOwner, Observer {

            dataBinding.chipGroupCities.removeAllViews()
            if(viewModel.cities.size == 3 && viewModel.cities.contains(it).not())
            {
                viewModel.cities.removeFirst()
            }
            if( viewModel.cities.contains(it).not())
                viewModel.cities.add(it)
            for (item in viewModel.cities)
            {

                dataBinding.chipGroupCities.addView(
                    (requireActivity().layoutInflater.inflate(R.layout.siple_chip
                        , dataBinding.chipGroupCities, false) as Chip).apply {
                        text = item
                        isCheckable = false
                        setOnClickListener {view ->
                            viewModel.cities.remove((view as Chip).text)
                            dataBinding.chipGroupCities.removeView(view)
                        }
                    }
                )
            }

        })

        viewModel.testimonialLiveData.observe(viewLifecycleOwner,Observer {

            testList.clear()
            testList.addAll(it)
            dataBinding.viewTestimonials.removeAllViews()
            for(i  in 0 until  testList.size)
            {
                var view = LayoutInflater.from(requireContext()).inflate(R.layout.ll_view_projects,null,false)
                view.findViewById<TextView>(R.id.tv_resp).text = testList[i]
                view.findViewById<AppCompatImageView>(R.id.iv_del).setOnClickListener {
                    viewModel.deleteTestimonial(i)
                }
                dataBinding.viewTestimonials.addView(view)
            }
        })

        viewModel.projectLiveData.observe(viewLifecycleOwner, Observer {
            proList.clear()
            proList.addAll(it)

            dataBinding.viewProjects.removeAllViews()
            for(i in 0 until  proList.size)
            {
                var view = LayoutInflater.from(requireContext()).inflate(R.layout.ll_view_projects,null,false)
                view.findViewById<TextView>(R.id.tv_resp).text = proList[i]
                view.findViewById<AppCompatImageView>(R.id.iv_del).setOnClickListener {
                    viewModel.deleteProject(i)
                }
                dataBinding.viewProjects.addView(view)
            }
        })

        dataBinding.swWorking.isChecked = viewModel.isWorking
        dataBinding.swWorking.setOnCheckedChangeListener { compoundButton, b ->
            viewModel.isWorking = b
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
                    renderUi()

                    // ...
                } else {

                }
            }
        }
    }
}