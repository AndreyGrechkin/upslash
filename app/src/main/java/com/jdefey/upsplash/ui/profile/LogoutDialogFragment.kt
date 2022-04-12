package com.jdefey.upsplash.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jdefey.upsplash.auth.AuthViewModel
import com.jdefey.upsplash.databinding.BottomSheetDialogFragmentBinding
import com.jdefey.upsplash.ui.photo.PhotoListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@AndroidEntryPoint
@FlowPreview
@ExperimentalCoroutinesApi
class LogoutDialogFragment : BottomSheetDialogFragment() {
    private val viewModel: AuthViewModel by viewModels()
    private val viewModelPhoto by viewModels<PhotoListViewModel>()
    private var _binding: BottomSheetDialogFragmentBinding? = null
    private val binding: BottomSheetDialogFragmentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButtonDialog.setOnClickListener {
            dismiss()
        }
        binding.allowButtonDialog.setOnClickListener {
            viewModel.logout()
            viewModelPhoto.deletePhotoFromDataBase()
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAuthFragment())
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}