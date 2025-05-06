package com.sugardaddy.cafeteriaudb.ui.platillos

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sugardaddy.cafeteriaudb.R

class DetallePlatilloActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_platillo)

        val nombre = intent.getStringExtra("nombre")
        val precio = intent.getDoubleExtra("precio", 0.0)
        val imagen = intent.getIntExtra("imagen", R.drawable.plato_ejemplo)
        val descripcion = intent.getStringExtra("descripcion")

        findViewById<TextView>(R.id.txt_nombre).text = nombre
        findViewById<TextView>(R.id.txt_precio).text = "Precio: $$precio"
        findViewById<ImageView>(R.id.img_plato).setImageResource(imagen)
        findViewById<TextView>(R.id.txt_descripcion).text = descripcion
    }
}