package com.autoencoder.glasdemoapp.main.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.autoencoder.glasdemoapp.shared.base.BaseCommand
import com.autoencoder.glasdemoapp.shared.base.BaseViewModel
import com.autoencoder.glasdemoapp.shared.utils.LiveEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DELAY = 800L

class SplashViewModel : BaseViewModel() {

    private val _cmd = LiveEvent<Command>()
    val cmd: LiveData<Command> = _cmd

    fun navigateToListView() {
        viewModelScope.launch {
            delay(DELAY)
            _baseCmd.postValue(
                BaseCommand.Navigate(SplashFragmentDirections.actionSplashFragmentToListFragment())
            )
        }
    }

    fun displayPermissionsRefusedDialog() {
        _cmd.value = Command.ShowRequestedPermissionsDialog
    }

    sealed class Command {
        object ShowRequestedPermissionsDialog: Command()
    }
}