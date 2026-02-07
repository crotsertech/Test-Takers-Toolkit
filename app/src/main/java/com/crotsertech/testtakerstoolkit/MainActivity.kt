package com.crotsertech.testtakerstoolkit

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var pdfGenerator: PdfGenerator

    // --- UI Views ---
    private lateinit var etName: TextInputEditText
    private lateinit var etStreetAddress: TextInputEditText
    private lateinit var etCity: TextInputEditText
    private lateinit var etState: TextInputEditText
    private lateinit var etZip: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var rgWaterSource: RadioGroup
    private lateinit var tvEpaNumber: TextView

    private lateinit var etDateTested: TextInputEditText
    private lateinit var etSampleLocation: TextInputEditText
    private lateinit var etHardness: TextInputEditText
    private lateinit var etTds: TextInputEditText
    private lateinit var etPh: TextInputEditText
    private lateinit var etChlorine: TextInputEditText
    private lateinit var etIron: TextInputEditText
    private lateinit var etAmmonia: TextInputEditText
    private lateinit var etNitrates: TextInputEditText
    private lateinit var etManganese: TextInputEditText
    private lateinit var etTannin: TextInputEditText
    private lateinit var etArsenic: TextInputEditText
    private lateinit var etChromium6: TextInputEditText
    private lateinit var etGlyphosate: TextInputEditText
    private lateinit var etLead: TextInputEditText
    private lateinit var etNotes: TextInputEditText

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            generatePdf()
        } else {
            Toast.makeText(this, "Permission denied. Cannot save PDF.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        preferencesManager = PreferencesManager(this)
        pdfGenerator = PdfGenerator(this)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        try {
            initializeViews()
            setupListeners()
            setupDatePicker()
        } catch (e: IllegalStateException) {
            showInitializationErrorDialog(e.message)
        }
    }

    private fun showInitializationErrorDialog(errorMessage: String?) {
        AlertDialog.Builder(this)
            .setTitle("Initialization Error")
            .setMessage("A required UI component could not be found. The layout may be corrupt. Please report this issue.\n\nError: $errorMessage")
            .setPositiveButton("OK") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun <T : View> safeFindViewById(id: Int): T {
        return findViewById(id) ?: throw IllegalStateException("View with ID $id not found in the current layout.")
    }

    private fun initializeViews() {
        etName = safeFindViewById(R.id.etName)
        etStreetAddress = safeFindViewById(R.id.etStreetAddress)
        etCity = safeFindViewById(R.id.etCity)
        etState = safeFindViewById(R.id.etState)
        etZip = safeFindViewById(R.id.etZip)
        etPhone = safeFindViewById(R.id.etPhone)
        etEmail = safeFindViewById(R.id.etEmail)
        rgWaterSource = safeFindViewById(R.id.rgWaterSource)
        tvEpaNumber = safeFindViewById(R.id.tvEpaNumber)

        etDateTested = safeFindViewById(R.id.etDateTested)
        etSampleLocation = safeFindViewById(R.id.etSampleLocation)
        etHardness = safeFindViewById(R.id.etHardness)
        etTds = safeFindViewById(R.id.etTds)
        etPh = safeFindViewById(R.id.etPh)
        etChlorine = safeFindViewById(R.id.etChlorine)
        etIron = safeFindViewById(R.id.etIron)
        etAmmonia = safeFindViewById(R.id.etAmmonia)
        etNitrates = safeFindViewById(R.id.etNitrates)
        etManganese = safeFindViewById(R.id.etManganese)
        etTannin = safeFindViewById(R.id.etTannin)
        etArsenic = safeFindViewById(R.id.etArsenic)
        etChromium6 = safeFindViewById(R.id.etChromium6)
        etGlyphosate = safeFindViewById(R.id.etGlyphosate)
        etLead = safeFindViewById(R.id.etLead)
        etNotes = safeFindViewById(R.id.etNotes)
    }

    private fun setupListeners() {
        etZip.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updateEpaNumber()
            }
        }
        rgWaterSource.setOnCheckedChangeListener { _, _ ->
            updateEpaNumber()
        }
        safeFindViewById<MaterialButton>(R.id.btnGeneratePdf).setOnClickListener {
            checkPermissionAndGeneratePdf()
        }
        safeFindViewById<MaterialButton>(R.id.btnClear).setOnClickListener {
            showClearConfirmation()
        }
        safeFindViewById<TextView>(R.id.tvAttribution).setOnClickListener {
            openGitHubLink()
        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_help -> {
                startActivity(Intent(this, HelpActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupDatePicker() {
        etDateTested.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                    etDateTested.setText(sdf.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateEpaNumber() {
        val zipCode = etZip.text.toString()
        val isMunicipal = safeFindViewById<MaterialRadioButton>(R.id.rbMunicipal).isChecked
        val isEpaLookupEnabled = preferencesManager.isEpaLookupEnabled()

        if (isMunicipal && zipCode.length == 5 && isEpaLookupEnabled) {
            val epaNumber = preferencesManager.getEpaSystemNumber(zipCode, enabled = true)
            tvEpaNumber.text = "IL EPA System Number: $epaNumber"
            tvEpaNumber.visibility = View.VISIBLE
        } else {
            tvEpaNumber.visibility = View.GONE
        }
    }

    private fun checkPermissionAndGeneratePdf() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            generatePdf()
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    generatePdf()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun generatePdf() {
        if (etName.text.toString().isBlank()) {
            etName.error = "Customer name is required"
            Toast.makeText(this, "Please enter customer name", Toast.LENGTH_SHORT).show()
            return
        }

        val companyInfo = preferencesManager.getCompanyInfo()
        if (companyInfo.name.isBlank()) {
            AlertDialog.Builder(this)
                .setTitle("Company Info Required")
                .setMessage("Please set up your company information in Settings before generating reports.")
                .setPositiveButton("Go to Settings") { _, _ ->
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                .setNegativeButton("Cancel", null)
                .show()
            return
        }

        try {
            val waterSource = when (rgWaterSource.checkedRadioButtonId) {
                R.id.rbPrivateWell -> WaterSource.PRIVATE_WELL
                R.id.rbCommunityWell -> WaterSource.COMMUNITY_WELL
                // R.id.rbOther is not in XML, so we default to MUNICIPAL
                else -> WaterSource.MUNICIPAL
            }

            val isEpaLookupEnabled = preferencesManager.isEpaLookupEnabled()
            val epaNumber = if (waterSource == WaterSource.MUNICIPAL) {
                preferencesManager.getEpaSystemNumber(etZip.text.toString(), enabled = isEpaLookupEnabled)
            } else {
                "N/A"
            }

            val customerInfo = CustomerInfo(
                name = etName.text.toString().trim(),
                streetAddress = etStreetAddress.text.toString().trim(),
                city = etCity.text.toString().trim(),
                state = etState.text.toString().trim(),
                zip = etZip.text.toString().trim(),
                phone = etPhone.text.toString().trim(),
                email = etEmail.text.toString().trim(),
                waterSource = waterSource,
                epaSystemNumber = epaNumber
            )

            val testResults = TestResults(
                dateTested = etDateTested.text.toString().trim(),
                sampleLocation = etSampleLocation.text.toString().trim(),
                hardness = etHardness.text.toString().trim(),
                tds = etTds.text.toString().trim(),
                ph = etPh.text.toString().trim(),
                chlorine = etChlorine.text.toString().trim(),
                iron = etIron.text.toString().trim(),
                ammonia = etAmmonia.text.toString().trim(),
                nitrates = etNitrates.text.toString().trim(),
                manganese = etManganese.text.toString().trim(),
                tannin = etTannin.text.toString().trim(),
                arsenic = etArsenic.text.toString().trim(),
                chromium6 = etChromium6.text.toString().trim(),
                glyphosate = etGlyphosate.text.toString().trim(),
                lead = etLead.text.toString().trim(),
                notes = etNotes.text.toString().trim()
            )

            // Get initials and signature from settings
            val testerInitials = preferencesManager.getTesterInitials()
            val signatureFile = File(filesDir, "signature.png")
            val signatureBitmap = if (signatureFile.exists()) {
                BitmapFactory.decodeFile(signatureFile.absolutePath)
            } else {
                null
            }

            pdfGenerator.generatePdf(companyInfo, customerInfo, testResults, testerInitials, signatureBitmap)
            Toast.makeText(this, "PDF saved to Documents folder", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun showClearConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Danger Zone")
            .setMessage("Are you sure you want to clear all fields?\nThis action CANNOT be undone.")
            .setPositiveButton("YES, I'm sure") { _, _ -> clearAllFields() }
            .setNegativeButton("NO, Take me back", null)
            .show()
    }

    private fun clearAllFields() {
        etName.text?.clear()
        etStreetAddress.text?.clear()
        etCity.text?.clear()
        etState.text?.clear()
        etZip.text?.clear()
        etPhone.text?.clear()
        etEmail.text?.clear()
        rgWaterSource.check(R.id.rbMunicipal)
        etDateTested.text?.clear()
        etSampleLocation.text?.clear()
        etHardness.text?.clear()
        etTds.text?.clear()
        etPh.text?.clear()
        etChlorine.text?.clear()
        etIron.text?.clear()
        etAmmonia.text?.clear()
        etNitrates.text?.clear()
        etManganese.text?.clear()
        etTannin.text?.clear()
        etArsenic.text?.clear()
        etChromium6.text?.clear()
        etGlyphosate.text?.clear()
        etLead.text?.clear()
        etNotes.text?.clear()
        Toast.makeText(this, "All fields cleared", Toast.LENGTH_SHORT).show()
    }
}
