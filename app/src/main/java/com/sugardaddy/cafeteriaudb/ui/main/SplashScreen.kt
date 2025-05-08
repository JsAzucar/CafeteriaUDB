package com.sugardaddy.cafeteriaudb.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sugardaddy.cafeteriaudb.R
import com.sugardaddy.cafeteriaudb.ui.authentication.AuthenticationMenuActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        // Move to MainActivity after 5 second
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,AuthenticationMenuActivity::class.java))
            finish()
        },5000)
    }
}