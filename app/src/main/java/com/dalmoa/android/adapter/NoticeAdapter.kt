package com.dalmoa.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dalmoa.android.data.remote.dto.notice.NoticeListItem
import com.dalmoa.android.databinding.ItemNoticeBinding

class NoticeAdapter(
    private var notices: List<NoticeListItem>,
    private val onItemClick: (NoticeListItem) -> Unit
) : RecyclerView.Adapter<NoticeAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notices[position]

        with(holder.binding) {
            if (item.version.isNullOrEmpty()) {
                tvVersion.visibility = View.GONE
            } else {
                tvVersion.visibility = View.VISIBLE
                tvVersion.text = "v${item.version}"
            }
            tvTitle.text = item.title
            tvDate.text = item.createdAt.substringBefore("T")

            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount(): Int = notices.size

    fun updateData(newData: List<NoticeListItem>) {
        this.notices = newData
        notifyDataSetChanged()
    }
}
