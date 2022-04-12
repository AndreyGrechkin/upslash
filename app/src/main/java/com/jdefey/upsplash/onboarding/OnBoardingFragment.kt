package com.jdefey.upsplash.onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.jdefey.upsplash.R
import com.jdefey.upsplash.databinding.FragmentOnboardingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OnBoardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val binding by viewBinding(FragmentOnboardingBinding::bind)
    private var itemList = ArrayList<OnBoardingData>()
    private var pageChangeCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.buttonArrowLeft.visibility =
                    View.INVISIBLE.takeIf { position == 0 } ?: View.VISIBLE
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewPager()

        binding.buttonArrowLeft.setOnClickListener {
            backPageOnBoarding()
        }

        binding.buttonArrowRight.setOnClickListener {
            forwardPageOnBoarding()
        }
    }

    private fun onBoardingFinished() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val sharedPref =
                requireContext().getSharedPreferences(ON_BOARDING, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean(FINISHED, true)
            editor.apply()
        }
    }

    private fun setViewPager() {
        itemList = getItems()
        binding.onBoardingViewPager.adapter = ViewPagerAdapter(itemList)
        binding.onBoardingViewPager.registerOnPageChangeCallback(pageChangeCallback)
    }


    private fun forwardPageOnBoarding() {
        val currentItemPosition = binding.onBoardingViewPager.currentItem
        if (currentItemPosition == itemList.size - 1) {
            findNavController().navigate(OnBoardingFragmentDirections.actionOnBoardingFragmentToAuthFragment())
            onBoardingFinished()
        } else {
            binding.onBoardingViewPager.setCurrentItem(currentItemPosition + 1, true)
        }
    }

    private fun backPageOnBoarding() {
        val currentItemPosition = binding.onBoardingViewPager.currentItem
        if (currentItemPosition == 0) return
        binding.onBoardingViewPager.setCurrentItem(currentItemPosition - 1, true)
    }

    private fun getItems(): ArrayList<OnBoardingData> {
        val items = ArrayList<OnBoardingData>()
        items.add(OnBoardingData(getString(R.string.onBoarding_title1)))
        items.add(OnBoardingData(getString(R.string.onBoarding_title2)))
        items.add(OnBoardingData(getString(R.string.onBoarding_title3)))
        return items
    }

    companion object {
        const val ON_BOARDING = "onBoarding"
        const val FINISHED = "finished"
    }
}