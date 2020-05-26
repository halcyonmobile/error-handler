package com.halcyonmobile.errorhandling.feature.sample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.halcyonmobile.errorhandling.R
import com.halcyonmobile.errorhandling.SampleBinding
import com.halcyonmobile.errorhandling.shared.error.ErrorItem
import com.halcyonmobile.errorhandling.shared.observeEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class SampleFragment : Fragment(R.layout.fragment_sample) {

    private val sampleViewModel: SampleViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = SampleBinding.bind(view)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sampleViewModel
        }

        sampleViewModel.event.observeEvent(viewLifecycleOwner) { event ->
            when (event) {
                is SampleViewModel.Event.UserActionFailed -> displayErrorInformation(binding, event.errorItem)
            }
        }
    }

    private fun displayErrorInformation(binding: SampleBinding, errorItem: ErrorItem) {
        val text = getString(errorItem.messageRes) + (errorItem.traceId.takeIf { it != null }?.let { "($it)" } ?: "")
        val snackbar = Snackbar.make(binding.root, text, if (errorItem.showIndefinite) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_LONG)

        if (errorItem.actionStringRes != null && errorItem.action != null) {
            snackbar.setAction(errorItem.actionStringRes) { errorItem.action.invoke() }
        }

        snackbar.show()
    }
}