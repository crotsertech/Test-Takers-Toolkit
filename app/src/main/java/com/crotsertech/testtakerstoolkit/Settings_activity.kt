package com.crotsertech.testtakerstoolkit

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

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var etCompanyName: TextInputEditText
    private lateinit var etCompanyStreetAddress: TextInputEditText
    private lateinit var etCompanyCity: TextInputEditText
    private lateinit var etCompanyState: TextInputEditText
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

        preferencesManager = PreferencesManager(this)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

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
        etCompanyState = findViewById(R.id.etCompanyState)
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
        val companyInfo = preferencesManager.getCompanyInfo()
        etCompanyName.setText(companyInfo.name)
        etCompanyStreetAddress.setText(companyInfo.streetAddress)
        etCompanyCity.setText(companyInfo.city)
        etCompanyState.setText(companyInfo.state)
        etCompanyZip.setText(companyInfo.zip)
        etCompanyPhone.setText(companyInfo.phone)
        etCompanyEmail.setText(companyInfo.email)
        etCompanyWebsite.setText(companyInfo.website)
        etTesterInitials.setText(preferencesManager.getTesterInitials())
        switchDeclareBlanks.isChecked = preferencesManager.isDeclareBlanksEnabled()

        if (signatureFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(signatureFile.absolutePath)
            ivSignaturePreview.setImageBitmap(bitmap)
        }
    }

    private fun saveSettings() {
        val companyInfo = CompanyInfo(
            name = etCompanyName.text.toString().trim(),
            streetAddress = etCompanyStreetAddress.text.toString().trim(),
            city = etCompanyCity.text.toString().trim(),
            state = etCompanyState.text.toString().trim(),
            zip = etCompanyZip.text.toString().trim(),
            phone = etCompanyPhone.text.toString().trim(),
            email = etCompanyEmail.text.toString().trim(),
            website = etCompanyWebsite.text.toString().trim()
        )
        preferencesManager.saveCompanyInfo(companyInfo)
        preferencesManager.saveTesterInitials(etTesterInitials.text.toString().trim())
        preferencesManager.saveDeclareBlanksEnabled(switchDeclareBlanks.isChecked)

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
