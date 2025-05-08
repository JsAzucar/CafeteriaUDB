package com.sugardaddy.cafeteriaudb.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sugardaddy.cafeteriaudb.R
import com.sugardaddy.cafeteriaudb.ui.authentication.IniciarSesionActivity

class MainActivity : AppCompatActivity() {

    private lateinit var txtBienvenida: TextView
    private lateinit var btnAdminPanel: Button
    private lateinit var btnVerMenu: Button
    private lateinit var btnMiPerfil: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtBienvenida = findViewById(R.id.txtBienvenida)
        btnAdminPanel = findViewById(R.id.btnAdminPanel)
        btnVerMenu = findViewById(R.id.btnVerMenu)
        btnMiPerfil = findViewById(R.id.btnMiPerfil)

        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "usuario"
        val nombreUsuario = intent.getStringExtra("ROL_USUARIO") ?: "usuario"

        if (rolUsuario == "administrador") {
            txtBienvenida.text = getString(R.string.main_welcome_admin)
            btnAdminPanel.visibility = Button.VISIBLE
        } else {
            txtBienvenida.text = getString(R.string.main_welcome_user)
            btnAdminPanel.visibility = Button.GONE
        }

        // Acciones de botones (ejemplo)
        btnVerMenu.setOnClickListener {
            // Ir a pantalla de ver menú
        }

        btnMiPerfil.setOnClickListener {
            // Ir a pantalla de perfil
        }

        btnAdminPanel.setOnClickListener {
            // Ir a pantalla de administración
        }
    }
    override fun onBackPressed() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Deseas cerrar tu sesión?")
            .setPositiveButton("Sí") { _, _ ->
                // Cierra sesión y vuelve a Login
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IniciarSesionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

}
