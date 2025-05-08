package com.sugardaddy.cafeteriaudb.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.sugardaddy.cafeteriaudb.data.model.menu
import com.sugardaddy.cafeteriaudb.data.model.platillos
import java.util.UUID

object FirebaseRepository {
    private val dbRef = FirebaseDatabase.getInstance().reference
    private val platosRef = FirebaseDatabase.getInstance().getReference("platos")

    val menus = listOf("desayuno","almuerzo", "cena")

    fun crearNodos(){
        menus.forEach { nombreMenu ->
            dbRef.child("menus").child(nombreMenu).get().addOnSuccessListener {
                if (!it.exists()) {
                    val nuevoMenu = menu(nombre = nombreMenu)
                    dbRef.child("menus").child(nombreMenu).setValue(nuevoMenu)
                }
            }
        }

        platosRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                val platoEjemplo = platillos(
                    id = "1",
                    nombre = "Ejemplo de platillo",
                    descripcion = "Descripción de prueba",
                    precio = 3.5,
                    imagen = "https://placehold.co/600x400"
                )
                platosRef.child(platoEjemplo.id).setValue(platoEjemplo)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Nodo platillos creado")
                    }
            } else {
                Log.d("Firebase", "Nodo platillos ya existe")
            }
        }


    }

    fun consultarMenu(callback: (Map<String, List<platillos>>) -> Unit): Pair<ValueEventListener, List<ValueEventListener>> {
        val platosListeners = mutableListOf<ValueEventListener>()

        val menuListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val resultado = mutableMapOf<String, MutableList<platillos>>()
                val idsPorMenu = mutableMapOf<String, List<String>>()
                val idsGlobales = mutableSetOf<String>()

                snapshot.children.forEach { menuSnap ->
                    val nombreMenu = menuSnap.key ?: return@forEach
                    val platosMap = menuSnap.child("platos").value as? Map<String, Boolean> ?: emptyMap()
                    val ids = platosMap.keys.toList()
                    resultado[nombreMenu] = mutableListOf()
                    idsPorMenu[nombreMenu] = ids
                    idsGlobales.addAll(ids)
                }

                if (idsGlobales.isEmpty()) {
                    callback(resultado)
                    return
                }

                var completados = 0
                idsGlobales.forEach { id ->
                    val listener = object : ValueEventListener {
                        override fun onDataChange(snap: DataSnapshot) {
                            val plato = snap.getValue(platillos::class.java)
                            if (plato != null) {
                                idsPorMenu.forEach { (menuNombre, ids) ->
                                    if (id in ids) {
                                        resultado[menuNombre]?.removeAll { it.id == id }
                                        resultado[menuNombre]?.add(plato)
                                    }
                                }
                            }

                            completados++
                            if (completados == idsGlobales.size) {
                                callback(resultado)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Firebase", "Error al actualizar platillo $id: ${error.message}")
                        }
                    }

                    platosRef.child(id).addValueEventListener(listener)
                    platosListeners.add(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al actualizar menús: ${error.message}")
            }
        }

        dbRef.child("menus").addValueEventListener(menuListener)
        return Pair(menuListener, platosListeners)
    }

    fun eliminarPlatilloMenu(nombreMenu: String, idPlatillo: String, onComplete: () -> Unit) {
        dbRef.child("menus").child(nombreMenu).child("platos")
            .child(idPlatillo).removeValue().addOnSuccessListener {
                onComplete()
            }
    }

    fun agregarPlatillo(nombre: String, descripcion:String, precio:Double, imagen:String, onSuccess: () -> Unit, onError: (Exception) -> Unit){
        val nuevoId = platosRef.push().key ?: return onError(Exception("Error generando ID"))
        val platillo = platillos(
            id = nuevoId,
            nombre = nombre,
            descripcion = descripcion,
            precio = precio,
            imagen = imagen
        )

        platosRef.child(nuevoId).setValue(platillo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun editarPlatillo(
        platillo: platillos,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        platosRef.child(platillo.id).setValue(platillo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun agregarPlatilloMenu(menuNombre: String, platilloId: String, callback: (Boolean) -> Unit) {
        val menuRef = FirebaseDatabase.getInstance().getReference("menus/$menuNombre/platos")

        menuRef.child(platilloId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                callback(false)
            } else {
                menuRef.child(platilloId).setValue(true).addOnSuccessListener {
                    callback(true)
                }.addOnFailureListener {
                    callback(false)
                }
            }
        }
    }



}