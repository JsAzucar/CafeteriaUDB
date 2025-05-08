package com.sugardaddy.cafeteriaudb.ui.edicion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sugardaddy.cafeteriaudb.R
import com.sugardaddy.cafeteriaudb.data.adapter.MenuPlatilloAdapter
import com.sugardaddy.cafeteriaudb.data.model.platillos
import com.sugardaddy.cafeteriaudb.data.repository.FirebaseRepository
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EdicionMenuActivity : AppCompatActivity() {

    private lateinit var btnAgregarDesayuno: Button
    private lateinit var btnAgregarAlmuerzo: Button
    private lateinit var btnAgregarCena: Button
    private lateinit var btnDesayuno: Button
    private lateinit var btnAlmuerzo: Button
    private lateinit var btnCena: Button
    private lateinit var vistaDesayuno: RecyclerView
    private lateinit var vistaAlmuerzo : RecyclerView
    private lateinit var vistaCena: RecyclerView
    private lateinit var adapterDesayuno: MenuPlatilloAdapter
    private lateinit var adapterAlmuerzo: MenuPlatilloAdapter
    private lateinit var adapterCena: MenuPlatilloAdapter
    private var listenerMenus: ValueEventListener? = null
    private var listenersPlatos: List<ValueEventListener> = emptyList()

    private val menuDesayuno = mutableListOf<platillos>()
    private val menuAlmuerzo = mutableListOf<platillos>()
    private val menuCena = mutableListOf<platillos>()

    private var desayunoVisible = false
    private var almuerzoVisible = false
    private var cenaVisible = false

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val platosRef = FirebaseDatabase.getInstance().getReference("platos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edicion_menu)
        inicializar()

    }

    private fun inicializar(){
        val toolbar: Toolbar = findViewById(R.id.barra_superior)
        val vistaEditar: DrawerLayout = findViewById(R.id.Inicio)

        btnAgregarDesayuno = findViewById(R.id.btn_agregar_desayuno)
        btnAgregarAlmuerzo = findViewById(R.id.btn_agregar_almuerzo)
        btnAgregarCena = findViewById(R.id.btn_agregar_cena)
        btnDesayuno = findViewById(R.id.btn_desayuno)
        btnAlmuerzo = findViewById(R.id.btn_almuerzo)
        btnCena = findViewById(R.id.btn_cena)
        vistaDesayuno = findViewById(R.id.contenedor_desayuno)
        vistaAlmuerzo = findViewById(R.id.contenedor_almuerzo)
        vistaCena = findViewById(R.id.contenedor_cena)

        adapterDesayuno = MenuPlatilloAdapter(menuDesayuno) {
            FirebaseRepository.eliminarPlatilloMenu("desayuno", it.id,onComplete = {
                Toast.makeText(this, "Platillo eliminado", Toast.LENGTH_SHORT).show()

            })
        }
        adapterAlmuerzo = MenuPlatilloAdapter(menuAlmuerzo) {
            FirebaseRepository.eliminarPlatilloMenu("almuerzo", it.id,onComplete = {
                Toast.makeText(this, "Platillo eliminado", Toast.LENGTH_SHORT).show()

            })
        }
        adapterCena = MenuPlatilloAdapter(menuCena) {
            FirebaseRepository.eliminarPlatilloMenu("cena", it.id,onComplete = {
                Toast.makeText(this, "Platillo eliminado", Toast.LENGTH_SHORT).show()

            })
        }

        vistaDesayuno.layoutManager = LinearLayoutManager(this)
        vistaAlmuerzo.layoutManager = LinearLayoutManager(this)
        vistaCena.layoutManager = LinearLayoutManager(this)

        vistaDesayuno.adapter = adapterDesayuno
        vistaAlmuerzo.adapter = adapterAlmuerzo
        vistaCena.adapter = adapterCena

        onStart()

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        vistaEditar.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        btnAgregarDesayuno.setOnClickListener {
            val intent = Intent(this, EdicionPlatoActivity::class.java)
            intent.putExtra("MENU", "desayuno")
            startActivity(intent)
        }

        btnAgregarAlmuerzo.setOnClickListener {
            val intent = Intent(this, EdicionPlatoActivity::class.java)
            intent.putExtra("MENU", "almuerzo")
            startActivity(intent)
        }

        btnAgregarCena.setOnClickListener {
            val intent = Intent(this, EdicionPlatoActivity::class.java)
            intent.putExtra("MENU", "cena")
            startActivity(intent)
        }

        btnDesayuno.setOnClickListener {
            desayunoVisible = !desayunoVisible
                vistaDesayuno.visibility = if(desayunoVisible) {
                    val fade = AnimationUtils.loadAnimation(this, R.anim.fade)
                    vistaDesayuno.startAnimation(fade)
                    View.VISIBLE
                }else {
                    View.GONE
                }

            btnAgregarDesayuno.visibility = if(desayunoVisible) {
                val fade = AnimationUtils.loadAnimation(this, R.anim.fade)
                btnAgregarDesayuno.startAnimation(fade)
                View.VISIBLE
            }else {
                View.GONE
            }

        }

        btnAlmuerzo.setOnClickListener {
            almuerzoVisible = !almuerzoVisible
            vistaAlmuerzo.visibility = if(almuerzoVisible) {
                val fade = AnimationUtils.loadAnimation(this, R.anim.fade)
                vistaAlmuerzo.startAnimation(fade)
                View.VISIBLE
            }else {
                View.GONE
            }

            btnAgregarAlmuerzo.visibility = if(almuerzoVisible) {
                val fade = AnimationUtils.loadAnimation(this, R.anim.fade)
                btnAgregarAlmuerzo.startAnimation(fade)
                View.VISIBLE
            }else {
                View.GONE
            }

        }

        btnCena.setOnClickListener {
            cenaVisible = !cenaVisible
            vistaCena.visibility = if(cenaVisible) {
                val fade = AnimationUtils.loadAnimation(this, R.anim.fade)
                vistaCena.startAnimation(fade)
                View.VISIBLE
            }else {
                View.GONE
            }

            btnAgregarCena.visibility = if(cenaVisible) {
                val fade = AnimationUtils.loadAnimation(this, R.anim.fade)
                btnAgregarCena.startAnimation(fade)
                View.VISIBLE
            }else {
                View.GONE
            }

        }

    }



    override fun onStart() {
        super.onStart()
        val (menuListener, platillosListeners) = FirebaseRepository.consultarMenu { menus ->
            menuDesayuno.clear()
            menuAlmuerzo.clear()
            menuCena.clear()

            menuDesayuno.addAll(menus["desayuno"] ?: emptyList())
            menuAlmuerzo.addAll(menus["almuerzo"] ?: emptyList())
            menuCena.addAll(menus["cena"] ?: emptyList())

            adapterDesayuno.notifyDataSetChanged()
            adapterAlmuerzo.notifyDataSetChanged()
            adapterCena.notifyDataSetChanged()
        }

        listenerMenus = menuListener
        listenersPlatos = platillosListeners
    }

    override fun onStop() {
        super.onStop()
        listenerMenus?.let {
            dbRef.child("menus").removeEventListener(it)
        }

        val ids = (menuDesayuno + menuAlmuerzo + menuCena).map { it.id }
        for ((i, id) in ids.withIndex()) {
            if (i < listenersPlatos.size) {
                platosRef.child(id).removeEventListener(listenersPlatos[i])
            }
        }
    }
}