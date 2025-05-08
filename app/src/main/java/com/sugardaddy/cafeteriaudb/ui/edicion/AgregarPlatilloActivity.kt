package com.sugardaddy.cafeteriaudb.ui.edicion

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.sugardaddy.cafeteriaudb.R
import com.sugardaddy.cafeteriaudb.data.model.platillos
import com.sugardaddy.cafeteriaudb.data.repository.FirebaseRepository

class AgregarPlatilloActivity : AppCompatActivity() {

    private lateinit var inputNombre: EditText
    private lateinit var inputDescripcion: EditText
    private lateinit var inputImagen: EditText
    private lateinit var inputPrecio: EditText
    private lateinit var btnAgregar: Button
    private lateinit var btnSubirImagen: Button
    private var platilloAEditar: platillos? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_platillo)
        inputNombre = findViewById(R.id.input_nombre)
        inputDescripcion = findViewById(R.id.input_descripcion)
        inputPrecio = findViewById(R.id.input_precio)
        inputImagen = findViewById(R.id.input_imagen)
        btnAgregar = findViewById(R.id.btn_agregar)
        btnAgregar = findViewById(R.id.btn_subir_imagen)

        inicializar()
    }

    private fun inicializar(){

        val toolbar: Toolbar = findViewById(R.id.barra_superior)
        val vistaPlatillo: DrawerLayout = findViewById(R.id.Inicio)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        vistaPlatillo.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        platilloAEditar = intent.getParcelableExtra("PLATILLO_EDITAR")

        if (platilloAEditar != null) {
            btnAgregar.text = "Guardar cambios"

            inputNombre.setText(platilloAEditar!!.nombre)
            inputDescripcion.setText(platilloAEditar!!.descripcion)
            inputPrecio.setText(platilloAEditar!!.precio.toString())
            inputImagen.setText(platilloAEditar!!.imagen)
        }

        btnSubirImagen = findViewById(R.id.btn_subir_imagen)

        btnSubirImagen.setOnClickListener {
            seleccionarImagen.launch("image/*")
        }

        btnAgregar.setOnClickListener {
            val nombre = inputNombre.text.toString()
            val descripcion = inputDescripcion.text.toString()
            val precioTexto = inputPrecio.text.toString()
            val imagen = inputImagen.text.toString()


            if (nombre.isBlank() || descripcion.isBlank() || precioTexto.isBlank() || imagen.isBlank()) {
                Toast.makeText(this, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val precio = precioTexto.toDoubleOrNull()
            if (precio == null) {
                Toast.makeText(this, "Precio inválido, ingrese el precio correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (platilloAEditar != null) {
                val platilloActualizado = platilloAEditar!!.copy(
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    imagen = imagen
                )
                FirebaseRepository.editarPlatillo(platilloActualizado,onSuccess = {
                    Toast.makeText(this, "Platillo actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                },
                    onError = {
                        Toast.makeText(this, "Error al actualizar: ${it.message}", Toast.LENGTH_LONG).show()
                    })
            }else{

                FirebaseRepository.agregarPlatillo(
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    imagen = imagen,
                    onSuccess = {
                        Toast.makeText(this, "Platillo agregado", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onError = {
                        Toast.makeText(this, "Error al guardar: ${it.message}", Toast.LENGTH_LONG).show()
                    }
                )

            }
                finish()

        }
    }

    private val seleccionarImagen = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            FirebaseRepository.subirImagenFirebase(this,it) { url ->
                inputImagen.setText(url)
                Toast.makeText(this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }


}