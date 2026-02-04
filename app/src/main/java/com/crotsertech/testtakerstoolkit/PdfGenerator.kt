package com.crotsertech.testtakerstoolkit

import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.colors.DeviceGray
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.AreaBreak
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class TestResultData(
    val label: String,
    val value: String,
    val unit: String,
    val boldCondition: (Float) -> Boolean
)

class PdfGenerator(private val context: Context) {

    fun generatePdf(
        companyInfo: CompanyInfo,
        customerInfo: CustomerInfo,
        testResults: TestResults
    ) {
        val pdfName = "WaterTest_${customerInfo.name.replace(" ", "")}_${
            SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        }.pdf"

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, pdfName)
        val outputStream = FileOutputStream(file)

        val writer = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument)

        // --- Build Address Line Helper ---
        fun buildAddressLine(city: String, state: String, zip: String): String {
            val cityState = listOf(city, state).filter { it.isNotBlank() }.joinToString(", ")
            return listOf(cityState, zip).filter { it.isNotBlank() }.joinToString(" ")
        }

        // === COMPANY INFO HEADER ===
        if (companyInfo.name.isNotBlank()) {
            document.add(Paragraph(companyInfo.name).setBold().setTextAlignment(TextAlignment.CENTER))
        }
        val companyAddressLine = buildAddressLine(companyInfo.city, "", companyInfo.zip)
        if (companyInfo.streetAddress.isNotBlank()) {
            document.add(Paragraph(companyInfo.streetAddress).setTextAlignment(TextAlignment.CENTER))
        }
        if (companyAddressLine.isNotBlank()) {
            document.add(Paragraph(companyAddressLine).setTextAlignment(TextAlignment.CENTER))
        }
        val contactInfo = listOf(
            if (companyInfo.phone.isNotBlank()) "Phone: ${companyInfo.phone}" else null,
            if (companyInfo.email.isNotBlank()) "Email: ${companyInfo.email}" else null
        ).filterNotNull().joinToString(" | ")
        if (contactInfo.isNotBlank()) {
            document.add(Paragraph(contactInfo).setTextAlignment(TextAlignment.CENTER))
        }
        if (companyInfo.website.isNotBlank()) {
            document.add(Paragraph(companyInfo.website).setTextAlignment(TextAlignment.CENTER))
        }


        // === CUSTOMER INFO ===
        document.add(Paragraph("\nCustomer Information").setBold())
        if (customerInfo.name.isNotBlank()) document.add(Paragraph(customerInfo.name))
        if (customerInfo.streetAddress.isNotBlank()) document.add(Paragraph(customerInfo.streetAddress))
        val customerAddressLine = buildAddressLine(customerInfo.city, customerInfo.state, customerInfo.zip)
        if (customerAddressLine.isNotBlank()) {
            document.add(Paragraph(customerAddressLine))
        }
        if (customerInfo.phone.isNotBlank()) document.add(Paragraph("Phone: ${customerInfo.phone}"))
        if (customerInfo.email.isNotBlank()) document.add(Paragraph("Email: ${customerInfo.email}"))
        document.add(Paragraph("Water Source: ${customerInfo.waterSource.displayName}"))
        if (customerInfo.waterSource == WaterSource.MUNICIPAL && customerInfo.epaSystemNumber.isNotBlank()) {
            document.add(Paragraph("IL EPA System Number: ${customerInfo.epaSystemNumber}"))
        }

        // === TEST RESULTS ===
        document.add(Paragraph("\nTest Results").setBold())
        if (testResults.dateTested.isNotBlank()) document.add(Paragraph("Date Tested: ${testResults.dateTested}"))
        if (testResults.sampleLocation.isNotBlank()) document.add(Paragraph("Sample Location: ${testResults.sampleLocation}"))
        if (testResults.testerInitials.isNotBlank()) document.add(Paragraph("Tester Initials: ${testResults.testerInitials}"))

        // Add the explanation for bolded results
        document.add(Paragraph("Bolded results may indicate a potential health concern.").setItalic().setFontSize(10f))
        document.add(Paragraph("")) // Spacer

        val resultsData = listOf(
            TestResultData("Hardness", testResults.hardness, "gpg") { v -> v > 10.5 },
            TestResultData("TDS", testResults.tds, "ppm") { v -> v > 500 },
            TestResultData("pH", testResults.ph, "") { v -> v < 6.5 || v > 8.5 },
            TestResultData("Iron", testResults.iron, "ppm") { v -> v > 0.3 },
            TestResultData("Ammonia", testResults.ammonia, "ppm") { v -> v > 0.5 },
            TestResultData("Nitrates", testResults.nitrates, "ppm") { v -> v > 10 },
            TestResultData("Manganese", testResults.manganese, "pm") { v -> v > 0.05 },
            TestResultData("Tannin", testResults.tannin, "ppm") { v -> v > 0.5 },
            TestResultData("Arsenic", testResults.arsenic, "ppb") { v -> v > 1 },
            TestResultData("Chromium-6", testResults.chromium6, "ppb") { v -> v > 0.02 },
            TestResultData("Glyphosate", testResults.glyphosate, "ppb") { v -> v > 700 },
            TestResultData("Lead", testResults.lead, "ppb") { v -> v > 15 }
        )

        // FIX: Create a 4-column table to create a more compact 2-column user layout
        val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f, 1f, 1f))).useAllAvailableWidth()
        val lightGrayBorder = SolidBorder(DeviceGray(0.75f), 1f)

        fun createCell(content: String, isBold: Boolean = false): Cell {
            val paragraph = if (isBold) Paragraph(content).setBold() else Paragraph(content)
            return Cell().add(paragraph)
                .setBorder(lightGrayBorder)
                .setPadding(5f)
        }

        for (i in resultsData.indices step 2) {
            val data1 = resultsData[i]
            val numericValue1 = data1.value.toFloatOrNull()
            val isBold1 = numericValue1 != null && data1.boldCondition(numericValue1)
            val displayValue1 = if (data1.value.isNotBlank()) {
                if (data1.unit.isNotBlank()) "${data1.value} ${data1.unit}" else data1.value
            } else {
                "Not Tested"
            }
            table.addCell(createCell(data1.label))
            table.addCell(createCell(displayValue1, isBold1))

            if (i + 1 < resultsData.size) {
                val data2 = resultsData[i + 1]
                val numericValue2 = data2.value.toFloatOrNull()
                val isBold2 = numericValue2 != null && data2.boldCondition(numericValue2)
                val displayValue2 = if (data2.value.isNotBlank()) {
                    if (data2.unit.isNotBlank()) "${data2.value} ${data2.unit}" else data2.value
                } else {
                    "Not Tested"
                }
                table.addCell(createCell(data2.label))
                table.addCell(createCell(displayValue2, isBold2))
            } else {
                table.addCell(createCell(""))
                table.addCell(createCell(""))
            }
        }
        document.add(table)


        // === NOTES ===
        if (testResults.notes.isNotBlank()) {
            document.add(AreaBreak()) // Add a page break
            document.add(Paragraph("\nNotes:").setBold())
            document.add(Paragraph(testResults.notes))
        }

        document.close()
    }
}
