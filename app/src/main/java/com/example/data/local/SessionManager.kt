package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.SalesmanUser
import com.example.data.model.UserRole

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveSession(user: SalesmanUser) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_USER_NAME, user.name)
            .putString(KEY_USER_PHONE, user.phone)
            .putString(KEY_USER_PIN, user.pin)
            .putString(KEY_USER_ROLE, user.role)
            .putBoolean(KEY_USER_ACTIVE, user.isActive)
            .putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    fun getSavedSession(): SalesmanUser? {
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (!isLoggedIn) return null

        val id = prefs.getString(KEY_USER_ID, null) ?: return null
        val name = prefs.getString(KEY_USER_NAME, "User") ?: "User"
        val phone = prefs.getString(KEY_USER_PHONE, id) ?: id
        val pin = prefs.getString(KEY_USER_PIN, "") ?: ""
        val role = prefs.getString(KEY_USER_ROLE, UserRole.SALESMAN.name) ?: UserRole.SALESMAN.name
        val isActive = prefs.getBoolean(KEY_USER_ACTIVE, true)

        return SalesmanUser(
            id = id,
            name = name,
            phone = phone,
            pin = pin,
            role = role,
            isActive = isActive
        )
    }

    fun isOwner(): Boolean {
        val user = getSavedSession() ?: return false
        return user.role.equals(UserRole.OWNER.name, ignoreCase = true)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "mj_garments_user_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_PIN = "user_pin"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_ACTIVE = "user_active"
        private const val KEY_LOGIN_TIMESTAMP = "login_time"
    }
}
