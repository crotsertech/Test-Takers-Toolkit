package com.crotsertech.testtakerstoolkit

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("TestTakersToolkitPrefs", Context.MODE_PRIVATE)

    fun saveCompanyInfo(companyInfo: CompanyInfo) {
        prefs.edit().apply {
            putString("company_name", companyInfo.name)
            putString("company_street_address", companyInfo.streetAddress)
            putString("company_city", companyInfo.city)
            putString("company_zip", companyInfo.zip)
            putString("company_phone", companyInfo.phone)
            putString("company_email", companyInfo.email)
            putString("company_website", companyInfo.website)
            apply()
        }
    }

    fun getCompanyInfo(): CompanyInfo {
        return CompanyInfo(
            name = prefs.getString("company_name", "") ?: "",
            streetAddress = prefs.getString("company_street_address", "") ?: "",
            city = prefs.getString("company_city", "") ?: "",
            zip = prefs.getString("company_zip", "") ?: "",
            phone = prefs.getString("company_phone", "") ?: "",
            email = prefs.getString("company_email", "") ?: "",
            website = prefs.getString("company_website", "") ?: ""
        )
    }

    fun saveEpaLookupEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("epa_lookup_enabled", enabled).apply()
    }

    fun isEpaLookupEnabled(): Boolean {
        return prefs.getBoolean("epa_lookup_enabled", true) // Default to true
    }
    fun saveDeclareBlanksEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("declare_blanks_enabled", enabled).apply()
    }

    fun isDeclareBlanksEnabled(): Boolean {
        return prefs.getBoolean("declare_blanks_enabled", true)
    }
    fun getEpaSystemNumber(zipCode: String, enabled: Boolean): String {
        if (!enabled) {
            return "N/A (Lookup Disabled)"
        }
        return when (zipCode.take(5)) {
            "62701", "62702", "62703", "62704", "62705", "62706",
            "62707", "62711", "62712", "62715", "62716",
            "62719", "62721", "62722", "62723", "62726"
                -> "IL1370100" // Springfield
            "62629" -> "IL1370010" // Chatham
            "62615" -> "IL1370020" // Auburn
            "62690" -> "IL1370030" // Virden
            "62640" -> "IL1370040" // Girard
            "62069" -> "IL1130010" // Mount Olive
            "62088" -> "IL1350020" // Staunton
            "62049" -> "IL1190650" // Hillsboro
            "62056" -> "IL1350010" // Litchfield
            "62521", "62522", "62523", "62524", "62525", "62526"
                -> "IL1150250" // Decatur
            "62549" -> "IL1150400" // Mount Zion
            "62565" -> "IL0211100 - Shelbyville Water Department" // Shelbyville
            "62468" -> "IL0211100 - Shelbyville Water Department" // Stewardson & Strasburg (supplied by Shelbyville)
            "62447" -> "IL0490550" // Neoga
            "62401" -> "IL0490250" // Effingham
            "62467" -> "IL0490700" // Teutopolis
            "62568" -> "IL0211200" // Taylorville
            "62557" -> "IL0210850" // Pana
            "62539" -> "IL1370050" // Kincaid
            "61701", "61704", "61705" -> "IL1130150" // Bloomington
            "61761" -> "IL1130950" // Normal
            "61554", "61550" -> "IL1814620 - Morton Water Department" // Morton
            "61571" -> "IL1814720" // Washington
            "61611" -> "IL1814320 - East Peoria Water Department" // East Peoria
            "61602", "61603", "61604", "61605", "61606", "61614", "61615"
                -> "IL1430010 - Illinois American Peoria" // Peoria (Illinois American Water)
            "61616" -> "IL1434750 - Greater Peoria Sanitary District" // Peoria Heights
            // "61550" -> "IL1430010" // Metamora (supplied by Illinois American Water)
            "61530" -> "IL2030200 - Eureka Public Works" // Eureka
            "61738" -> "IL1130550" // El Paso
            "61725" -> "IL1130250" // Chenoa
            "61764" -> "IL1054500" // Pontiac
            "61739" -> "IL1054150" // Fairbury
            "61727" -> "IL0540010" // Chatsworth
            "61741" -> "IL0540020" // Forrest
            "61729" -> "IL1054100" // Cullom
            "61820", "61821", "61822" -> "IL0190010 - Illinois American Champaign-Urbana" // Champaign & Urbana (Illinois American Water)
            "61874" -> "IL0190010 - Illinois American Champaign-Urbana" // Savoy (supplied by Illinois American Water)
            "61853" -> "IL0190010" // Mahomet (supplied by Illinois American Water)
            "61801", "61802" -> "IL0190010" // Rantoul (supplied by Illinois American Water)
            "61877" -> "IL1834900" // Tolono
            "61953" -> "IL1450010" // Tuscola
            "61911" -> "IL0394050" // Arcola
            "61938" -> "IL0394450" // Mattoon
            "61920" -> "IL0310010" // Charleston
            "61832", "61834" -> "IL1830010" // Danville (Aqua Illinois)
            "61883" -> "IL1830010" // Tilton (supplied by Aqua Illinois)
            "62650" -> "IL0170550" // Jacksonville
            "62681" -> "IL0170850" // South Jacksonville
            "62656" -> "IL1370060" // Lincoln
            "61726" -> "IL0374200" // Clinton
            "61830" -> "IL1450010" // Monticello
            else -> "Water System Number Unavailable or Not in Database."
        }
    }
}
