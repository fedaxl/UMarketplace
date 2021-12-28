package ie.wit.umarketplace.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.umarketplace.firebase.FirebaseDBManager
import ie.wit.umarketplace.models.ProductModel
import timber.log.Timber
import java.lang.Exception

class ListViewModel : ViewModel() {

    private val productList =
        MutableLiveData<List<ProductModel>>()

    val observableProductList: LiveData<List<ProductModel>>
        get() = productList

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    init { load() }

    fun load() {
        try {
            FirebaseDBManager.findAll(liveFirebaseUser.value?.email!!,
                productList)
            Timber.i("List Load Success : ${productList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("List Load Error : $e.message")
        }
    }
    fun loadAll() {
        try {
            FirebaseDBManager.findAll(productList)
            Timber.i("List Load All Success : ${productList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("List Load All Error : $e.message")
        }
    }

    fun delete(userid: String, id: String) {
        try {
            FirebaseDBManager.delete(userid,id)
            Timber.i("List Delete Success")
        }
        catch (e: Exception) {
            Timber.i("List Delete Error : $e.message")
        }
    }
}

