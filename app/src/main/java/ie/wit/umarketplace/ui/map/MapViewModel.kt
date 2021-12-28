package ie.wit.umarketplace.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import ie.wit.umarketplace.R

import ie.wit.umarketplace.firebase.FirebaseDBManager
import ie.wit.umarketplace.models.ProductModel
import timber.log.Timber
import androidx.core.content.ContextCompat

import android.graphics.drawable.Drawable

import com.google.android.gms.maps.model.BitmapDescriptor




class MapViewModel : ViewModel() {

    private var map: GoogleMap? = null

    private val _products = MutableLiveData<List<ProductModel>>()
    var observableProducts: LiveData<List<ProductModel>>
        get() = _products
        set(value) {_products.value = value.value}

    private val _product = MutableLiveData<ProductModel>()
    var observableProduct: LiveData<ProductModel> = TODO()
        get() = _product


    private val _rating = MutableLiveData<Float>()
    var observableRating: LiveData<Float>
        get() = _rating
        set(value) {_rating.value = value.value}

    fun load(email:String) {
        try {
            FirebaseDBManager.findAll(email, _products)
            Timber.i("Detail getUserProducts() Success : %s", _products.value.toString())
        }
        catch (e: Exception) {
            Timber.i("Detail getUserProducts() Error : ${e.message}")
        }
    }
    fun loadAll() {
        try {
            FirebaseDBManager.findAll(_products)
            Timber.i("List Load All Success : ${_products.value.toString()}")
        }
        catch (e: java.lang.Exception) {
            Timber.i("List Load All Error : $e.message")
        }
    }

    fun doConfigureMap(m: GoogleMap) {
        map = m
        map?.uiSettings?.isZoomControlsEnabled = true
        map!!.setOnMarkerClickListener {
            Timber.i("setOnMarkerClickListener $it")
            doMarkerSelected(it)
            true
        }
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.360401, -6.043043), 10f))
    }
    fun mapLocationUpdate(context: Context) {
        Timber.i("Step1")
        map?.clear()
        var marker = LatLng(0.0, 0.0)
        _products.value!!.forEach {
            Timber.i("Step2 $it")
            marker = LatLng(it.lat, it.lng)

            var bitmap = when(it.category) {
                1 -> bitmapDescriptorFromVector(context, R.drawable.for_sale_product)!!
                2 -> bitmapDescriptorFromVector(context, R.drawable.sold_product)!!
                else -> {
                    bitmapDescriptorFromVector(context, R.drawable.for_sale_product)!!
                }
            }




            Timber.i("Step5")
            val options = MarkerOptions().title(marker.toString()).position(marker).icon(bitmap)
            Timber.i("Step6")
            map?.addMarker(options)?.tag = it.uid

        }


    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
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
    fun loadImage(imageView: ImageView){
        Timber.i("loadImage")
        if (_product.value!!.image != ""){
            Picasso.get()
                .load(_product.value!!.image)
                .resize(200, 200)
                .into(imageView)
        }
    }

    fun doMarkerSelected(marker: Marker){
        Timber.i("doMarkerSelected")
        val tag = marker.tag as String
        FirebaseDBManager.findById(tag, _product)
    }

}
