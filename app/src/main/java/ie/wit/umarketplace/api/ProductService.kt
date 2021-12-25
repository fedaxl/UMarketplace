package ie.wit.umarketplace.api

import ie.wit.umarketplace.models.ProductModel
import retrofit2.Call
import retrofit2.http.*


interface ProductService {
    @GET("/products")
    fun getall(): Call<List<ProductModel>>

    @GET("/products/{id}")
    fun get(@Path("id") id: String): Call<ProductModel>

    @DELETE("/products/{id}")
    fun delete(@Path("id") id: String): Call<ProductWrapper>

    @POST("/products")
    fun post(@Body product: ProductModel): Call<ProductWrapper>

    @PUT("/products/{id}")
    fun put(@Path("id") id: String,
            @Body product: ProductModel
    ): Call<ProductWrapper>
}