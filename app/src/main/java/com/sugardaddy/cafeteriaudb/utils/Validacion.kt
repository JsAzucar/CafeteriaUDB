package com.sugardaddy.cafeteriaudb.utils

import android.content.Context
import android.util.Patterns
import android.widget.EditText

object Validacion {

    fun esNombreValido(nombre: String): Boolean {
        return nombre.isNotEmpty() && nombre.length >= 4
    }
    fun esCorreoValido(correo: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }
    fun esContrasenaValida(contrasena: String): Boolean {
        return contrasena.length >= 6
    }
    fun sonContrasenasIguales(con1: String, con2: String): Boolean {
        return con1 == con2
    }
    fun validarCampos(
        context: Context,
        nombre: EditText,
        correo: EditText,
        contrasena: EditText,
        contrasenaR: EditText
    ): Boolean {
        if (!esNombreValido(nombre.text.toString())) {
            nombre.error = "Nombre inválido (mínimo 4 letras)"
            return false
        }
        if (!esCorreoValido(correo.text.toString())) {
            correo.error = "Correo inválido"
            return false
        }
        if (!esContrasenaValida(contrasena.text.toString())) {
            contrasena.error = "Contraseña débil (mínimo 6 caracteres)"
            return false
        }
        if (!sonContrasenasIguales(contrasena.text.toString(), contrasenaR.text.toString())) {
            contrasenaR.error = "Las contraseñas no coinciden"
            return false
        }
        return true
    }

}