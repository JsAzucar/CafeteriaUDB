package com.sugardaddy.cafeteriaudb.ui.inicio

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.sugardaddy.cafeteriaudb.R

class InicioActivity : AppCompatActivity() {

    private lateinit var vistaInicio: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        vistaInicio = findViewById(R.id.Inicio)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.barra_superior)

        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(this, vistaInicio, toolbar,
            R.string.navigation_drawer_abrir, R.string.navigation_drawer_cerrar)
        vistaInicio.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_inicio -> {
                    Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_configuracion -> {
                    Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show()
                }
            }
            vistaInicio.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onBackPressed() {
        if (vistaInicio.isDrawerOpen(GravityCompat.START)) {
            vistaInicio.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    }
