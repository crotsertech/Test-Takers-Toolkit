package com.crotsertech.testtakerstoolkit

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
// import androidx.appcompat.app.AppCompatActivityimport
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText

class SettingsActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var etCompanyName: TextInputEditText
    private lateinit var etCompanyStreetAddress: TextInputEditText
    private lateinit var etCompanyCity: TextInputEditText
    private lateinit var etCompanyState: AutoCompleteTextView
    private lateinit var etCompanyZip: TextInputEditText
    private lateinit var etCompanyPhone: TextInputEditText
    private lateinit var etCompanyEmail: TextInputEditText
    private lateinit var etCompanyWebsite: TextInputEditText
    private lateinit var switchEpaLookup: SwitchMaterial
    private lateinit var switchDeclareBlanks: SwitchMaterial
    private lateinit var btnSaveSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        preferencesManager = PreferencesManager(this)
        initializeViews()
        setupStateDropdown()
        loadSettings()

        btnSaveSettings.setOnClickListener {
            saveSettings()
        }
    }

    private fun initializeViews() {
        etCompanyName = findViewById(R.id.etCompanyName)
        etCompanyStreetAddress = findViewById(R.id.etCompanyStreetAddress)
        etCompanyCity = findViewById(R.id.etCompanyCity)
        etCompanyState = findViewById(R.id.etCompanyState)
        etCompanyZip = findViewById(R.id.etCompanyZip)
        etCompanyPhone = findViewById(R.id.etCompanyPhone)
        etCompanyEmail = findViewById(R.id.etCompanyEmail)
        etCompanyWebsite = findViewById(R.id.etCompanyWebsite)
        switchEpaLookup = findViewById(R.id.switchEpaLookup)
        switchDeclareBlanks = findViewById(R.id.switchDeclareBlanks)
        btnSaveSettings = findViewById(R.id.btnSaveSettings)
    }

    private fun setupStateDropdown() {
        val states = resources.getStringArray(R.array.us_states)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, states)
        etCompanyState.setAdapter(adapter)
    }

    private fun loadSettings() {
        val companyInfo = preferencesManager.getCompanyInfo()
        etCompanyName.setText(companyInfo.name)
        etCompanyStreetAddress.setText(companyInfo.streetAddress)
        etCompanyCity.setText(companyInfo.city)

        etCompanyState.setText(companyInfo.state, false)

        etCompanyZip.setText(companyInfo.zip)
        etCompanyPhone.setText(companyInfo.phone)
        etCompanyEmail.setText(companyInfo.email)
        etCompanyWebsite.setText(companyInfo.website)

        switchEpaLookup.isChecked = preferencesManager.isEpaLookupEnabled()
        switchDeclareBlanks.isChecked = preferencesManager.isDeclareBlanksEnabled()
    }

    private fun saveSettings() {
        val companyInfo = CompanyInfo(
            name = etCompanyName.text.toString(),
            streetAddress = etCompanyStreetAddress.text.toString(),
            city = etCompanyCity.text.toString(),
            state = etCompanyState.text.toString(),
            zip = etCompanyZip.text.toString(),
            phone = etCompanyPhone.text.toString(),
            email = etCompanyEmail.text.toString(),
            website = etCompanyWebsite.text.toString()
        )
        preferencesManager.saveCompanyInfo(companyInfo)
        preferencesManager.saveEpaLookupEnabled(switchEpaLookup.isChecked)
        preferencesManager.saveDeclareBlanksEnabled(switchDeclareBlanks.isChecked)

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
        finish()
    }
}
