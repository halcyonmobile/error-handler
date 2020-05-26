package com.halcyonmobile.errorhandling.feature.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halcyonmobile.errorhandlercore.DataLayerException
import com.halcyonmobile.errorhandlercore.ResultWrapper
import com.halcyonmobile.errorhandling.R
import com.halcyonmobile.errorhandling.core.model.User
import com.halcyonmobile.errorhandling.core.usecase.GetUsersUseCase
import com.halcyonmobile.errorhandling.core.usecase.LoginUseCase
import com.halcyonmobile.errorhandling.core.usecase.RegisterUseCase
import com.halcyonmobile.errorhandling.error.ApiErrorCode
import com.halcyonmobile.errorhandling.error.DemoAppApiError
import com.halcyonmobile.errorhandling.shared.Consumable
import com.halcyonmobile.errorhandling.shared.error.ErrorItem
import com.halcyonmobile.errorhandling.shared.error.ErrorParser
import com.halcyonmobile.errorhandling.util.causedBy
import com.halcyonmobile.errorhandling.util.exhaustive
import kotlinx.coroutines.launch
import timber.log.Timber

class SampleViewModel(
    private val register: RegisterUseCase,
    private val login: LoginUseCase,
    private val getUsers: GetUsersUseCase,
    private val errorParser: ErrorParser
) : ViewModel() {

    val userName = MutableLiveData("userName")
    val password = MutableLiveData("password")

    private val _lastResponse = MutableLiveData<String>()
    val lastResponse: LiveData<String> get() = _lastResponse

    private val _event = MutableLiveData<Consumable<Event>>()
    val event: LiveData<Consumable<Event>> get() = _event

    // region Registration
    fun registerUser() {
        viewModelScope.launch {
            when (val result = register(userName.value.orEmpty(), "firstName", "lastName", "email@email.com", password.value.orEmpty())) {
                is ResultWrapper.Success -> handleRegistrationSuccess(result.data)
                is ResultWrapper.Error -> handleRegistrationError(result.dataLayerException)
                is ResultWrapper.Exception -> {
                    Timber.e(result.exception, "[Exception] ${result.exception}")
                    _lastResponse.value = "[Exception] ${result.exception}"
                    _event.value = Consumable(Event.UserActionFailed(ErrorItem(R.string.error_something_went_wrong, null, null, null)))
                }
            }.exhaustive
        }
    }

    private fun handleRegistrationSuccess(user: User) {
        with("[Success] $user") {
            Timber.d(this)
            _lastResponse.value = this
        }
    }

    private fun handleRegistrationError(dataLayerException: DataLayerException) {
        with("[Error] $dataLayerException") {
            val error =
                if ((dataLayerException as? DemoAppApiError)?.payload?.causedBy(ApiErrorCode.USERNAME_TAKEN) == true) {
                    ErrorItem(R.string.error_username_taken, null, null, null)
                } else {
                    errorParser.extractErrorInfoFromException(dataLayerException, ::registerUser, null)
                }

            Timber.d(this)
            _lastResponse.value = this
            _event.value = Consumable(Event.UserActionFailed(error))
        }
    }
    // endregion Registration

    // region Login
    fun loginUser() {
        viewModelScope.launch {
            when (val result = login(userName.value.orEmpty(), password.value.orEmpty())) {
                is ResultWrapper.Success -> handleLoginSuccess(result.data)
                is ResultWrapper.Error -> handleLoginError(result.dataLayerException)
                is ResultWrapper.Exception -> {
                    Timber.e(result.exception, "[Exception] ${result.exception}")
                    _lastResponse.value = "[Exception] ${result.exception}"
                    _event.value = Consumable(Event.UserActionFailed(ErrorItem(R.string.error_something_went_wrong, null, null, null)))
                }
            }.exhaustive
        }
    }

    private fun handleLoginSuccess(user: User) {
        with("[Success] $user") {
            Timber.d(this)
            _lastResponse.value = this
        }
    }

    private fun handleLoginError(dataLayerException: DataLayerException) {
        with("[Error] $dataLayerException") {
            Timber.d(this)
            _lastResponse.value = this

            val error = if ((dataLayerException as? DemoAppApiError)?.payload?.causedBy(ApiErrorCode.INVALID_CREDENTIALS) == true) {
                ErrorItem(R.string.error_invalid_credentials, null, null, null)
            } else {
                errorParser.extractErrorInfoFromException(dataLayerException, ::loginUser, null)
            }

            _event.value = Consumable(Event.UserActionFailed(error))
        }
    }
    // endregion Login

    // region Users
    fun getUserList() {
        viewModelScope.launch {
            when (val result = getUsers()) {
                is ResultWrapper.Success -> handleUsersSuccess(result.data)
                is ResultWrapper.Error -> handleUsersError(result.dataLayerException)
                is ResultWrapper.Exception -> {
                    Timber.e(result.exception, "[Exception] ${result.exception}")
                    _lastResponse.value = "[Exception] ${result.exception}"
                    _event.value = Consumable(Event.UserActionFailed(ErrorItem(R.string.error_something_went_wrong, null, null, null)))
                }
            }.exhaustive
        }
    }

    private fun handleUsersSuccess(users: List<User>) {
        with("[Success] $users") {
            Timber.d(this)
            _lastResponse.value = this
        }
    }

    private fun handleUsersError(dataLayerException: DataLayerException) {
        with("[Error] $dataLayerException") {
            Timber.d(this)
            _lastResponse.value = this

            _event.value = Consumable(Event.UserActionFailed(errorParser.extractErrorInfoFromException(dataLayerException, ::getUserList, null)))
        }
    }
    // endregion Users

    sealed class Event {
        data class UserActionFailed(val errorItem: ErrorItem) : Event()
    }
}
