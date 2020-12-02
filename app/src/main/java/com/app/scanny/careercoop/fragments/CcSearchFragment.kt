package com.app.scanny.careercoop.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.scanny.R
import com.app.scanny.bindasbol.viewmodels.BBSharedViewModel
import com.app.scanny.careercoop.adapters.CarrerCoopAdapter
import com.app.scanny.careercoop.models.CcUserModel
import com.app.scanny.databinding.FragmentCcSearchBinding
import com.app.scanny.repository.Repository
import com.google.android.material.chip.Chip

class CcSearchFragment : Fragment() {

    private lateinit var dataBinding : FragmentCcSearchBinding
    private lateinit var viewModel  : BBSharedViewModel
    private var list = ArrayList<Pair<String,CcUserModel>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =  DataBindingUtil.inflate(inflater,R.layout.fragment_cc_search, container, false)
        return dataBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this)[BBSharedViewModel::class.java]
        dataBinding.ccHomeViewodel =  viewModel

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

        dataBinding.rvSearch.adapter  = CarrerCoopAdapter(requireContext(),list)
        dataBinding.rvSearch.itemAnimator = DefaultItemAnimator()
        dataBinding.rvSearch.layoutManager = LinearLayoutManager(requireContext())

        dataBinding.floatingActionButton.setOnClickListener {
            updateData()
        }

    }

    private fun updateData()
    {
        if(viewModel.skills.isNullOrEmpty() || viewModel.cities.isNullOrEmpty())
        {
            Toast.makeText(requireContext(),resources.getString(R.string.select_values),Toast.LENGTH_SHORT).show()
        }
        else {
            viewModel.getData(viewModel.skills, viewModel.cities)
                .observe(viewLifecycleOwner, Observer {
                    list.clear()
                    list.addAll(it)
                    dataBinding.rvSearch.adapter?.notifyDataSetChanged()
                })
        }
    }
}