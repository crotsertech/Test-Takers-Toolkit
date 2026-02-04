package com.crotsertech.testtakerstoolkit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        // Setup the toolbar with a back button
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_help)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // This is the modern and recommended way to handle the toolbar's back arrow
        toolbar.setNavigationOnClickListener {
            // This properly handles back navigation
            onBackPressedDispatcher.onBackPressed()
        }

        // Call the functions to set up the page content
        setVersionNumber()
        setupClickListeners()
    }

    /**
     * Retrieves the app's version name from the package manager
     * and displays it in the corresponding TextView.
     */
    private fun setVersionNumber() {
        val versionTextView = findViewById<TextView>(R.id.tv_version_number)
        try {
            // Get package info to access the versionName defined in build.gradle
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            versionTextView.text = "Version $versionName"
        } catch (e: Exception) {
            // Safely handle cases where the version name can't be found
            e.printStackTrace()
            versionTextView.text = "Version not found"
        }
    }

    /**
     * Sets up click handlers for interactive elements on the page.
     */
    private fun setupClickListeners() {
        // GitHub link in the "Support" card
        findViewById<TextView>(R.id.tvGitHubLink).setOnClickListener {
            openUrlInBrowser("https://github.com/crotsertech/Test-Takers-Toolkit")
        }

        // Attribution link at the very bottom
        findViewById<TextView>(R.id.tvAttributionHelp).setOnClickListener {
            openUrlInBrowser("https://github.com/crotsertech")
        }
    }

    /**
     * Opens the provided URL in the device's default web browser.
     */
    private fun openUrlInBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open browser", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
