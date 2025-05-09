package com.sugardaddy.cafeteriaudb.ui.authentication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.sugardaddy.cafeteriaudb.R
import com.sugardaddy.cafeteriaudb.data.model.Usuario
import com.sugardaddy.cafeteriaudb.data.repository.FirebaseRepository
import com.sugardaddy.cafeteriaudb.utils.Validacion

class RegistrarActivity : AppCompatActivity() {

    private lateinit var edtUsuario: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtFecha: EditText
    private lateinit var edtContrasenia: EditText
    private lateinit var edtContraseniaR: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var txtIrSesion: TextView
    private lateinit var imgRegresar: ImageView
    private lateinit var imgNombreAleatorio: ImageView
    private lateinit var progressBar: ProgressBar

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "REGISTER_DEBUG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)

        edtUsuario = findViewById(R.id.edtUsuario)
        edtEmail = findViewById(R.id.edtEmail)
        edtFecha = findViewById(R.id.dpdFecha)
        edtContrasenia = findViewById(R.id.edtContrasenia)
        edtContraseniaR = findViewById(R.id.edtContraseniaR)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        txtIrSesion = findViewById(R.id.txtIrSesion)
        imgRegresar = findViewById(R.id.imgRegresar)
        imgNombreAleatorio = findViewById(R.id.imgNombreAleatorio)
        progressBar = findViewById(R.id.progressBar)

        imgNombreAleatorio.setOnClickListener {
            val randomUser = "user" + (1000..9999).random()
            edtUsuario.setText(randomUser)
        }

        txtIrSesion.setOnClickListener {
            startActivity(Intent(this, IniciarSesionActivity::class.java))
            finish()
        }

        imgRegresar.setOnClickListener {
            finish()
        }

        edtFecha.setOnClickListener {
            mostrarMaterialDatePicker()
        }

        btnRegistrar.setOnClickListener {
            if (!Validacion.validarCampos(this, edtUsuario, edtEmail, edtContrasenia, edtContraseniaR)) {
                return@setOnClickListener
            }

            val nombreUsuario = edtUsuario.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val contrasenia = edtContrasenia.text.toString().trim()
            val fechaNacimiento = edtFecha.text.toString().trim()

            if (contrasenia != edtContraseniaR.text.toString().trim()) {
                edtContraseniaR.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Confirmar registro")
                .setMessage("¿Estás seguro que deseas registrar este usuario?")
                .setPositiveButton("Sí") { _, _ ->
                    btnRegistrar.isEnabled = false
                    progressBar.visibility = View.VISIBLE

                    verificarDuplicadosYRegistrar(nombreUsuario, email, contrasenia, fechaNacimiento)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun mostrarMaterialDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecciona tu fecha de nacimiento")
            .build()

        picker.addOnPositiveButtonClickListener {
            val fechaSeleccionada = picker.headerText
            edtFecha.setText(fechaSeleccionada)
            Log.d(TAG, "Fecha seleccionada: $fechaSeleccionada")
        }

        if (!supportFragmentManager.isStateSaved) {
            picker.show(supportFragmentManager, "date_picker")
        }
    }

    private fun verificarDuplicadosYRegistrar(nombreUsuario: String, email: String, contrasenia: String, fecha: String) {
        FirebaseRepository.obtenerUsuarioPorNombreOEmail(nombreUsuario) { usuarioExistente, _ ->
            if (usuarioExistente != null) {
                edtUsuario.error = "Este nombre de usuario ya existe"
                progressBar.visibility = View.GONE
                btnRegistrar.isEnabled = true
                return@obtenerUsuarioPorNombreOEmail
            }

            FirebaseRepository.correoExisteEnDatabase(email) { correoExiste ->
                if (correoExiste) {
                    edtEmail.error = "Este correo ya está registrado"
                    progressBar.visibility = View.GONE
                    btnRegistrar.isEnabled = true
                    return@correoExisteEnDatabase
                }

                auth.createUserWithEmailAndPassword(email, contrasenia)
                    .addOnSuccessListener {
                        val user = auth.currentUser
                        Log.d(TAG, "Usuario registrado en Firebase Auth: $email")

                        user?.sendEmailVerification()
                        Log.d(TAG, "Correo de verificación enviado a: $email")

                        val nuevoUsuario = Usuario(
                            uid = user?.uid ?: "",
                            nombre = nombreUsuario,
                            correo = email,
                            fechaNacimiento = fecha,
                            rol = "usuario"
                        )

                        if (!nuevoUsuario.uid.isNullOrEmpty()) {
                            FirebaseRepository.guardarUsuario(nuevoUsuario.uid, nuevoUsuario) { exito ->
                                if (!exito) {
                                    Log.e(TAG, "Fallo al guardar usuario en Realtime Database")
                                }
                            }
                        }

                        Toast.makeText(this, "Registro exitoso. Verifica tu correo antes de iniciar sesión.", Toast.LENGTH_LONG).show()

                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this, IniciarSesionActivity::class.java))
                            finish()
                        }, 3000)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al registrar: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e(TAG, "Error al crear usuario: ${e.message}")
                        progressBar.visibility = View.GONE
                        btnRegistrar.isEnabled = true
                    }
            }
        }
    }
}
