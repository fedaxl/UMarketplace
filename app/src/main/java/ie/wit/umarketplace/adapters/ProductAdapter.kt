package ie.wit.umarketplace.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.wit.umarketplace.R
import ie.wit.umarketplace.databinding.CardProductBinding
import ie.wit.umarketplace.models.ProductModel

interface ProductClickListener {
    fun onProductClick(product: ProductModel)
}

class ProductAdapter constructor(private var products: ArrayList<ProductModel>,
                                  private val listener: ProductClickListener)
    : RecyclerView.Adapter<ProductAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardProductBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val product = products[holder.adapterPosition]
        holder.bind(product,listener)
    }

    fun removeAt(position: Int) {
        products.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = products.size

    inner class MainHolder(val binding : CardProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductModel, listener: ProductClickListener) {
            binding.root.tag = product._id
            binding.product = product
            if (product.image != ""){
                Picasso.get()
                    .load(product.image)
                    .resize(200, 200)
                    .into(binding.imageIcon)
            }
            when (product.category) {
                1 -> binding.categoryIcon.setImageResource(R.drawable.for_sale_product)
                2 -> binding.categoryIcon.setImageResource(R.drawable.sold_product)
                else -> {
                    binding.categoryIcon.setImageResource(R.drawable.for_sale)
                }}
            binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)
            binding.root.setOnClickListener { listener.onProductClick(product) }
            binding.executePendingBindings()
        }
    }
}