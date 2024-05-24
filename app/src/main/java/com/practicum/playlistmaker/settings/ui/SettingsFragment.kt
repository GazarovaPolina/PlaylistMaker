package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.themeSwitcherState.observe(viewLifecycleOwner) { isChecked ->
            binding.themeSwitcher.isChecked = isChecked
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitcherClicked(isChecked)
        }

        binding.textViewShare.setOnClickListener {
            viewModel.onShareAppLinkClicked()
        }

        binding.textViewSupport.setOnClickListener {
            viewModel.onWriteToSupportClicked()
        }

        binding.textViewAgreement.setOnClickListener {
            viewModel.onAgreementClicked()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}