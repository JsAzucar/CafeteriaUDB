package com.sugardaddy.cafeteriaudb.ui.edicion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.sugardaddy.cafeteriaudb.R

class MenuEdicionActivity : AppCompatActivity() {


    private lateinit var btnEditarMenu: Button
    private lateinit var btnEditarPlato: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_edicion)
        inicializar()
    }

    private fun inicializar() {
        val toolbar: Toolbar = findViewById(R.id.barra_superior)
        val vistaEditar: DrawerLayout = findViewById(R.id.Inicio)
        btnEditarMenu = findViewById(R.id.btn_editar_menu)
        btnEditarPlato = findViewById(R.id.btn_editar_plato)

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnEditarMenu.setOnClickListener {
            val i = Intent(this,EdicionMenuActivity::class.java)
            startActivity(i)
        }

        btnEditarPlato.setOnClickListener {
            val i = Intent(this,EdicionPlatoActivity::class.java)
            startActivity(i)
        }
    }
}