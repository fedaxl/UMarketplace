package ie.wit.umarketplace.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.umarketplace.models.ProductManager
import ie.wit.umarketplace.models.ProductModel
import timber.log.Timber
import java.lang.Exception

class ReportViewModel : ViewModel() {

    private val productsList =
        MutableLiveData<List<ProductModel>>()

    val observableProductsList: LiveData<List<ProductModel>>
        get() = productsList

    init { load() }

    fun load() {
        try {
            ProductManager.findAll(productsList)
            Timber.i("Retrofit Load Success : $productsList.value")
        }
        catch (e: Exception) {
            Timber.i("Retrofit Load Error : $e.message")
        }
    }

    fun delete(id: String) {
        try {
            ProductManager.delete(id)
            Timber.i("Retrofit Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Retrofit Delete Error : $e.message")
        }
    }
}

