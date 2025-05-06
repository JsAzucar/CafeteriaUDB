package com.sugardaddy.cafeteriaudb.ui.platillos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sugardaddy.cafeteriaudb.R

class PlatillosActivity : AppCompatActivity() {

    private lateinit var recyclerPlatillos: RecyclerView
    private lateinit var adaptador: AdaptadorPlatillos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platillos)

        val tiempoComida = intent.getStringExtra("TIEMPO_COMIDA") ?: "Desayuno"
        title = tiempoComida

        recyclerPlatillos = findViewById(R.id.recycler_platillos)
        recyclerPlatillos.layoutManager = LinearLayoutManager(this)

        val listaPlatillos = obtenerPlatillos(tiempoComida)
        adaptador = AdaptadorPlatillos(listaPlatillos)
        recyclerPlatillos.adapter = adaptador
    }

    private fun obtenerPlatillos(tipo: String): List<Platillo> {
        return listOf(
            Platillo("Plato 1", 10.0, R.drawable.plato_ejemplo),
            Platillo("Plato 2", 10.0, R.drawable.plato_ejemplo),
            Platillo("Plato 3", 10.0, R.drawable.plato_ejemplo)
        )
    }
}