package ie.wit.umarketplace.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class ProductModel(var _id: String = "N/A",
                        @SerializedName("paymenttype")
                         val paymentmethod: String = "N/A",
                        val message: String = "n/a",
                        val amount: Int = 0) : Parcelable