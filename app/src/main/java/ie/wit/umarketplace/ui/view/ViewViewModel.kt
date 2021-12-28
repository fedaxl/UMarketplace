package ie.wit.umarketplace.ui.view

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import ie.wit.umarketplace.R
import ie.wit.umarketplace.firebase.FirebaseDBManager
import ie.wit.umarketplace.models.ProductModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ViewViewModel : ViewModel() {

    private var map: GoogleMap? = null

    private val _product = MutableLiveData<ProductModel>()
    var observableProduct: LiveData<ProductModel>
        get() = _product
        set(value) {_product.value = value.value}

    fun getProduct( id: String?) {
        try {

            FirebaseDBManager.findById( id!!, _product)
            Timber.i("Detail getProduct() Success : ${
                _product.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Detail getProduct() Error : ${e.message}")
        }
    }

    fun loadCategoryImage(imageView: ImageView){
        when (_product.value!!.category) {
            1 -> imageView.setImageResource(R.drawable.for_sale_product)
            2 -> imageView.setImageResource(R.drawable.sold_product)
            else -> {
                imageView.setImageResource(R.drawable.for_sale_product)
            }
        }
    }


    fun loadImage(image: String, imageView: ImageView){
        Picasso.get()
            .load(image)
            .into(imageView)
    }

    fun doConfigureMap(m: GoogleMap) {
        map = m
        map?.uiSettings?.isZoomControlsEnabled = true
    }
    fun mapLocationUpdate() {
        map?.clear()
        val marker = LatLng(_product.value!!.lat, _product.value!!.lng)
        val options = MarkerOptions().title(marker.toString()).position(marker)
        map?.addMarker(options)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))
    }

}