package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SessionManager
import com.example.data.model.SalesmanUser
import com.example.data.model.UserRole
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUser: SalesmanUser? = null,
    val isOwnerLoggedIn: Boolean = false,
    val enteredPhone: String = "",
    val enteredPin: String = "",
    val isAuthenticating: Boolean = false,
    val errorMessage: String? = null,
    val isAutoLoggingIn: Boolean = true
)

class AuthViewModel(
    private val repository: ShopRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val activeSalesmen: StateFlow<List<SalesmanUser>> = repository.getActiveSalesmen()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        checkSavedSession()
    }

    private fun checkSavedSession() {
        val savedUser = sessionManager.getSavedSession()
        if (savedUser != null) {
            _uiState.value = _uiState.value.copy(
                currentUser = savedUser,
                isOwnerLoggedIn = savedUser.role.equals(UserRole.OWNER.name, ignoreCase = true),
                isAutoLoggingIn = false
            )
        } else {
            _uiState.value = _uiState.value.copy(isAutoLoggingIn = false)
        }
    }

    fun onPhoneChanged(phone: String) {
        _uiState.value = _uiState.value.copy(
            enteredPhone = phone.filter { it.isDigit() }.take(10),
            errorMessage = null
        )
    }

    fun onPinChanged(pin: String) {
        _uiState.value = _uiState.value.copy(
            enteredPin = pin.filter { it.isDigit() }.take(6),
            errorMessage = null
        )
    }

    fun onQuickFill(phone: String, pin: String) {
        _uiState.value = _uiState.value.copy(
            enteredPhone = phone,
            enteredPin = pin,
            errorMessage = null
        )
        loginWithPhoneAndPin(phone, pin)
    }

    fun loginWithPhoneAndPin(
        phone: String = _uiState.value.enteredPhone.trim(),
        pin: String = _uiState.value.enteredPin.trim()
    ) {
        if (phone.isBlank() || phone.length < 10) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid 10-digit mobile number")
            return
        }
        if (pin.isBlank() || pin.length < 4) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your 4-digit security PIN")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAuthenticating = true, errorMessage = null)

            // Look up salesman/owner by phone in repository (Room + cloud sync)
            var user = repository.getSalesmanByPhone(phone)

            // Fallback check for default owner/salesmen credentials if DB was fresh
            if (user == null) {
                if (phone == "9847000001" || phone == "9876543210") {
                    user = SalesmanUser(
                        id = phone,
                        name = "Syed Ibrahim (Admin)",
                        phone = phone,
                        pin = "1980",
                        role = UserRole.OWNER.name
                    )
                    repository.saveSalesman(user, "System")
                } else if (phone.startsWith("984700000")) {
                    val index = phone.takeLast(1)
                    user = SalesmanUser(
                        id = phone,
                        name = "Salesman $index",
                        phone = phone,
                        pin = "1234",
                        role = UserRole.SALESMAN.name
                    )
                    repository.saveSalesman(user, "System")
                }
            }

            if (user != null) {
                if (user.pin != pin) {
                    _uiState.value = _uiState.value.copy(
                        isAuthenticating = false,
                        errorMessage = "Incorrect PIN. Please check and try again."
                    )
                    return@launch
                }

                val isOwner = user.role.equals(UserRole.OWNER.name, ignoreCase = true)
                if (!isOwner) {
                    if (!user.isActive) {
                        _uiState.value = _uiState.value.copy(
                            isAuthenticating = false,
                            errorMessage = "Account is inactive. Contact admin Syed Ibrahim."
                        )
                        return@launch
                    }
                    if (user.isOnLeave) {
                        _uiState.value = _uiState.value.copy(
                            isAuthenticating = false,
                            errorMessage = "You are marked 'On Leave' for today. Contact admin Syed Ibrahim to enable sales access."
                        )
                        return@launch
                    }
                }

                // Save session permanently on this mobile device!
                sessionManager.saveSession(user)

                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    isOwnerLoggedIn = isOwner,
                    isAuthenticating = false,
                    errorMessage = null,
                    enteredPin = ""
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isAuthenticating = false,
                    errorMessage = "Account not found for mobile number $phone. Contact admin Syed Ibrahim."
                )
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _uiState.value = AuthUiState(
            currentUser = null,
            isOwnerLoggedIn = false,
            enteredPhone = "",
            enteredPin = "",
            isAutoLoggingIn = false
        )
    }

    fun quickSwitchUser(targetUser: SalesmanUser) {
        sessionManager.saveSession(targetUser)
        _uiState.value = _uiState.value.copy(
            currentUser = targetUser,
            isOwnerLoggedIn = targetUser.role.equals(UserRole.OWNER.name, ignoreCase = true),
            errorMessage = null
        )
    }
}
