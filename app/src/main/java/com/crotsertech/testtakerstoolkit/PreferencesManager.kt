package com.crotsertech.testtakerstoolkit

import android.content.Context

class PreferencesManager(context: Context) {

    private val prefs = context.getSharedPreferences(
        "com.crotsertech.testtakerstoolkit_preferences",
        Context.MODE_PRIVATE
    )

    companion object {
        const val KEY_COMPANY_NAME = "company_name"
        const val KEY_COMPANY_STREET = "company_street_address"
        const val KEY_COMPANY_CITY = "company_city"
        const val KEY_COMPANY_STATE = "company_state"
        const val KEY_COMPANY_ZIP = "company_zip"
        const val KEY_COMPANY_PHONE = "company_phone"
        const val KEY_COMPANY_EMAIL = "company_email"
        const val KEY_COMPANY_WEBSITE = "company_website"
        const val KEY_EPA_LOOKUP_ENABLED = "epa_lookup_enabled"
        const val KEY_DECLARE_BLANKS = "declare_blanks_not_tested"
        const val KEY_TESTER_INITIALS = "tester_initials"
    }

    fun saveCompanyInfo(companyInfo: CompanyInfo) {
        prefs.edit().apply {
            putString(KEY_COMPANY_NAME, companyInfo.name)
            putString(KEY_COMPANY_STREET, companyInfo.streetAddress)
            putString(KEY_COMPANY_CITY, companyInfo.city)
            putString(KEY_COMPANY_STATE, companyInfo.state)
            putString(KEY_COMPANY_ZIP, companyInfo.zip)
            putString(KEY_COMPANY_PHONE, companyInfo.phone)
            putString(KEY_COMPANY_EMAIL, companyInfo.email)
            putString(KEY_COMPANY_WEBSITE, companyInfo.website)
            apply()
        }
    }

    fun getCompanyInfo(): CompanyInfo {
        return CompanyInfo(
            name = prefs.getString(KEY_COMPANY_NAME, "") ?: "",
            streetAddress = prefs.getString(KEY_COMPANY_STREET, "") ?: "",
            city = prefs.getString(KEY_COMPANY_CITY, "") ?: "",
            state = prefs.getString(KEY_COMPANY_STATE, "") ?: "",
            zip = prefs.getString(KEY_COMPANY_ZIP, "") ?: "",
            phone = prefs.getString(KEY_COMPANY_PHONE, "") ?: "",
            email = prefs.getString(KEY_COMPANY_EMAIL, "") ?: "",
            website = prefs.getString(KEY_COMPANY_WEBSITE, "") ?: ""
        )
    }

    fun saveEpaLookupEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_EPA_LOOKUP_ENABLED, enabled).apply()
    }

    fun isEpaLookupEnabled(): Boolean {
        return prefs.getBoolean(KEY_EPA_LOOKUP_ENABLED, false)
    }

    fun saveDeclareBlanksEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DECLARE_BLANKS, enabled).apply()
    }

    fun isDeclareBlanksEnabled(): Boolean {
        return prefs.getBoolean(KEY_DECLARE_BLANKS, true)
    }

    fun saveTesterInitials(initials: String) {
        prefs.edit().putString(KEY_TESTER_INITIALS, initials).apply()
    }

    fun getTesterInitials(): String {
        return prefs.getString(KEY_TESTER_INITIALS, "") ?: ""
    }

    fun getEpaSystemNumber(zipCode: String, enabled: Boolean): String {
        // This is a placeholder as the logic was removed.
        // You can re-implement your EPA lookup logic here if needed.
        if (enabled) {
            return "Placeholder EPA # for $zipCode"
        }
        return "N/A"
    }
}
