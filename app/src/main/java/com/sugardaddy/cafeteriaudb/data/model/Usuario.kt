package com.sugardaddy.cafeteriaudb.data.model

data class Usuario(
    var uid: String = "",
    var nombre: String = "",
    var correo: String = "",
    var fechaNacimiento: String = "",
    var rol: String = "usuario"
)
