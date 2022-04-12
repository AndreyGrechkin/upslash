package com.jdefey.upsplash.auth

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jdefey.upsplash.R
import com.jdefey.upsplash.onboarding.OnBoardingFragment

class OpenFragment : Fragment(R.layout.fragment_open) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startToApp()
    }

    private fun startToApp(){
        Handler(Looper.myLooper()!!).postDelayed({
            if (onBoardingFinished()) {
                findNavController().navigate(OpenFragmentDirections.actionOpenFragmentToAuthFragment())
            } else {
                findNavController().navigate(OpenFragmentDirections.actionOpenFragmentToOnBoardingFragment())
            }
        }, 1000)
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPref = requireContext().getSharedPreferences(
            OnBoardingFragment.ON_BOARDING,
            Context.MODE_PRIVATE
        )
        return sharedPref.getBoolean(OnBoardingFragment.FINISHED, false)
    }
}