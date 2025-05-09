package com.sugardaddy.cafeteriaudb.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sugardaddy.cafeteriaudb.R
import com.sugardaddy.cafeteriaudb.data.model.platillos

class PlatilloAdapter(
    private val lista: List<platillos>,
    private val mostrarAgregar: Boolean = false,
    private val onAgregar: ((platillos) -> Unit)? = null,
    private val onEliminar: (platillos) -> Unit,
    private val onEditar: (platillos) -> Unit
) : RecyclerView.Adapter<PlatilloAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.txt_Nombre)
        val descripcion: TextView = view.findViewById(R.id.txt_Descripcion)
        val precio: TextView = view.findViewById(R.id.txt_Precio)
        val imagen: ImageView = view.findViewById(R.id.imgPlatillo)
        val btnEliminar: Button = view.findViewById(R.id.btn_Eliminar)
        val btnEditar: Button = view.findViewById(R.id.btn_Editar)
        val btnAgregar: Button = view.findViewById(R.id.btn_Agregar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.platillos, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val platillo = lista[position]
        holder.nombre.text = platillo.nombre
        holder.descripcion.text = platillo.descripcion
        holder.precio.text = "$${platillo.precio}"
        holder.btnAgregar.visibility = if (mostrarAgregar) View.VISIBLE else View.GONE
        holder.btnEditar.visibility = if (mostrarAgregar) View.GONE else View.VISIBLE
        holder.btnEliminar.visibility = if (mostrarAgregar) View.GONE else View.VISIBLE
        Glide.with(holder.imagen.context).load(platillo.imagen).into(holder.imagen)

        holder.btnEliminar.setOnClickListener {
            onEliminar(platillo)
        }

        holder.btnEditar.setOnClickListener {
            onEditar(platillo)
        }

        holder.btnAgregar.setOnClickListener {
            onAgregar?.invoke(platillo)
        }
    }
}