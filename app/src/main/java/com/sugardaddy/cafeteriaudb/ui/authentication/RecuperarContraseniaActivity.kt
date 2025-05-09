package com.sugardaddy.cafeteriaudb.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sugardaddy.cafeteriaudb.R
import com.sugardaddy.cafeteriaudb.data.repository.FirebaseRepository

class RecuperarContraseniaActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var btnEnviar: Button
    private lateinit var txtInstruccion: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnRegresar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasenia)

        edtEmail = findViewById(R.id.edtEmail)
        btnEnviar = findViewById(R.id.btnEnviar)
        txtInstruccion = findViewById(R.id.txtInstruccion)
        progressBar = findViewById(R.id.progressBar)
        btnRegresar = findViewById(R.id.btnRegresar)

        btnEnviar.setOnClickListener {
            val email = edtEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Ingrese un correo electrónico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            FirebaseRepository.correoExisteEnDatabase(email) { existe ->
                if (existe) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, "Correo enviado. Revise su bandeja de entrada.", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, IniciarSesionActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, "Error al enviar el correo: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Este correo no está registrado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnRegresar.setOnClickListener {
            finish()
        }
    }
}
