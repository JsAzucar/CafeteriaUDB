package com.sugardaddy.cafeteriaudb.ui.edicion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sugardaddy.cafeteriaudb.R
import com.sugardaddy.cafeteriaudb.data.model.platillos
import com.sugardaddy.cafeteriaudb.data.adapter.PlatilloAdapter
import com.sugardaddy.cafeteriaudb.data.repository.FirebaseRepository

class EdicionPlatoActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlatilloAdapter
    private val listaPlatillos = mutableListOf<platillos>()
    private val dbRef = FirebaseDatabase.getInstance().getReference("platos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edicion_plato)

        val menuObjetivo = intent.getStringExtra("MENU")
        val estaAgregandoAMenu = !menuObjetivo.isNullOrEmpty()
        val desdeEditarMenu = menuObjetivo != null

        recyclerView = findViewById(R.id.recyclerPlatillos)
        val toolbar: Toolbar = findViewById(R.id.barra_superior)
        val vistaPlatillo: DrawerLayout = findViewById(R.id.Inicio)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(desdeEditarMenu){
            findViewById<Button>(R.id.btnAgregarPlatillo).visibility = View.GONE
        }

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        vistaPlatillo.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        adapter = PlatilloAdapter(
            listaPlatillos,
            onEliminar = { platillo ->
                eliminarPlatillo(platillo)
            },
            onEditar = { platillo ->
                val intent = Intent(this, AgregarPlatilloActivity::class.java)
                intent.putExtra("PLATILLO_EDITAR", platillo)
                startActivity(intent)
            },
            mostrarAgregar = estaAgregandoAMenu,
            onAgregar = { platillo ->
                FirebaseRepository.agregarPlatilloMenu(menuObjetivo!!, platillo.id) { agregado ->
                    if (agregado) {
                        Toast.makeText(this, "Platillo agregado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Ya está en el menú", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnAgregarPlatillo).setOnClickListener {
            startActivity(Intent(this, AgregarPlatilloActivity::class.java))
        }

        cargarPlatillos()
    }

    private fun cargarPlatillos() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaPlatillos.clear()
                for (child in snapshot.children) {
                    val platillo = child.getValue(platillos::class.java)
                    platillo?.let { listaPlatillos.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun eliminarPlatillo(platillo: platillos) {
        dbRef.child(platillo.id).removeValue().addOnSuccessListener {
            eliminarReferenciaMenus(platillo.id)
        }
    }

    private fun eliminarReferenciaMenus(idPlatillo: String) {
        val menusRef = FirebaseDatabase.getInstance().getReference("menus")
        menusRef.get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach { menuSnap ->
                val platillosIdsRef = menuSnap.child("platillosIds").ref
                platillosIdsRef.child(idPlatillo).removeValue()
            }
        }
    }
}