package com.example.wpigroupfinder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.ScrollView
import android.widget.LinearLayout
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.util.TypedValue

class PermissionsRationaleActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // scrollView with TextView to display the rationale
        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(40, 60, 40, 60)
        }
        val textView = TextView(this).apply {
            text = """
                📄 Privacy Policy

                Please give access to your health data through Health Connect
                in order to provide step count.

                🔐 Data Handling:
                - Your data stays on your device.
                - We do not share your health data with third parties.
                - You can revoke access at any time in the system settings
            """.trimIndent()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        }

        layout.addView(textView)
        scrollView.addView(layout)
        setContentView(scrollView)
    }

}