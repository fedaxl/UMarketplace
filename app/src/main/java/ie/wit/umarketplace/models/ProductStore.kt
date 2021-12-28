package ie.wit.umarketplace.models

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface ProductStore {
    fun findAll(productsList:
                MutableLiveData<List<ProductModel>>)
    fun findAll(userid:String,
                productsList:
                MutableLiveData<List<ProductModel>>)
    fun findById(productid: String,
                 product: MutableLiveData<ProductModel>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, product: ProductModel, context: Context)
    fun delete(userid:String,productid: String)
    fun update(userid:String,productid: String,product: ProductModel, context: Context, imageChanged: Boolean)
}