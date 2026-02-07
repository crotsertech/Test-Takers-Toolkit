package com.crotsertech.testtakerstoolkit

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream

class SettingsActivity : AppCompatActivity(), SignatureDialogFragment.SignatureDialogListener {

    private lateinit var prefs: SharedPreferences
    private lateinit var etCompanyName: TextInputEditText
    private lateinit var etCompanyStreetAddress: TextInputEditText
    private lateinit var etCompanyCity: TextInputEditText
    private lateinit var etCompanyZip: TextInputEditText
    private lateinit var etCompanyPhone: TextInputEditText
    private lateinit var etCompanyEmail: TextInputEditText
    private lateinit var etCompanyWebsite: TextInputEditText
    private lateinit var etTesterInitials: TextInputEditText
    private lateinit var ivSignaturePreview: ImageView
    private lateinit var btnCaptureSignature: Button
    private lateinit var switchDeclareBlanks: SwitchMaterial
    private lateinit var btnSaveSettings: Button

    private val signatureFile: File by lazy {
        File(filesDir, "signature.png")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        prefs = getSharedPreferences("com.crotsertech.testtakerstoolkit_preferences", MODE_PRIVATE)

        initializeViews()
        loadSettings()

        btnSaveSettings.setOnClickListener {
            saveSettings()
        }

        btnCaptureSignature.setOnClickListener {
            SignatureDialogFragment().show(supportFragmentManager, "SignatureDialog")
        }
    }

    private fun initializeViews() {
        etCompanyName = findViewById(R.id.etCompanyName)
        etCompanyStreetAddress = findViewById(R.id.etCompanyStreetAddress)
        etCompanyCity = findViewById(R.id.etCompanyCity)
        etCompanyZip = findViewById(R.id.etCompanyZip)
        etCompanyPhone = findViewById(R.id.etCompanyPhone)
        etCompanyEmail = findViewById(R.id.etCompanyEmail)
        etCompanyWebsite = findViewById(R.id.etCompanyWebsite)
        etTesterInitials = findViewById(R.id.etTesterInitials)
        ivSignaturePreview = findViewById(R.id.ivSignaturePreview)
        btnCaptureSignature = findViewById(R.id.btnCaptureSignature)
        switchDeclareBlanks = findViewById(R.id.switchDeclareBlanks)
        btnSaveSettings = findViewById(R.id.btnSaveSettings)
    }

    private fun loadSettings() {
        etCompanyName.setText(prefs.getString("company_name", ""))
        etCompanyStreetAddress.setText(prefs.getString("company_street_address", ""))

        val city = prefs.getString("company_city", "")
        val state = prefs.getString("company_state", "")
        val cityState = if (city?.isNotEmpty() == true && state?.isNotEmpty() == true) {
            "$city, $state"
        } else {
            city ?: ""
        }
        etCompanyCity.setText(cityState)

        etCompanyZip.setText(prefs.getString("company_zip", ""))
        etCompanyPhone.setText(prefs.getString("company_phone", ""))
        etCompanyEmail.setText(prefs.getString("company_email", ""))
        etCompanyWebsite.setText(prefs.getString("company_website", ""))
        etTesterInitials.setText(prefs.getString("tester_initials", ""))
        switchDeclareBlanks.isChecked = prefs.getBoolean("declare_blanks_not_tested", true)

        if (signatureFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(signatureFile.absolutePath)
            ivSignaturePreview.setImageBitmap(bitmap)
        }
    }

    private fun saveSettings() {
        val editor = prefs.edit()

        val cityStateInput = etCompanyCity.text.toString().trim()
        val parts = cityStateInput.split(",").map { it.trim() }
        val city = parts.getOrNull(0) ?: ""
        val state = parts.getOrNull(1) ?: ""

        editor.putString("company_name", etCompanyName.text.toString().trim())
        editor.putString("company_street_address", etCompanyStreetAddress.text.toString().trim())
        editor.putString("company_city", city)
        editor.putString("company_state", state)
        editor.putString("company_zip", etCompanyZip.text.toString().trim())
        editor.putString("company_phone", etCompanyPhone.text.toString().trim())
        editor.putString("company_email", etCompanyEmail.text.toString().trim())
        editor.putString("company_website", etCompanyWebsite.text.toString().trim())
        editor.putString("tester_initials", etTesterInitials.text.toString().trim())
        editor.putBoolean("declare_blanks_not_tested", switchDeclareBlanks.isChecked)

        editor.apply()

        Snackbar.make(findViewById(android.R.id.content), "Settings saved successfully", Snackbar.LENGTH_SHORT).show()
    }

    override fun onSignatureSaved(signatureBitmap: Bitmap) {
        try {
            FileOutputStream(signatureFile).use { out ->
                signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            ivSignaturePreview.setImageBitmap(signatureBitmap)
            Snackbar.make(findViewById(android.R.id.content), "Signature saved", Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Snackbar.make(findViewById(android.R.id.content), "Error saving signature", Snackbar.LENGTH_LONG).show()
        }
    }
}
