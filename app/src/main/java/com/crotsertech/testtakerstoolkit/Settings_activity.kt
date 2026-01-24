package com.crotsertech.testtakerstoolkit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SettingsActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        preferencesManager = PreferencesManager(this)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val etCompanyName = findViewById<TextInputEditText>(R.id.etCompanyName)
        val etCompanyStreetAddress = findViewById<TextInputEditText>(R.id.etCompanyStreetAddress)
        val etCompanyCity = findViewById<TextInputEditText>(R.id.etCompanyCity)
        val etCompanyZip = findViewById<TextInputEditText>(R.id.etCompanyZip)
        val etCompanyPhone = findViewById<TextInputEditText>(R.id.etCompanyPhone)
        val etCompanyEmail = findViewById<TextInputEditText>(R.id.etCompanyEmail)
        val etCompanyWebsite = findViewById<TextInputEditText>(R.id.etCompanyWebsite)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)

        // Load existing company info
        val companyInfo = preferencesManager.getCompanyInfo()
        etCompanyName.setText(companyInfo.name)
        etCompanyStreetAddress.setText(companyInfo.streetAddress)
        etCompanyCity.setText(companyInfo.city)
        etCompanyZip.setText(companyInfo.zip)
        etCompanyPhone.setText(companyInfo.phone)
        etCompanyEmail.setText(companyInfo.email)
        etCompanyWebsite.setText(companyInfo.website)

        btnSave.setOnClickListener {
            val newCompanyInfo = CompanyInfo(
                name = etCompanyName.text.toString(),
                streetAddress = etCompanyStreetAddress.text.toString(),
                city = etCompanyCity.text.toString(),
                zip = etCompanyZip.text.toString(),
                phone = etCompanyPhone.text.toString(),
                email = etCompanyEmail.text.toString(),
                website = etCompanyWebsite.text.toString()
            )
            preferencesManager.saveCompanyInfo(newCompanyInfo)
            Toast.makeText(this, "Company information saved", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Attribution link
        findViewById<TextView>(R.id.tvAttributionSettings).setOnClickListener {
            openGitHubLink()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun openGitHubLink() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/crotsertech"))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open browser", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}