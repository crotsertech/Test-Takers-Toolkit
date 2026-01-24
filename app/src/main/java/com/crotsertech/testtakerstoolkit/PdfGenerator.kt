package com.crotsertech.testtakerstoolkit

import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator(private val context: Context) {

    fun generatePdf(
        companyInfo: CompanyInfo,
        customerInfo: CustomerInfo,
        testResults: TestResults
    ): File {
        // Create file in Downloads directory
        val fileName = "WaterTest_${customerInfo.name.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        val writer = PdfWriter(file)
        val pdfDoc = PdfDocument(writer)
        pdfDoc.defaultPageSize = PageSize.LETTER
        val document = Document(pdfDoc)

        // Reduce margins for more space
        document.setMargins(36f, 36f, 36f, 36f)

        val normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)
        val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
        val smallFont = 8f
        val normalFontSize = 9f
        val headerFontSize = 12f

        // Company Header - Compact
        document.add(
            Paragraph(companyInfo.name)
                .setFont(boldFont)
                .setFontSize(14f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(2f)
        )

        val companyDetails = StringBuilder()
        if (companyInfo.streetAddress.isNotEmpty()) {
            companyDetails.append(companyInfo.streetAddress)
        }
        if (companyInfo.city.isNotEmpty() || companyInfo.zip.isNotEmpty()) {
            if (companyDetails.isNotEmpty()) companyDetails.append(" • ")
            companyDetails.append("${companyInfo.city}, IL ${companyInfo.zip}")
        }
        if (companyInfo.phone.isNotEmpty()) {
            if (companyDetails.isNotEmpty()) companyDetails.append(" • ")
            companyDetails.append("Ph: ${companyInfo.phone}")
        }

        if (companyDetails.isNotEmpty()) {
            document.add(
                Paragraph(companyDetails.toString())
                    .setFont(normalFont)
                    .setFontSize(smallFont)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(2f)
            )
        }

        val contactDetails = StringBuilder()
        if (companyInfo.email.isNotEmpty()) {
            contactDetails.append(companyInfo.email)
        }
        if (companyInfo.website.isNotEmpty()) {
            if (contactDetails.isNotEmpty()) contactDetails.append(" • ")
            contactDetails.append(companyInfo.website)
        }

        if (contactDetails.isNotEmpty()) {
            document.add(
                Paragraph(contactDetails.toString())
                    .setFont(normalFont)
                    .setFontSize(smallFont)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(8f)
            )
        } else {
            document.add(Paragraph("").setMarginBottom(8f))
        }

        // Title
        document.add(
            Paragraph("Water Quality Test Report")
                .setFont(boldFont)
                .setFontSize(headerFontSize)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(8f)
        )

        // Customer Information - Compact two-column layout
        document.add(
            Paragraph("Customer Information")
                .setFont(boldFont)
                .setFontSize(10f)
                .setMarginBottom(4f)
        )

        val customerTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
            .useAllAvailableWidth()
            .setMarginBottom(8f)

        customerTable.addCell(createCompactCell("Name: ${customerInfo.name}", normalFont, normalFontSize))
        customerTable.addCell(createCompactCell("Water Source: ${customerInfo.waterSource.name.replace("_", " ")}", normalFont, normalFontSize))

        customerTable.addCell(createCompactCell("Address: ${customerInfo.streetAddress}", normalFont, normalFontSize))
        if (customerInfo.waterSource == WaterSource.MUNICIPAL) {
            customerTable.addCell(createCompactCell("IL EPA #: ${customerInfo.epaSystemNumber}", normalFont, normalFontSize))
        } else {
            customerTable.addCell(createCompactCell("", normalFont, normalFontSize))
        }

        customerTable.addCell(createCompactCell("City & ZIP: ${customerInfo.city}, ${customerInfo.zip}", normalFont, normalFontSize))
        customerTable.addCell(createCompactCell("Phone: ${customerInfo.phone}", normalFont, normalFontSize))

        customerTable.addCell(createCompactCell("Email: ${customerInfo.email}", normalFont, normalFontSize))
        customerTable.addCell(createCompactCell("", normalFont, normalFontSize))

        document.add(customerTable)

        // Test Information
        document.add(
            Paragraph("Test Information")
                .setFont(boldFont)
                .setFontSize(10f)
                .setMarginBottom(4f)
        )

        val testInfoTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f, 1f)))
            .useAllAvailableWidth()
            .setMarginBottom(8f)

        testInfoTable.addCell(createCompactCell("Date: ${testResults.dateTested}", normalFont, normalFontSize))
        testInfoTable.addCell(createCompactCell("Location: ${testResults.sampleLocation}", normalFont, normalFontSize))
        testInfoTable.addCell(createCompactCell("Tested By: ${testResults.testerInitials}", normalFont, normalFontSize))

        document.add(testInfoTable)

        // Test Results - Compact table format
        document.add(
            Paragraph("Test Results")
                .setFont(boldFont)
                .setFontSize(10f)
                .setMarginBottom(4f)
        )

        val resultsTable = Table(UnitValue.createPercentArray(floatArrayOf(2f, 1f, 2f, 1f)))
            .useAllAvailableWidth()
            .setMarginBottom(8f)

        // Add results in compact 2-column format
        addResultRow(
            table = resultsTable,
            name1 = "Hardness", value1 = testResults.hardness, unit1 = "ppm",
            normalFont = normalFont, boldFont = boldFont, fontSize = normalFontSize,
            threshold1 = 180.0, alwaysBold1 = false,
            name2 = "TDS", value2 = testResults.tds, unit2 = "ppm",
            threshold2 = 500.0, alwaysBold2 = false
        )

        addResultRow(
            table = resultsTable,
            name1 = "pH", value1 = testResults.ph, unit1 = "",
            normalFont = normalFont, boldFont = boldFont, fontSize = normalFontSize,
            threshold1 = null, alwaysBold1 = false, minThreshold1 = 6.5, maxThreshold1 = 8.5,
            name2 = "Iron", value2 = testResults.iron, unit2 = "ppm",
            threshold2 = 0.3, alwaysBold2 = false
        )

        addResultRow(
            table = resultsTable,
            name1 = "Ammonia", value1 = testResults.ammonia, unit1 = "ppm",
            normalFont = normalFont, boldFont = boldFont, fontSize = normalFontSize,
            threshold1 = 0.5, alwaysBold1 = false,
            name2 = "Nitrates", value2 = testResults.nitrates, unit2 = "ppm",
            threshold2 = 10.0, alwaysBold2 = false
        )

        addResultRow(
            table = resultsTable,
            name1 = "Manganese", value1 = testResults.manganese, unit1 = "ppm",
            normalFont = normalFont, boldFont = boldFont, fontSize = normalFontSize,
            threshold1 = 0.05, alwaysBold1 = false,
            name2 = "Tannin", value2 = testResults.tannin, unit2 = "ppm",
            threshold2 = null, alwaysBold2 = false
        )

        addResultRow(
            table = resultsTable,
            name1 = "Arsenic", value1 = testResults.arsenic, unit1 = "ppb",
            normalFont = normalFont, boldFont = boldFont, fontSize = normalFontSize,
            threshold1 = null, alwaysBold1 = true,
            name2 = "Chromium 6", value2 = testResults.chromium6, unit2 = "ppb",
            threshold2 = null, alwaysBold2 = true
        )

        addResultRow(
            table = resultsTable,
            name1 = "Glyphosate", value1 = testResults.glyphosate, unit1 = "ppb",
            normalFont = normalFont, boldFont = boldFont, fontSize = normalFontSize,
            threshold1 = null, alwaysBold1 = true,
            name2 = "Lead", value2 = testResults.lead, unit2 = "ppb",
            threshold2 = null, alwaysBold2 = true
        )

        document.add(resultsTable)

        // Footer on first page
        document.add(
            Paragraph("Report generated on ${SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US).format(Date())}")
                .setFont(normalFont)
                .setFontSize(smallFont)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(8f)
        )

        // Notes on new page if present
        if (testResults.notes.isNotEmpty()) {
            document.add(com.itextpdf.layout.element.AreaBreak())

            document.add(
                Paragraph("Notes")
                    .setFont(boldFont)
                    .setFontSize(10f)
                    .setMarginBottom(4f)
            )
            document.add(
                Paragraph(testResults.notes)
                    .setFont(normalFont)
                    .setFontSize(normalFontSize)
            )
        }

        document.close()
        return file
    }

    private fun createCompactCell(
        text: String,
        font: com.itextpdf.kernel.font.PdfFont,
        fontSize: Float
    ): com.itextpdf.layout.element.Cell {
        return com.itextpdf.layout.element.Cell()
            .add(Paragraph(text).setFont(font).setFontSize(fontSize))
            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            .setPadding(2f)
    }

    private fun addResultRow(
        table: Table,
        name1: String,
        value1: String,
        unit1: String,
        normalFont: com.itextpdf.kernel.font.PdfFont,
        boldFont: com.itextpdf.kernel.font.PdfFont,
        fontSize: Float,
        threshold1: Double?,
        alwaysBold1: Boolean,
        minThreshold1: Double? = null,
        maxThreshold1: Double? = null,
        name2: String,
        value2: String,
        unit2: String,
        threshold2: Double?,
        alwaysBold2: Boolean,
        minThreshold2: Double? = null,
        maxThreshold2: Double? = null
    ) {
        // First parameter
        if (value1.isNotEmpty()) {
            val shouldBold1 = alwaysBold1 || shouldBoldValue(value1, threshold1, minThreshold1, maxThreshold1)
            val cell1Name = com.itextpdf.layout.element.Cell()
                .add(Paragraph("$name1:").setFont(normalFont).setFontSize(fontSize))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(2f)

            val valueText1 = if (unit1.isNotEmpty()) "$value1 $unit1" else value1
            val cell1Value = com.itextpdf.layout.element.Cell()
                .add(Paragraph(valueText1).setFont(if (shouldBold1) boldFont else normalFont).setFontSize(fontSize))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(2f)

            table.addCell(cell1Name)
            table.addCell(cell1Value)
        } else {
            table.addCell(createCompactCell("", normalFont, fontSize))
            table.addCell(createCompactCell("", normalFont, fontSize))
        }

        // Second parameter
        if (value2.isNotEmpty()) {
            val shouldBold2 = alwaysBold2 || shouldBoldValue(value2, threshold2, minThreshold2, maxThreshold2)
            val cell2Name = com.itextpdf.layout.element.Cell()
                .add(Paragraph("$name2:").setFont(normalFont).setFontSize(fontSize))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(2f)

            val valueText2 = if (unit2.isNotEmpty()) "$value2 $unit2" else value2
            val cell2Value = com.itextpdf.layout.element.Cell()
                .add(Paragraph(valueText2).setFont(if (shouldBold2) boldFont else normalFont).setFontSize(fontSize))
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(2f)

            table.addCell(cell2Name)
            table.addCell(cell2Value)
        } else {
            table.addCell(createCompactCell("", normalFont, fontSize))
            table.addCell(createCompactCell("", normalFont, fontSize))
        }
    }

    private fun shouldBoldValue(
        value: String,
        threshold: Double?,
        minThreshold: Double?,
        maxThreshold: Double?
    ): Boolean {
        val numValue = value.toDoubleOrNull() ?: return false

        if (threshold != null) {
            return numValue > threshold
        } else if (minThreshold != null && maxThreshold != null) {
            return numValue < minThreshold || numValue > maxThreshold
        }

        return false
    }
}