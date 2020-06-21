package com.gulshansutey.messagereader

interface TxnAmountObserver {
    fun onCreditAmount(amount:String)
    fun onDebitAmount(amount: String)
    fun onComplete(credit:Double,debit:Double)
}