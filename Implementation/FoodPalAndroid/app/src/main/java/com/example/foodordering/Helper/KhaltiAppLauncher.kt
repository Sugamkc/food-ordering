@file:Suppress("PackageName")

package com.example.foodordering.Helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object KhaltiAppLauncher {

    private const val KHALTI_PACKAGE_NAME = "com.khalti.android"

    fun openPaymentGateway(context: Context, paymentUrl: String) {
        if (paymentUrl.isBlank()) {
            Toast.makeText(context, "Invalid payment URL", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(context, "Opening Khalti Gateway... 🟣", Toast.LENGTH_SHORT).show()

        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            // Fallback: If no browser can handle it, direct to Play Store
            try {
                val playStoreIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$KHALTI_PACKAGE_NAME")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(playStoreIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Unable to open browser or Play Store", Toast.LENGTH_SHORT).show()
            }
        }
    }
}