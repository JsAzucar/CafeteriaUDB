package com.sugardaddy.cafeteriaudb.data.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sugardaddy.cafeteriaudb.R
import com.sugardaddy.cafeteriaudb.data.model.platillos
import com.sugardaddy.cafeteriaudb.ui.detalle.DetallePlatilloActivity

class AdaptadorVistaPlatillos(private val lista: List<platillos>) :
    RecyclerView.Adapter<AdaptadorVistaPlatillos.PlatilloViewHolder>() {

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
        Glide.with(holder.imagen.context)
            .load(platillo.imagen)
            .circleCrop()
            .into(holder.imagen)


        holder.itemView.setOnClickListener {
            val contexto = holder.itemView.context
            val intent = Intent(contexto, DetallePlatilloActivity::class.java).apply {
                putExtra("nombre", platillo.nombre)
                putExtra("precio", platillo.precio)
                putExtra("imagen", platillo.imagen)
                putExtra("descripcion", platillo.descripcion)
            }
            contexto.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = lista.size
}