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

class MenuPlatilloAdapter(
    private val lista: List<platillos>,
    private val onEliminar: (platillos) -> Unit
) : RecyclerView.Adapter<MenuPlatilloAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.img_platillo)
        val nombre = view.findViewById<TextView>(R.id.txt_nombre)
        val precio = view.findViewById<TextView>(R.id.txt_precio)
        val btnEliminar = view.findViewById<Button>(R.id.btn_eliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val platillo = lista[position]
        holder.nombre.text = platillo.nombre
        holder.precio.text = "Precio: $${platillo.precio}"

        Glide.with(holder.itemView.context)
            .load(platillo.imagen)
            .into(holder.img)

        holder.btnEliminar.setOnClickListener {
            onEliminar(platillo)
        }
    }

    override fun getItemCount(): Int = lista.size
}