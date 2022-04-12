package com.jdefey.upsplash.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.jdefey.upsplash.R
import com.jdefey.upsplash.databinding.FragmentAuthBinding
import com.jdefey.upsplash.utils.toast
import net.openid.appauth.*

class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val binding: FragmentAuthBinding by viewBinding(FragmentAuthBinding::bind)
    private val viewModel: AuthViewModel by viewModels()
    private var response: ActivityResultLauncher<Intent>? = null
    private var responseLogout: ActivityResultLauncher<Intent>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        loginToApp()
        logoutToApp()
    }

    private fun loginToApp() {
        response =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    val tokenExchangeRequest = result.data?.let {
                        AuthorizationResponse.fromIntent(it)
                            ?.createTokenExchangeRequest()
                    }
                    val exception = AuthorizationException.fromIntent(result.data)
                    when {
                        tokenExchangeRequest != null && exception == null ->
                            viewModel.onAuthCodeReceived(tokenExchangeRequest)
                        exception != null -> viewModel.onAuthCodeFailed()
                    }
                }
            }
    }

    private fun logoutToApp() {
        responseLogout =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    }

    private fun openAuthPageLogout(intent: Intent) {
        responseLogout?.launch(intent)
    }

    private fun openAuthPage(intent: Intent) {
        response?.launch(intent)
    }

    private fun bindViewModel() {
        viewModel.tokenSusses()
        binding.loginButton.setOnClickListener { viewModel.openLoginPage() }
        viewModel.loadingLiveData.observe(viewLifecycleOwner, ::updateIsLoading)
        viewModel.openAuthPageLiveData.observe(viewLifecycleOwner, ::openAuthPage)
        viewModel.openAuthPageLiveData2.observe(viewLifecycleOwner, ::openAuthPageLogout)
        viewModel.toastLiveData.observe(viewLifecycleOwner, ::toast)
        viewModel.authSuccessLiveData.observe(viewLifecycleOwner) {
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToPhotoListFragment())
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        binding.loginButton.isVisible = !isLoading
        binding.loginProgress.isVisible = isLoading
    }
}