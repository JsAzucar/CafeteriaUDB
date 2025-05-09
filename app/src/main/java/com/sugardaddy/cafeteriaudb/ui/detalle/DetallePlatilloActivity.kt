package com.sugardaddy.cafeteriaudb.ui.detalle

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.sugardaddy.cafeteriaudb.R

class DetallePlatilloActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_platillo)

        val nombre = intent.getStringExtra("nombre")
        val precio = intent.getDoubleExtra("precio", 0.0)
        val imagen = intent.getStringExtra("imagen")
        val descripcion = intent.getStringExtra("descripcion")

        findViewById<TextView>(R.id.txt_nombre).text = nombre
        findViewById<TextView>(R.id.txt_precio).text = "Precio: $$precio"
        val imagenURL = findViewById<ImageView>(R.id.img_plato)
        findViewById<TextView>(R.id.txt_descripcion).text = descripcion
        
        Glide.with(this)
            .load(imagen)
            .placeholder(R.drawable.plato_ejemplo)
            .error(R.drawable.plato_ejemplo)
            .circleCrop()
            .into(imagenURL)

        inicializar()
    }

    private fun inicializar() {
        val toolbar: Toolbar = findViewById(R.id.barra_superior)
        val vistaDescripcion: DrawerLayout = findViewById(R.id.Inicio)

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        vistaDescripcion.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }
}