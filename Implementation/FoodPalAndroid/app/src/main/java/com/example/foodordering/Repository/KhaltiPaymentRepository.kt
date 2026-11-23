package com.example.foodordering.Repository

import com.example.foodordering.Domain.FoodModel
import com.example.foodordering.Helper.UserSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions

class KhaltiPaymentRepository {

    private val functions = FirebaseFunctions.getInstance()

    fun createPayment(
        orderId: String,
        totalAmount: Double,
        items: List<FoodModel>,
        onSuccess: (paymentUrl: String, pidx: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = try { FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous_user" } catch (e: Exception) { "anonymous_user" }
        val payload = mapOf(
            "uid" to uid,
            "orderId" to orderId,
            "totalAmount" to totalAmount,
            "customerName" to UserSession.name,
            "customerEmail" to UserSession.email,
            "customerPhone" to "9812345678",
            "items" to items.map { item ->
                mapOf(
                    "Id" to item.Id,
                    "Title" to item.Title,
                    "Price" to item.Price,
                    "numberInCart" to item.numberInCart
                )
            }
        )

        functions
            .getHttpsCallable("createKhaltiPayment")
            .call(payload)
            .addOnSuccessListener { result ->
                val data = result.data as? Map<*, *>
                val paymentUrl = data?.get("paymentUrl") as? String
                val pidx = data?.get("pidx") as? String

                if (paymentUrl != null) {
                    onSuccess(paymentUrl, pidx ?: "")
                } else {
                    onError("Payment URL was not returned by server.")
                }
            }
            .addOnFailureListener { error ->
                onError(error.localizedMessage ?: "Payment initiation failed.")
            }
    }

    fun verifyPayment(
        pidx: String,
        orderId: String,
        onSuccess: (Boolean, String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        val payload = mapOf(
            "pidx" to pidx,
            "orderId" to orderId
        )

        functions
            .getHttpsCallable("verifyKhaltiPayment")
            .call(payload)
            .addOnSuccessListener { result ->
                val data = result.data as? Map<*, *>
                val paid = data?.get("paid") as? Boolean ?: false
                val status = data?.get("status") as? String ?: "UNKNOWN"
                val txnId = data?.get("transactionId") as? String ?: ""

                onSuccess(paid, status, txnId)
            }
            .addOnFailureListener { error ->
                onError(error.localizedMessage ?: "Payment verification failed.")
            }
    }
}
