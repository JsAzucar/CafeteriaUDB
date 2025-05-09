package com.sugardaddy.cafeteriaudb.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sugardaddy.cafeteriaudb.R
import com.sugardaddy.cafeteriaudb.data.adapter.AdaptadorVistaPlatillos
import com.sugardaddy.cafeteriaudb.data.adapter.MenuPlatilloAdapter
import com.sugardaddy.cafeteriaudb.data.model.platillos
import com.sugardaddy.cafeteriaudb.ui.edicion.EdicionMenuActivity
import com.sugardaddy.cafeteriaudb.ui.edicion.MenuEdicionActivity
import com.sugardaddy.cafeteriaudb.data.repository.FirebaseRepository

class InicioActivity : AppCompatActivity() {

    private lateinit var vistaInicio: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var btnDesayuno: Button
    private lateinit var btnAlmuerzo: Button
    private lateinit var btnCena: Button
    private lateinit var btnEditar: Button

    private val menuDesayuno = mutableListOf<platillos>()
    private val menuAlmuerzo = mutableListOf<platillos>()
    private val menuCena = mutableListOf<platillos>()

    private lateinit var vistaDesayuno: RecyclerView
    private lateinit var vistaAlmuerzo : RecyclerView
    private lateinit var vistaCena: RecyclerView

    private lateinit var adapterDesayuno: AdaptadorVistaPlatillos
    private lateinit var adapterAlmuerzo: AdaptadorVistaPlatillos
    private lateinit var adapterCena: AdaptadorVistaPlatillos

    private var listenerMenus: ValueEventListener? = null
    private var listenersPlatos: List<ValueEventListener> = emptyList()

    private var desayunoVisible = false
    private var almuerzoVisible = false
    private var cenaVisible = false

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val platosRef = FirebaseDatabase.getInstance().getReference("platos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        inicializar()


    }

    override fun onBackPressed() {
        if (vistaInicio.isDrawerOpen(GravityCompat.START)) {
            vistaInicio.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun inicializar(){

        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "usuario"
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "usuario"

        vistaInicio = findViewById(R.id.Inicio)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.barra_superior)
        btnDesayuno = findViewById(R.id.btn_desayuno)
        btnAlmuerzo = findViewById(R.id.btn_almuerzo)
        btnCena = findViewById(R.id.btn_cena)
        btnEditar = findViewById(R.id.btn_editar)

        val headerView = navView.getHeaderView(0)

        vistaDesayuno = findViewById(R.id.recycler_platillos_desayuno)
        vistaAlmuerzo = findViewById(R.id.recycler_platillos_almuerzo)
        vistaCena = findViewById(R.id.recycler_platillos_cena)

        if (rolUsuario == "administrador") {
            //txtBienvenida.text = getString(R.string.main_welcome_admin)
            btnEditar.visibility = Button.VISIBLE
        } else {
            //txtBienvenida.text = getString(R.string.main_welcome_user)
            btnEditar.visibility = Button.GONE
        }

        Log.d("usuario", "$nombreUsuario")
        val txtTitulo = headerView.findViewById<TextView>(R.id.titulo)
        txtTitulo.text = "Bienvenido, $nombreUsuario"
        val txtRol = headerView.findViewById<TextView>(R.id.rol)
        txtRol.text = "Rol: $rolUsuario"

        setSupportActionBar(toolbar)
        FirebaseRepository.crearNodos()

        toggle = ActionBarDrawerToggle(this, vistaInicio, toolbar,
            R.string.navigation_drawer_abrir, R.string.navigation_drawer_cerrar)
        vistaInicio.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_inicio -> {
                    Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_configuracion -> {
                    Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show()
                }
            }
            vistaInicio.closeDrawer(GravityCompat.START)
            true
        }

        adapterDesayuno = AdaptadorVistaPlatillos(menuDesayuno)
        adapterAlmuerzo = AdaptadorVistaPlatillos(menuAlmuerzo)
        adapterCena = AdaptadorVistaPlatillos(menuCena)

        vistaDesayuno.layoutManager = LinearLayoutManager(this)
        vistaAlmuerzo.layoutManager = LinearLayoutManager(this)
        vistaCena.layoutManager = LinearLayoutManager(this)

        vistaDesayuno.adapter = adapterDesayuno
        vistaAlmuerzo.adapter = adapterAlmuerzo
        vistaCena.adapter = adapterCena

        onStart()

        btnEditar.setOnClickListener {
        val i = Intent(this,MenuEdicionActivity::class.java)
        startActivity(i)
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
