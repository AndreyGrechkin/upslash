package com.jdefey.upsplash.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jdefey.upsplash.databinding.ItemOnboardingBinding

class ViewPagerAdapter(private var onBoardingList: List<OnBoardingData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBinding =
            ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnBoardingViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = onBoardingList[position]
        when (holder) {
            is OnBoardingViewHolder -> holder.bind(data)
        }
    }

    override fun getItemCount(): Int {
        return onBoardingList.size
    }

    class OnBoardingViewHolder(private val itemBinding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: OnBoardingData) {
            itemBinding.textOnBoarding.text = data.title
        }
    }
}