package com.crotsertech.testtakerstoolkit

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
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
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var pdfGenerator: PdfGenerator

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
    private lateinit var etTesterInitials: TextInputEditText
    private lateinit var etHardness: TextInputEditText
    private lateinit var etTds: TextInputEditText
    private lateinit var etPh: TextInputEditText
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

        initializeViews()
        setupListeners()
        setupDatePicker()
    }

    private fun initializeViews() {
        etName = findViewById(R.id.etName)
        etStreetAddress = findViewById(R.id.etStreetAddress)
        etCity = findViewById(R.id.etCity)
        etState = findViewById(R.id.etState)
        etZip = findViewById(R.id.etZip)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        rgWaterSource = findViewById(R.id.rgWaterSource)
        tvEpaNumber = findViewById(R.id.tvEpaNumber)

        etDateTested = findViewById(R.id.etDateTested)
        etSampleLocation = findViewById(R.id.etSampleLocation)
        etTesterInitials = findViewById(R.id.etTesterInitials)
        etHardness = findViewById(R.id.etHardness)
        etTds = findViewById(R.id.etTds)
        etPh = findViewById(R.id.etPh)
        etIron = findViewById(R.id.etIron)
        etAmmonia = findViewById(R.id.etAmmonia)
        etNitrates = findViewById(R.id.etNitrates)
        etManganese = findViewById(R.id.etManganese)
        etTannin = findViewById(R.id.etTannin)
        etArsenic = findViewById(R.id.etArsenic)
        etChromium6 = findViewById(R.id.etChromium6)
        etGlyphosate = findViewById(R.id.etGlyphosate)
        etLead = findViewById(R.id.etLead)
        etNotes = findViewById(R.id.etNotes)
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
        findViewById<MaterialButton>(R.id.btnGeneratePdf).setOnClickListener {
            checkPermissionAndGeneratePdf()
        }
        findViewById<MaterialButton>(R.id.btnClear).setOnClickListener {
            showClearConfirmation()
        }
        findViewById<TextView>(R.id.tvAttribution).setOnClickListener {
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

    private fun openUrlInBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
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
        val issuesUrl = "https://github.com/crotsertech/Test-Takers-Toolkit/issues"
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            // Temporarily Disabled
            /*
            R.id.action_bug_report -> {
                openUrlInBrowser("$issuesUrl/new?template=bug_report.md")
                true
            }
            R.id.action_feature_request -> {
                openUrlInBrowser("$issuesUrl/new?template=feature_request.md")
                true
            }*/
            R.id.action_help -> {
                startActivity(Intent(this, HelpActivity::class.java))
                true
            } //
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
        val isMunicipal = findViewById<MaterialRadioButton>(R.id.rbMunicipal).isChecked
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
        if (etName.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter customer name", Toast.LENGTH_SHORT).show()
            etName.error = "Customer name is required"
            return
        }

        val companyInfo = preferencesManager.getCompanyInfo()
        if (companyInfo.name.isEmpty()) {
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
                else -> WaterSource.MUNICIPAL
            }

            val isEpaLookupEnabled = preferencesManager.isEpaLookupEnabled()
            val epaNumber = if (waterSource == WaterSource.MUNICIPAL) {
                preferencesManager.getEpaSystemNumber(etZip.text.toString(), enabled = isEpaLookupEnabled)
            } else {
                "N/A"
            }

            val customerInfo = CustomerInfo(
                name = etName.text.toString(),
                streetAddress = etStreetAddress.text.toString(),
                city = etCity.text.toString(),
                state = etState.text.toString(),
                zip = etZip.text.toString(),
                phone = etPhone.text.toString(),
                email = etEmail.text.toString(),
                waterSource = waterSource,
                epaSystemNumber = epaNumber
            )

            val testResults = TestResults(
                dateTested = etDateTested.text.toString(),
                sampleLocation = etSampleLocation.text.toString(),
                testerInitials = etTesterInitials.text.toString(),
                hardness = etHardness.text.toString(),
                tds = etTds.text.toString(),
                ph = etPh.text.toString(),
                iron = etIron.text.toString(),
                ammonia = etAmmonia.text.toString(),
                nitrates = etNitrates.text.toString(),
                manganese = etManganese.text.toString(),
                tannin = etTannin.text.toString(),
                arsenic = etArsenic.text.toString(),
                chromium6 = etChromium6.text.toString(),
                glyphosate = etGlyphosate.text.toString(),
                lead = etLead.text.toString(),
                notes = etNotes.text.toString()
            )

            pdfGenerator.generatePdf(companyInfo, customerInfo, testResults)
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
        etTesterInitials.text?.clear()
        etHardness.text?.clear()
        etTds.text?.clear()
        etPh.text?.clear()
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
