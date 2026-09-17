package com.dalmoa.android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dalmoa.android.databinding.ItemMonthlySpendingBinding
import com.dalmoa.android.model.MonthSpending
import java.text.DecimalFormat

class MonthlySpendingAdapter(
    private var items: List<MonthSpending>
) : RecyclerView.Adapter<MonthlySpendingAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMonthlySpendingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMonthlySpendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val decimalFormat = DecimalFormat("#,###")
        with(holder.binding) {
            tvMonthLabel.text = "${item.year}년 ${item.month}월"
            tvMonthAmount.text = "${decimalFormat.format(item.totalAmount)}원"
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<MonthSpending>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
