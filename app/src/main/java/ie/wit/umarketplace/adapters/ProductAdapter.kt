package ie.wit.umarketplace.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.umarketplace.R
import ie.wit.umarketplace.databinding.CardDonationBinding
import ie.wit.umarketplace.models.ProductModel

interface ProductClickListener {
    fun onProductClick(product: ProductModel)
}

class ProductAdapter constructor(private var products: ArrayList<ProductModel>,
                                  private val listener: ProductClickListener)
    : RecyclerView.Adapter<ProductAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardDonationBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val donation = products[holder.adapterPosition]
        holder.bind(donation,listener)
    }

    fun removeAt(position: Int) {
        products.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = products.size

    inner class MainHolder(val binding : CardDonationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductModel, listener: ProductClickListener) {
            binding.root.tag = product._id
            binding.product = product
            binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)
            binding.root.setOnClickListener { listener.onProductClick(product) }
            binding.executePendingBindings()
        }
    }
}