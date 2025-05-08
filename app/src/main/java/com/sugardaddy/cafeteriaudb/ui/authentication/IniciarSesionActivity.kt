package com.sugardaddy.cafeteriaudb.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sugardaddy.cafeteriaudb.R
import com.sugardaddy.cafeteriaudb.data.repository.FirebaseRepository
import com.sugardaddy.cafeteriaudb.ui.inicio.InicioActivity
import com.sugardaddy.cafeteriaudb.ui.main.MainActivity

class IniciarSesionActivity : AppCompatActivity() {

    private lateinit var edtUsuario: EditText
    private lateinit var edtContrasenia: EditText
    private lateinit var btnAcceder: Button
    private lateinit var txtOlvidar: TextView
    private lateinit var txtRegistrarse: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        edtUsuario = findViewById(R.id.edtUsuario)
        edtContrasenia = findViewById(R.id.edtContrasenia)
        btnAcceder = findViewById(R.id.btnAcceder)
        txtOlvidar = findViewById(R.id.txtOlvidar)
        txtRegistrarse = findViewById(R.id.txtRegistrarse)
        progressBar = findViewById(R.id.progressBar)

        auth = FirebaseAuth.getInstance()

        btnAcceder.setOnClickListener {
            val input = edtUsuario.text.toString().trim()
            val clave = edtContrasenia.text.toString().trim()

            if (input.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, getString(R.string.login_empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnAcceder.isEnabled = false
            progressBar.visibility = View.VISIBLE

            // Buscar al usuario por correo o nombre de usuario
            FirebaseRepository.obtenerUsuarioPorNombreOEmail(input) { usuario, uid ->
                if (usuario != null && uid != null) {
                    // Autenticar usando el email obtenido
                    auth.signInWithEmailAndPassword(usuario.correo, clave)
                        .addOnSuccessListener {
                            val intent = Intent(this, InicioActivity::class.java)
                            intent.putExtra("ROL_USUARIO", usuario.rol)
                            intent.putExtra("NOMBRE_USUARIO", usuario.nombre)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, getString(R.string.login_invalid_credentials), Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                            btnAcceder.isEnabled = true
                        }
                } else {
                    Toast.makeText(this, getString(R.string.login_user_not_found), Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    btnAcceder.isEnabled = true
                }
            }
        }

        /*

        // Ir a pantalla de recuperación
        txtOlvidar.setOnClickListener {
            startActivity(Intent(this, RecuperarContraseniaActivity::class.java))
        }

         */

        // Ir a pantalla de registro
        txtRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegistrarActivity::class.java))
        }
    }
}
