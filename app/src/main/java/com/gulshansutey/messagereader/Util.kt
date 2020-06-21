package com.gulshansutey.messagereader

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.*
import java.util.regex.Pattern


fun Context.hasPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}

fun Activity.requestPermission(
    permission: String,
    requestCode: Int,
    rationaleStr: String
) {
    val provideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

    if (provideRationale) {
        AlertDialog.Builder(this).apply {
            setTitle("Permission")
            setMessage(rationaleStr)
            setPositiveButton("Ok") { _, _ ->
                ActivityCompat.requestPermissions(
                    this@requestPermission,
                    arrayOf(permission),
                    requestCode
                )
            }
            create()
            show()
        }
    } else {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }
}


inline fun calculateAmount(
    smsList: List<Sms>,
    observer: TxnAmountObserver
) {
    var debits = 0.0
    var credit = 0.0
    smsList.forEach {
        val body = it.body
        if (!TextUtils.isEmpty(body)) {
            if (body!!.contains("credited")) {
                credit += body.extractCreditAmount()
            } else if (body.contains("debited") || body.startsWith("Txn of ")) {
                debits += body.extractDebitAmount()
            }
        }
    }
    observer.onCreditAmount(String.format("%.2f", credit))
    observer.onDebitAmount(String.format("%.2f", debits))
    observer.onComplete(credit,debits)
}

fun String.extractCreditAmount(): Double {
    val string = this.toLowerCase(Locale.ENGLISH)

    //INR
    if (string.contains("credited with inr") ||
        string.contains("credited by inr")
    ) {
        val s = string.substringAfter("inr").trim()
        val amt = s.substring(0, s.indexOf(".") + 3).replace(",", "")
        return amt.intoDoubleOrZero()
    }
    //RS
    else if (string.contains("credited with rs") ||
        string.contains("credited by rs")
    ) {
        val s = string.substringAfter("Rs.").trim()
        val amt = s.substring(0, s.indexOf(".") + 3).replace(",", "")
        try {
            return amt.intoDoubleOrZero()
        } catch (e: Exception) {
            Log.e("Error", string)
            e.printStackTrace()
        }

    }
    return 0.0
}



fun String.extractDebitAmount(): Double {
    val string = this.toLowerCase(Locale.ENGLISH)
    if (string.contains("txn of inr")) {
        val s = string.substringAfter("inr").trim()
        val amt = s.substring(0, s.indexOf(".") + 3).replace(",", "")
        return amt.intoDoubleOrZero()
    }
    //INR
    else if (string.contains("debited with inr") ||
        string.contains("debited by inr")
    ) {
        val s = string.substringAfter("inr").trim()
        val amt = s.substring(0, s.indexOf(".") + 3).replace(",", "")
        return amt.intoDoubleOrZero()
    }
    //RS
    else if (string.contains("debited with rs") ||
        string.contains("debited by rs")
    ) {
        val s = string.substringAfter("Rs.").trim()
        val amt = s.substring(0, s.indexOf(".") + 3).replace(",", "")
        return amt.intoDoubleOrZero()
    }
    return 0.0
}

private fun String.intoDoubleOrZero(): Double {
    val regex = Pattern.compile("\\d*\\.?\\d+")
    return if (this.matches(regex = regex.toRegex())) {
        this.toDouble()
    } else {
        0.0
    }
}

fun main() {
    val sms = "You account is credited with INR 12,991.00 done on Acct"
    print(sms.extractCreditAmount())
}

