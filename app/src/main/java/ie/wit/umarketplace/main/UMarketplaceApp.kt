package ie.wit.umarketplace.main

import android.app.Application
import timber.log.Timber

class UMarketplaceApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("UMarketplace App started")
    }
}
