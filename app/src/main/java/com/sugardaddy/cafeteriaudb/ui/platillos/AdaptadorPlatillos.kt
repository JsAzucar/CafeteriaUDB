package com.sugardaddy.cafeteriaudb.ui.platillos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sugardaddy.cafeteriaudb.R

class AdaptadorPlatillos(private val lista: List<Platillo>) :
    RecyclerView.Adapter<AdaptadorPlatillos.PlatilloViewHolder>() {

    class PlatilloViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagen: ImageView = itemView.findViewById(R.id.imagen_plato)
        val nombre: TextView = itemView.findViewById(R.id.nombre_plato)
        val precio: TextView = itemView.findViewById(R.id.precio_plato)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatilloViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_platillo, parent, false)
        return PlatilloViewHolder(vista)
    }

    override fun onBindViewHolder(holder: PlatilloViewHolder, position: Int) {
        val platillo = lista[position]
        holder.nombre.text = platillo.nombre
        holder.precio.text = "$${platillo.precio}"
        holder.imagen.setImageResource(platillo.imagenResId)
    }

    override fun getItemCount(): Int = lista.size
}