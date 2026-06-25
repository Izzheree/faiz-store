package com.faiz0033.faizstore.utils

import java.text.NumberFormat
import java.util.Locale

fun Double.toRupiah(): String {
    val localeID = Locale("in", "ID")
    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
    formatRupiah.maximumFractionDigits = 0
    return formatRupiah.format(this).replace("Rp", "Rp ")
}
