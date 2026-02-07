package com.crotsertech.testtakerstoolkit

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView // <<< ADD THIS IMPORT
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_help)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Setup UI components
        setupVersionNumber()
        setupClickableLinks()
    }

    private fun setupVersionNumber() {
        val tvVersionNumber = findViewById<TextView>(R.id.tv_version_number)
        try {
            val pInfo: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }
            val version = pInfo.versionName
            tvVersionNumber.text = "Version $version"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            tvVersionNumber.text = "Version unknown"
        }
    }

    private fun setupClickableLinks() {
        // Find all clickable views by their unique IDs
        val githubLink = findViewById<TextView>(R.id.tvGitHubLink)
        val coffeeLink = findViewById<TextView>(R.id.tvCoffeeLink)
        // Correctly find the ImageView by its ID from the XML
        val attributionLink = findViewById<ImageView>(R.id.ivAttribution)

        // Set click listener for the GitHub link
        githubLink.setOnClickListener {
            openUrl("https://github.com/crotsertech/Test-Takers-Toolkit")
        }

        // Set click listener for the coffee link
        coffeeLink.setOnClickListener {
            openUrl("https://ko-fi.com/crotsertech")
        }

        // Set click listener for the attribution badge
        attributionLink.setOnClickListener {
            openUrl("https://github.com/crotsertech")
        }
    }

    // Helper function to open a URL in the browser
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
