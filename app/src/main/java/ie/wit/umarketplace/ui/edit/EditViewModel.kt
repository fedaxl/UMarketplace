package ie.wit.umarketplace.ui.edit


import android.content.Context
import android.content.Intent
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import ie.wit.umarketplace.firebase.FirebaseDBManager
import ie.wit.umarketplace.helpers.showImagePicker
import ie.wit.umarketplace.models.Location
import ie.wit.umarketplace.models.ProductModel
import timber.log.Timber


class EditViewModel : ViewModel() {

    private var map: GoogleMap? = null

    private val _product = MutableLiveData<ProductModel>()
    var observableProduct: LiveData<ProductModel>
        get() = _product
        set(value) {_product.value = value.value}

    private val _status = MutableLiveData<Boolean>()
    val observableStatus: LiveData<Boolean>
        get() = _status


    fun radioButtonCategory(category: Int){
        _product.value!!.category = category
    }

    fun addProduct(firebaseUser: MutableLiveData<FirebaseUser>, context: Context) {
        _status.value = try {
            var product = ProductModel(title = _product.value!!.title, description = _product.value!!.description, category = _product.value!!.category,
                image = _product.value!!.image, email = firebaseUser.value?.email!!, lat = _product.value!!.lat, lng = _product.value!!.lng, zoom=15f)
            FirebaseDBManager.create(firebaseUser,product, context)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    fun editProduct(userid: String, productid: String, product: ProductModel, context: Context, imageChanged:Boolean) {
        _status.value = try {
            FirebaseDBManager.update(userid,productid, product, context, imageChanged)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    fun getProduct(userid:String, id: String?) {
        try {
            FirebaseDBManager.findById(id!!, _product)
            Timber.i("Detail getProduct() Success : ${
                _product.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Detail getProduct() Error : ${e.message}")
        }
    }

    fun loadImage(image: String, imageView:ImageView){
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

    fun doSelectImage(intentLauncher: ActivityResultLauncher<Intent>) {
        showImagePicker(intentLauncher)
    }

    fun setImage(image: String){
        _product.value!!.image = image
        Timber.i("Product after saving image: ${_product.value}")
    }
    fun setDefaultProduct(userid:String){
        _product.value = ProductModel(email=userid)
    }

    fun setProductLocation(location: Location){
        _product.value!!.lat = location.lat
        _product.value!!.lng = location.lng
        _product.value!!.zoom = location.zoom
    }

    fun getLocation():Location{
        val location = Location()
        if(_product.value?.lat != null){
            location.lat = _product.value!!.lat
            location.lng = _product.value!!.lng
            location.zoom = _product.value!!.zoom
        }else{
            location.lat = 0.0
            location.lng = 0.0
            location.zoom = 0f
        }
        return location
    }

}