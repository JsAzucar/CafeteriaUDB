package com.sugardaddy.cafeteriaudb

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseRef: DatabaseReference //En una referencia a un nodo en Firebase Realtime database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Obtener la referencia de la base de datos
        firebaseRef = FirebaseDatabase.getInstance().reference

        // Escribir datos en Firebase
        escribirEnFirebase()

    }
    private fun escribirEnFirebase() {
        val mensaje = mapOf(
            "mensaje" to "¡Hola Firebase!"
        )

        firebaseRef.child("mensajes").push().setValue(mensaje)
            .addOnSuccessListener {
                Log.d("Firebase", "Datos guardados exitosamente en Firebase")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al guardar datos: ${e.message}", e)
            }
    }
}