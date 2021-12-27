package ie.wit.umarketplace.main

import android.app.Application
import ie.wit.umarketplace.firebase.FirebaseDBManager
import ie.wit.umarketplace.models.ProductStore
import android.location.Location
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import timber.log.Timber

class UMarketplaceApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("UMarketplace App started")
    }
}
