package com.sugardaddy.cafeteriaudb.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
    private val TAG = "LOGIN_DEBUG"

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
                mostrarError("Campos vacíos")
                return@setOnClickListener
            }

            btnAcceder.isEnabled = false
            progressBar.visibility = View.VISIBLE

            Log.d(TAG, "Intentando login con input: $input")

            // 1. Intentar login directo como si fuera correo
            auth.signInWithEmailAndPassword(input, clave)
                .addOnSuccessListener {
                    val user = auth.currentUser
                    user?.reload()?.addOnSuccessListener {
                        if (user.isEmailVerified) {
                            Log.d(TAG, "Correo verificado. Buscando en Realtime DB por UID: ${user.uid}")
                            FirebaseRepository.obtenerUsuarioPorUID(user.uid) { usuarioDB ->
                                if (usuarioDB != null) {
                                    navegarSegunRol(usuarioDB.rol, usuarioDB.nombre)
                                } else {
                                    mostrarError("No se encontró información del usuario en la base de datos")
                                }
                            }
                        } else {
                            auth.signOut()
                            mostrarDialogoCorreoNoVerificado(input)
                            btnAcceder.isEnabled = true
                            progressBar.visibility = View.GONE
                        }
                    }
                }
                .addOnFailureListener {
                    Log.w(TAG, "Login directo falló. Intentando buscar por nombre...")

                    // 2. Buscar por nombre en la DB para obtener el correo real
                    FirebaseRepository.obtenerUsuarioPorNombreOEmail(input) { usuario, _ ->
                        if (usuario != null) {
                            val correo = usuario.correo
                            Log.d(TAG, "Usuario encontrado en DB con correo: $correo")

                            auth.signInWithEmailAndPassword(correo, clave)
                                .addOnSuccessListener {
                                    val user = auth.currentUser
                                    user?.reload()?.addOnSuccessListener {
                                        if (user.isEmailVerified) {
                                            navegarSegunRol(usuario.rol, usuario.nombre)
                                        } else {
                                            auth.signOut()
                                            mostrarDialogoCorreoNoVerificado(correo)
                                            btnAcceder.isEnabled = true
                                            progressBar.visibility = View.GONE
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    mostrarError("Credenciales inválidas")
                                }
                        } else {
                            mostrarError("Usuario no encontrado")
                        }
                    }
                }
        }

        txtOlvidar.setOnClickListener {
            startActivity(Intent(this, RecuperarContraseniaActivity::class.java))
        }

        txtRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegistrarActivity::class.java))
        }
    }

    private fun mostrarError(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        progressBar.visibility = View.GONE
        btnAcceder.isEnabled = true
        Log.e(TAG, "Error mostrado al usuario: $msg")
    }

    private fun navegarSegunRol(rol: String, usuario: String) {
        Log.d(TAG, "Navegando según rol: $rol")
        val intent = Intent(this, InicioActivity::class.java)
        intent.putExtra("ROL_USUARIO", rol)
        intent.putExtra("NOMBRE_USUARIO", usuario)
        startActivity(intent)
        finish()
    }

    private fun mostrarDialogoCorreoNoVerificado(email: String) {
        Log.d(TAG, "Mostrando diálogo de verificación para: $email")
        AlertDialog.Builder(this)
            .setTitle("Correo no verificado")
            .setMessage("Debes verificar tu correo antes de iniciar sesión. ¿Deseas reenviar el correo de verificación a:\n\n$email ?")
            .setPositiveButton("Reenviar") { _, _ ->
                FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                Toast.makeText(this, "Correo de verificación reenviado", Toast.LENGTH_LONG).show()
                Log.d(TAG, "Correo de verificación reenviado a: $email")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
