package ie.wit.umarketplace.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import ie.wit.umarketplace.R
import ie.wit.umarketplace.ui.auth.Login

class SplashScreenActivity : AppCompatActivity() {

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Toast.makeText(this, "Welcome to UMarketplace", Toast.LENGTH_SHORT).show()

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.

        handler = Handler()
        Handler().postDelayed({
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }, 3000) // 3000 is the delayed time in milliseconds.
    }
}