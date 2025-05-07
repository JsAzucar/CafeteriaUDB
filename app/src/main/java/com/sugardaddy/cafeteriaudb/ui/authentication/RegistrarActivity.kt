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
import com.google.firebase.database.FirebaseDatabase
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
    private val db = FirebaseDatabase.getInstance().getReference("usuarios")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)

        // Vincular vistas
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
            Log.d("Registro", "Nombre aleatorio generado: $randomUser")
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
                Log.d("Registro", "Validación de campos fallida.")
                return@setOnClickListener
            }

            val nombreUsuario = edtUsuario.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val contrasenia = edtContrasenia.text.toString().trim()
            val fechaNacimiento = edtFecha.text.toString().trim()

            if (contrasenia != edtContraseniaR.text.toString().trim()) {
                edtContraseniaR.error = "Las contraseñas no coinciden"
                Log.d("Registro", "Las contraseñas no coinciden.")
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Confirmar registro")
                .setMessage("¿Estás seguro que deseas registrar este usuario?")
                .setPositiveButton("Sí") { _, _ ->
                    btnRegistrar.isEnabled = false
                    progressBar.visibility = View.VISIBLE

                    Log.d("Registro", "Confirmación positiva, iniciando verificación de duplicados.")
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

        picker.addOnPositiveButtonClickListener { selection ->
            val fechaSeleccionada = picker.headerText
            edtFecha.setText(fechaSeleccionada)
            Log.d("Registro", "Fecha seleccionada: $fechaSeleccionada")
        }

        if (!supportFragmentManager.isStateSaved) {
            picker.show(supportFragmentManager, "date_picker")
        } else {
            Log.e("Registro", "No se pudo mostrar el DatePicker porque el estado del fragmento ya fue guardado.")
        }
    }

    private fun verificarDuplicadosYRegistrar(nombreUsuario: String, email: String, contrasenia: String, fecha: String) {
        db.orderByChild("nombre").equalTo(nombreUsuario).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                edtUsuario.error = "Este nombre de usuario ya existe"
                Log.d("Registro", "Nombre duplicado encontrado.")
                return@addOnSuccessListener
            }

            db.orderByChild("correo").equalTo(email).get().addOnSuccessListener { correoSnap ->
                if (correoSnap.exists()) {
                    edtEmail.error = "Este correo ya está registrado"
                    Log.d("Registro", "Correo duplicado encontrado.")
                    return@addOnSuccessListener
                }

                Log.d("Registro", "Intentando crear usuario con correo: $email")

                auth.createUserWithEmailAndPassword(email, contrasenia)
                    .addOnCompleteListener { task ->

                        btnRegistrar.isEnabled = true
                        progressBar.visibility = View.GONE

                        if (task.isSuccessful) {
                            val uid = auth.currentUser?.uid
                            if (uid == null) {
                                Log.e("Registro", "UID no disponible luego del registro.")
                                Toast.makeText(this, "Error: UID no disponible", Toast.LENGTH_LONG).show()
                                return@addOnCompleteListener
                            }

                            val nuevoUsuario = Usuario(
                                uid = uid,
                                nombre = nombreUsuario,
                                correo = email,
                                fechaNacimiento = fecha,
                                rol = "usuario"
                            )


                            FirebaseRepository.guardarUsuario(uid, nuevoUsuario)

                            Log.d("Registro", "Usuario guardado en Realtime Database.")
                            Log.d("DEBUG", "Usuario creado: $nuevoUsuario")
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()

                            Handler(Looper.getMainLooper()).postDelayed({
                                startActivity(Intent(this, IniciarSesionActivity::class.java))
                                finish()
                            },3000)

                        } else {
                            val errorMsg = task.exception?.message ?: "Error desconocido"
                            Log.e("Registro", "Fallo al crear usuario: $errorMsg")
                            Toast.makeText(this, "Error al registrar: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    }
            }.addOnFailureListener {
                Log.e("Registro", "Error consultando correo: ${it.message}")
            }
        }.addOnFailureListener {
            Log.e("Registro", "Error consultando nombre: ${it.message}")
        }
    }
}

