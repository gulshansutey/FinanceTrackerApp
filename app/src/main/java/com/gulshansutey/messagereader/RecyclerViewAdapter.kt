package com.gulshansutey.messagereader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_recycler_view.view.*

class RecyclerViewAdapter : ListAdapter<Sms, RecyclerView.ViewHolder>(COMPARATOR) {

    private var onItemClickListener: OnItemClickListener? = null

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Sms>() {
            override fun areItemsTheSame(oldItem: Sms, newItem: Sms) =
                oldItem.body == newItem.body
            override fun areContentsTheSame(oldItem: Sms, newItem: Sms) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.create(parent, onItemClickListener)

    fun addOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position).apply { (holder as ViewHolder).bindData(this) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        constructor(itemView: View, onItemClickListener: OnItemClickListener?) : this(itemView) {
            itemView.setOnClickListener { onItemClickListener?.onItemClick(adapterPosition) }
        }

        companion object {
            fun create(parent: ViewGroup, listener: OnItemClickListener?) = ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_recycler_view, parent, false), listener
            )
        }

        fun bindData(contact: Sms) {
            itemView.apply {
                tv_title.text = contact.address
                tv_sub_title.text = contact.body
                tv_type.text = contact.type
                tv_subject.text = contact.subject
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}