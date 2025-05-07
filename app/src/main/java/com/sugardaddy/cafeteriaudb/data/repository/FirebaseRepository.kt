package com.sugardaddy.cafeteriaudb.data.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sugardaddy.cafeteriaudb.data.model.Usuario

object FirebaseRepository {

    private val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("usuarios")

    fun guardarUsuario(uid: String, usuario: Usuario) {
        db.child(uid).setValue(usuario)
    }

    fun obtenerUsuarioPorUID(uid: String, callback: (Usuario?) -> Unit) {
        db.child(uid).get().addOnSuccessListener { snapshot ->
            val usuario = snapshot.getValue(Usuario::class.java)
            callback(usuario)
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun actualizarRol(uid: String, nuevoRol: String) {
        db.child(uid).child("rol").setValue(nuevoRol)
    }

    fun obtenerTodosLosUsuarios(callback: (List<Usuario>) -> Unit) {
        db.get().addOnSuccessListener { snapshot ->
            val lista = mutableListOf<Usuario>()
            snapshot.children.forEach { child ->
                val usuario = child.getValue(Usuario::class.java)
                if (usuario != null) {
                    lista.add(usuario)
                }
            }
            callback(lista)
        }.addOnFailureListener {
            callback(emptyList())
        }
    }
    // 🔍 Nuevo: busca un usuario por su nombre de usuario o correo electrónico
    fun obtenerUsuarioPorNombreOEmail(input: String, callback: (Usuario?, String?) -> Unit) {
        db.get().addOnSuccessListener { snapshot ->
            for (child in snapshot.children) {
                val usuario = child.getValue(Usuario::class.java)
                if (usuario != null &&
                    (usuario.nombre.equals(input, ignoreCase = true)
                            || usuario.correo.equals(input, ignoreCase = true))
                ) {
                    callback(usuario, child.key)
                    return@addOnSuccessListener
                }
            }
            callback(null, null) // No encontrado
        }.addOnFailureListener {
            callback(null, null)
        }
    }
}
