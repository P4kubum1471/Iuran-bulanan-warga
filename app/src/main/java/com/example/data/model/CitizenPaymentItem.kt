package com.example.data.model

data class CitizenPaymentItem(
    val citizen: Citizen,
    val paymentRecord: PaymentRecord?
) {
    val tagihan: Double get() = paymentRecord?.tagihan ?: 0.0
    val admin: Double get() = paymentRecord?.admin ?: 0.0
    val denda: Double get() = paymentRecord?.denda ?: 0.0
    val ksu: Double get() = paymentRecord?.ksu ?: 0.0
    val total: Double get() = tagihan + admin + denda + ksu
    val isPaid: Boolean get() = tagihan > 0
}
