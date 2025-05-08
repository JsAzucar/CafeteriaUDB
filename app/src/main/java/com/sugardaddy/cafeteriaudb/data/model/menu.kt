package com.sugardaddy.cafeteriaudb.data.model

data class menu (
    val nombre: String = "",
    val platos: Map<String, Boolean> = emptyMap()

)