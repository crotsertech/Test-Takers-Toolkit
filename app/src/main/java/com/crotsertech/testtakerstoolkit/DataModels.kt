package com.crotsertech.testtakerstoolkit

data class CompanyInfo(
    val name: String = "",    val streetAddress: String = "",
    val city: String = "",
    val state: String = "",
    val zip: String = "",
    val phone: String = "",
    val email: String = "",
    val website: String = ""
)

data class CustomerInfo(
    val name: String = "",
    val streetAddress: String = "",
    val city: String = "",
    val state: String = "", // FIX: Added default value
    val zip: String = "",
    val phone: String = "",
    val email: String = "",
    val waterSource: WaterSource = WaterSource.MUNICIPAL,
    val epaSystemNumber: String = ""
)

data class TestResults(
    val dateTested: String = "",
    val sampleLocation: String = "",
    // val testerInitials: String = "", // FIX: This is now correctly removed
    val hardness: String = "",
    val tds: String = "",
    val ph: String = "",
    val chlorine: String = "",
    val iron: String = "",
    val ammonia: String = "",
    val nitrates: String = "",
    val manganese: String = "",
    val tannin: String = "",
    val arsenic: String = "",
    val chromium6: String = "",
    val glyphosate: String = "",
    val lead: String = "",
    val notes: String = ""
)

enum class WaterSource(val displayName: String) {
    MUNICIPAL("Municipal Water System"),
    PRIVATE_WELL("Private Well"),
    COMMUNITY_WELL("Community Well"),
    OTHER("Other")
}
