package com.gulshansutey.messagereader

import android.app.Application
import android.net.Uri
import android.provider.Telephony
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.regex.Pattern

class MainViewModel constructor(private val ctx: Application) :
    AndroidViewModel(ctx) {
    private val mutableSmsList = MutableLiveData<List<Sms>>()
    val smsList: LiveData<List<Sms>>
        get() = mutableSmsList

    fun readMessageInbox() {
        mutableSmsList.value = getSmsFromDevice()
    }

    private fun getSmsFromDevice(): List<Sms> {
        val cursor = ctx.contentResolver.query(
            Uri.parse("content://sms/"),
            null, null, null, null
        )
        val list = arrayListOf<Sms>()
        cursor.use {
            if (it != null && it.moveToFirst()) {
                while (it.moveToNext()) {
                    val regEx: Pattern = Pattern.compile("[a-zA-Z0-9]{2}-[a-zA-Z0-9]{6}")
                    val regExAmt: Pattern =
                        Pattern.compile("(?=.*[Aa]ccount.*|.*[Aa]/[Cc].*|.*[Aa][Cc][Cc][Tt].*|.*[Cc][Aa][Rr][Dd].*)(?=.*[Cc]redit.*|.*[Dd]ebit.*)(?=.*[Ii][Nn][Rr].*|.*[Rr][Ss].*)")
                    val address =
                        cursor?.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                    val body = cursor?.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
                    if (regEx.matcher(address as CharSequence).find() &&
                        regExAmt.matcher(body as CharSequence).find() &&
                        body.contains("credited") ||
                        body!!.contains("debited") ||
                        body.contains("Txn of INR")
                    ) {
                        val type =
                            cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE))
                        val smsDate =
                            cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))

                        val subject =
                            cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.SUBJECT))
                        list.add(
                            Sms(
                                address = address,
                                date = smsDate,
                                type = type,
                                body = body,
                                subject = subject
                            )
                        )
                    }
                }
            }
        }
        return list
    }

}