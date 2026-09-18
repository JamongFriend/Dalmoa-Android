package com.dalmoa.android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dalmoa.android.databinding.ItemSubscribeBinding
import com.dalmoa.android.model.Subscribe
import com.dalmoa.android.core.formatDate
import java.text.DecimalFormat

class SubscribeAdapter(
    private var subscribes: List<Subscribe>,
    private val onItemClick: (Subscribe) -> Unit
) : RecyclerView.Adapter<SubscribeAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSubscribeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSubscribeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = subscribes[position]
        val decimalFormat = DecimalFormat("#,###")

        with(holder.binding) {
            tvServiceName.text = item.name
            val categoryLabel = if (item.category == com.dalmoa.android.model.SubCategory.ETC && !item.customCategoryTag.isNullOrEmpty()) {
                item.customCategoryTag
            } else {
                item.category.displayName
            }
            tvCategory.text = "${categoryLabel} | ${formatDate(item.date, item.term)}"
            val termSuffix = when (item.term) {
                com.dalmoa.android.model.Term.WEEK -> " / 주"
                com.dalmoa.android.model.Term.MONTH -> " / 월"
                com.dalmoa.android.model.Term.YEAR -> " / 년"
            }
            tvPrice.text = if (item.currency == "USD") {
                "$${decimalFormat.format(item.price)} (약 ${decimalFormat.format(item.convertedPriceKrw)}원)${termSuffix}"
            } else {
                "${decimalFormat.format(item.price)}원${termSuffix}"
            }
            tvCurrency.visibility = android.view.View.GONE

            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount(): Int = subscribes.size

    fun updateData(newData: List<Subscribe>) {
        this.subscribes = newData
        notifyDataSetChanged()
    }
}
