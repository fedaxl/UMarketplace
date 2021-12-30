package ie.wit.umarketplace.main

import android.app.AlertDialog
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import ie.wit.umarketplace.R
import timber.log.Timber

class UMarketplaceApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("UMarketplace App started")
    }
}