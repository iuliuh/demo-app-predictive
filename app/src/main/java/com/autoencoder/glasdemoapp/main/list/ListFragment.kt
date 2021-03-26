package com.autoencoder.glasdemoapp.main.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.databinding.ListFragmentBinding
import com.autoencoder.glasdemoapp.main.list.adapter.DemoActivitiesAdapter
import com.autoencoder.glasdemoapp.shared.base.BaseFragment
import com.autoencoder.glasdemoapp.shared.utils.extensions.showDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListFragment : BaseFragment<ListFragmentBinding>() {

    override val viewModel by viewModel<ListViewModel>()

    private val adapter: DemoActivitiesAdapter by lazy {
        DemoActivitiesAdapter(viewModel::onActivityClicked)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ListFragmentBinding.inflate(layoutInflater).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.setVariable(BR.viewModel, viewModel)
        binding = it
    }.root

    override fun setupViews() {
        binding?.demoActivities?.adapter = adapter
        setupObservers()
        viewModel.bootGlas()
    }

    private fun setupObservers() {
        viewModel.activities.observe(viewLifecycleOwner, adapter::submitList)
        viewModel.cmd.observe(viewLifecycleOwner) { command ->
            when (command) {
                is ListViewModel.Command.ShowDialog ->
                    showDialog(
                        getString(
                            R.string.dialog_title,
                            getString(command.title),
                        ),
                        getString(R.string.dialog_description_placeholder, command.percentage),
                        icon = command.icon
                    )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterListeners()
    }
}
