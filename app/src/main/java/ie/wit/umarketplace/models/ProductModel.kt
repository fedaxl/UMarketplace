package ie.wit.umarketplace.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class ProductModel(var _id: String = "N/A",
                        var title: String = "",
                        var description: String = "",
                        var category: Int = 1,
                        @SerializedName("paymenttype")
                        val paymentmethod: String = "N/A",
                        val message: String = "n/a",
                        var email: String? = "homer@simpson.com",
                        var image: String = "",
                        val amount: Int = 0,
                        var lat : Double = 0.0,
                        var lng: Double = 0.0,
                        var zoom: Float = 0f,) : Parcelable

@Parcelize
data class Location(  var lat: Double = 0.0,
                      var lng: Double = 0.0,
                      var zoom: Float = 0f) : Parcelable
//@Parcelize
//data class Review(
//    var comment: String? = "",
//    var date: String = "",
//    var user: String = "",
//    var rating: Float? = 0f) : Parcelable