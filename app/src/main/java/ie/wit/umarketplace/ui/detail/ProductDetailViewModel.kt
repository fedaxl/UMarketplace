package ie.wit.umarketplace.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.umarketplace.models.ProductManager
import ie.wit.umarketplace.models.ProductModel

class ProductDetailViewModel : ViewModel() {
    private val product = MutableLiveData<ProductModel>()

    val observableProduct: LiveData<ProductModel>
        get() = product

    fun getProduct(id: String) {
        product.value = ProductManager.findById(id)
    }
}

