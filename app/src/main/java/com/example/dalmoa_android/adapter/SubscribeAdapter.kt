package com.example.dalmoa_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dalmoa_android.databinding.ItemSubscribeBinding
import com.example.dalmoa_android.model.Subscribe
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
            tvCategory.text = "${item.category.displayName} • ${item.date}"
            tvPrice.text = "${decimalFormat.format(item.price)}원"
            tvCurrency.text = item.currency

            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount(): Int = subscribes.size

    fun updateData(newData: List<Subscribe>) {
        this.subscribes = newData
        notifyDataSetChanged()
    }
}