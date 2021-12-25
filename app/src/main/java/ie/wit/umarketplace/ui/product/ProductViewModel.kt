package ie.wit.umarketplace.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.umarketplace.models.ProductManager
import ie.wit.umarketplace.models.ProductModel

class ProductViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addProduct(product: ProductModel) {
        status.value = try {
            ProductManager.create(product)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}