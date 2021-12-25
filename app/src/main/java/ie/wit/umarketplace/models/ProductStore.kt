package ie.wit.umarketplace.models

import androidx.lifecycle.MutableLiveData

interface ProductStore {
    fun findAll(productsList: MutableLiveData<List<ProductModel>>)
    fun findById(id: String) : ProductModel?
    fun create(product: ProductModel)
    fun delete(id: String)
}