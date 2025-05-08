package com.sugardaddy.cafeteriaudb.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class platillos (
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val imagen: String = ""

) : Parcelable