package com.app.scanny.careercoop.fragments

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.scanny.R
import com.app.scanny.bindasbol.viewmodels.BBSharedViewModel
import com.app.scanny.careercoop.models.CcUserModel
import com.app.scanny.databinding.FragmentCareerCoopAddUpdateBinding
import com.app.scanny.repository.Repository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip

class CareerCoopAddUpdate : Fragment() {

    private lateinit var  userType :  Array<String>
    private var  proList: ArrayList<String> = ArrayList()
    private var testList: ArrayList<String> = ArrayList()
    private lateinit var skillsList: List<String>
    private lateinit var cityList: List<String>
    private lateinit var dataBinding : FragmentCareerCoopAddUpdateBinding
    private  var payload : CcUserModel? = null
    private lateinit var viewModel : BBSharedViewModel

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
        userType = resources.getStringArray(R.array.user_type)
        viewModel = ViewModelProvider(this)[BBSharedViewModel::class.java]
        dataBinding.ccHomeViewodel = viewModel
        renderUi()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility = View.GONE
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

}