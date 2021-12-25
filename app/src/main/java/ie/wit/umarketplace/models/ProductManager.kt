package ie.wit.umarketplace.models

import androidx.lifecycle.MutableLiveData
import ie.wit.umarketplace.api.ProductClient
import ie.wit.umarketplace.api.ProductWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

object ProductManager : ProductStore {

    private var products = ArrayList<ProductModel>()

    override fun findAll(productsList: MutableLiveData<List<ProductModel>>) {

        val call = ProductClient.getApi().getall()

        call.enqueue(object : Callback<List<ProductModel>> {
            override fun onResponse(call: Call<List<ProductModel>>,
                                    response: Response<List<ProductModel>>
            ) {
                productsList.value = response.body() as? ArrayList<ProductModel>
                Timber.i("Retrofit JSON = ${response.body()}")
            }

            override fun onFailure(call: Call<List<ProductModel>>, t: Throwable) {
                Timber.i("Retrofit Error : $t.message")
            }
        })
    }

    override fun findById(id:String) : ProductModel? {
        val foundProduct: ProductModel? = products.find { it._id == id }
        return foundProduct
    }

    override fun create(product: ProductModel) {

        val call = ProductClient.getApi().post(product)

        call.enqueue(object : Callback<ProductWrapper> {
            override fun onResponse(call: Call<ProductWrapper>,
                                    response: Response<ProductWrapper>
            ) {
                val productWrapper = response.body()
                if (productWrapper != null) {
                    Timber.i("Retrofit ${productWrapper.message}")
                    Timber.i("Retrofit ${productWrapper.data.toString()}")
                }
            }

            override fun onFailure(call: Call<ProductWrapper>, t: Throwable) {
                        Timber.i("Retrofit Error : $t.message")
            }
        })
    }

    override fun delete(id: String) {
        val call = ProductClient.getApi().delete(id)

        call.enqueue(object : Callback<ProductWrapper> {
            override fun onResponse(call: Call<ProductWrapper>,
                                    response: Response<ProductWrapper>
            ) {
                val productWrapper = response.body()
                if (productWrapper != null) {
                    Timber.i("Retrofit Delete ${productWrapper.message}")
                    Timber.i("Retrofit Delete ${productWrapper.data.toString()}")
                }
            }

            override fun onFailure(call: Call<ProductWrapper>, t: Throwable) {
                Timber.i("Retrofit Delete Error : $t.message")
            }
        })
    }

    fun logAll() {
        Timber.v("** Products List **")
        products.forEach { Timber.v("Add to sell ${it}") }
    }
}