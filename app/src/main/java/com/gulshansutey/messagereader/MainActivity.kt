package com.gulshansutey.messagereader

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object SmsUtil {
        const val SMS_PERMISSION_REQUEST_CODE = 10
    }
    private lateinit var viewModel : MainViewModel
    private lateinit var adapter: RecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        init()
    }

    private fun init(){
         recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = RecyclerViewAdapter()
        recycler_view.adapter = adapter

        viewModel.smsList.observe(this,
            Observer {
                adapter.submitList(it)
                 calculateAmount(it,object : TxnAmountObserver{
                     override fun onCreditAmount(amount: String) {
                         tv_income_amt.text = amount
                     }
                     override fun onDebitAmount(amount: String) {
                         tv_spend_amt.text = amount
                     }
                     override fun onComplete(credit: Double, debit: Double) {
                         initPieChart(credit,debit)
                     }
                 })
            })

         if (hasPermission(Manifest.permission.READ_SMS)){
             viewModel.readMessageInbox()
         }else {
            requestPermission(Manifest.permission.READ_SMS,
            SMS_PERMISSION_REQUEST_CODE,"This permission is required for this app to work")
         }

    }

    fun initPieChart(credit: Double, debit: Double) {
        chart_pie.isRotationEnabled = false
        chart_pie.description .isEnabled = false
        chart_pie.setDrawEntryLabels(false)
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(
            PieEntry(
                (credit).toFloat(),
                "Credits",
                null
            )
        )
        entries.add(
            PieEntry(
                (debit).toFloat(),
                "Debits",
               null
            )
        )
        val colors: ArrayList<Int> = ArrayList()
        for (c in ColorTemplate.MATERIAL_COLORS) colors.add(c)

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setDrawValues(false)
        chart_pie.data = data
        chart_pie.invalidate()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_REQUEST_CODE && grantResults[0] == Activity.RESULT_OK){
            viewModel.readMessageInbox()
        }
    }


}