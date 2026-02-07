package com.crotsertech.testtakerstoolkit

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.github.gcacace.signaturepad.views.SignaturePad

class SignatureDialogFragment : DialogFragment() {

    private var listener: SignatureDialogListener? = null

    interface SignatureDialogListener {
        fun onSignatureSaved(signatureBitmap: Bitmap)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? SignatureDialogListener
        if (listener == null) {
            throw ClassCastException("$context must implement SignatureDialogListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_signature, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signaturePad = view.findViewById<SignaturePad>(R.id.signature_pad)
        val clearButton = view.findViewById<Button>(R.id.clear_button)
        val saveButton = view.findViewById<Button>(R.id.save_button)

        clearButton.setOnClickListener {
            signaturePad.clear()
        }

        saveButton.setOnClickListener {
            if (!signaturePad.isEmpty) {
                val signatureBitmap = signaturePad.signatureBitmap
                listener?.onSignatureSaved(signatureBitmap)
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
