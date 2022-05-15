package com.ewind.newsapptest.view.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.ewind.newsapi.presentation.main.base.BaseFragment
import com.ewind.newsapptest.R
import com.ewind.newsapptest.databinding.FragmentProfileBinding
import com.ewind.newsapptest.domain.model.DUser
import com.ewind.newsapptest.util.Resource
import com.ewind.newsapptest.util.ResourceState
import com.ewind.newsapptest.util.ext.showToast
import com.ewind.newsapptest.view.main.register.RegisterActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    private val profileViewModel by viewModel<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel.userLiveData.observe(this, Observer { setData(it) })
        profileViewModel.updateLiveData.observe(this, Observer { updateView(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)
        profileViewModel.getUser()

        binding.btnSignUp.setOnClickListener {
            profileViewModel.deleteUser()
        }
    }

    private fun setData(resource: Resource<DUser>?) {
        resource?.let {
            when (it.state) {
                ResourceState.LOADING -> {
                }
                ResourceState.SUCCESS -> {
                    binding.tvUserName.text = it.data?.name
                    binding.tvEmail.text = it.data?.email
                }
                ResourceState.ERROR -> {
                    //Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateView(resource: Resource<String>?) {
        resource?.let {
            when (it.state) {
                ResourceState.LOADING -> {
                }
                ResourceState.SUCCESS -> {
                    it.data?.showToast(requireContext())
                    goToRegister()
                }
                ResourceState.ERROR -> {
                    //Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun goToRegister() {
        requireActivity().startActivity(
            Intent(
                requireActivity(),
                RegisterActivity::class.java
            )
        )
        requireActivity().finishAffinity()
    }
}
