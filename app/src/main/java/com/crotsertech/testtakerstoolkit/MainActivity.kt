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
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.activity.enableEdgeToEdge
//import androidx.compose.ui.layout.layout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.net.Uri
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var pdfGenerator: PdfGenerator

    // Customer info fields
    private lateinit var etName: TextInputEditText
    private lateinit var etStreetAddress: TextInputEditText
    private lateinit var etCity: TextInputEditText
    private lateinit var etZip: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var rgWaterSource: RadioGroup
    private lateinit var tvEpaNumber: TextView

    // Test result fields
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
        // Customer info
        etName = findViewById(R.id.etName)
        etStreetAddress = findViewById(R.id.etStreetAddress)
        etCity = findViewById(R.id.etCity)
        etZip = findViewById(R.id.etZip)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        rgWaterSource = findViewById(R.id.rgWaterSource)
        tvEpaNumber = findViewById(R.id.tvEpaNumber)

        // Test results
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
        // ZIP code listener to update EPA number
        etZip.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updateEpaNumber()
            }
        }

        // Water source listener
        rgWaterSource.setOnCheckedChangeListener { _, _ ->
            updateEpaNumber()
        }

        // Generate PDF button
        findViewById<MaterialButton>(R.id.btnGeneratePdf).setOnClickListener {
            checkPermissionAndGeneratePdf()
        }

        // Clear button
        findViewById<MaterialButton>(R.id.btnClear).setOnClickListener {
            showClearConfirmation()
        }

        // Attribution link
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // The URL for your GitHub issues page
        val issuesUrl = "https://github.com/crotsertech/Test-Takers-Toolkit/issues"

        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_bug_report -> {
                // To make it even better, you can link directly to the new issue page for bugs
                openUrlInBrowser("$issuesUrl/new?template=bug_report.md")
                true
            }
            R.id.action_feature_request -> {
                // And a direct link for feature requests
                openUrlInBrowser("$issuesUrl/new?template=feature_request.md")
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
        val isMunicipal = findViewById<com.google.android.material.radiobutton.MaterialRadioButton>(R.id.rbMunicipal).isChecked

        if (isMunicipal && zipCode.length == 5) {
            val epaNumber = preferencesManager.getEpaSystemNumber(zipCode)
            tvEpaNumber.text = "IL EPA System Number: $epaNumber"
            tvEpaNumber.visibility = TextView.VISIBLE
        } else {
            tvEpaNumber.visibility = TextView.GONE
        }
    }

    private fun checkPermissionAndGeneratePdf() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ doesn't need storage permission for Downloads
            generatePdf()
        } else {
            // Android 9 and below need storage permission
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
        // Validate required fields
        if (etName.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter customer name", Toast.LENGTH_SHORT).show()
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
            // Get water source
            val waterSource = when (rgWaterSource.checkedRadioButtonId) {
                R.id.rbPrivateWell -> WaterSource.PRIVATE_WELL
                R.id.rbCommunityWell -> WaterSource.COMMUNITY_WELL
                else -> WaterSource.MUNICIPAL
            }

            // Get EPA number if municipal
            val epaNumber = if (waterSource == WaterSource.MUNICIPAL) {
                preferencesManager.getEpaSystemNumber(etZip.text.toString())
            } else {
                ""
            }

            // Collect customer info
            val customerInfo = CustomerInfo(
                name = etName.text.toString(),
                streetAddress = etStreetAddress.text.toString(),
                city = etCity.text.toString(),
                zip = etZip.text.toString(),
                phone = etPhone.text.toString(),
                email = etEmail.text.toString(),
                waterSource = waterSource,
                epaSystemNumber = epaNumber
            )

            // Collect test results
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

            // Generate PDF
            val pdfFile = pdfGenerator.generatePdf(companyInfo, customerInfo, testResults)

            Toast.makeText(
                this,
                "PDF saved to Downloads: ${pdfFile.name}",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun showClearConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Clear Form")
            .setMessage("Are you sure you want to clear all fields?")
            .setPositiveButton("Yes") { _, _ ->
                clearAllFields()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun clearAllFields() {
        // Clear customer info
        etName.setText("")
        etStreetAddress.setText("")
        etCity.setText("")
        etZip.setText("")
        etPhone.setText("")
        etEmail.setText("")

        // Clear test results
        etDateTested.setText("")
        etSampleLocation.setText("")
        etTesterInitials.setText("")
        etHardness.setText("")
        etTds.setText("")
        etPh.setText("")
        etIron.setText("")
        etAmmonia.setText("")
        etNitrates.setText("")
        etManganese.setText("")
        etTannin.setText("")
        etArsenic.setText("")
        etChromium6.setText("")
        etGlyphosate.setText("")
        etLead.setText("")
        etNotes.setText("")

        // Reset water source to Municipal
        findViewById<com.google.android.material.radiobutton.MaterialRadioButton>(R.id.rbMunicipal).isChecked = true
        tvEpaNumber.visibility = TextView.GONE

        Toast.makeText(this, "Form cleared", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
