package com.example.practica_blackjack_yuboyangel

import android.content.Context
import android.util.Log

class Baraja(private val context: Context) {
    private val cartas = mutableListOf<Carta>()

    init {
        reiniciar()
    }

    /**
     * Reiniciar la baraja: limpia la lista y vuelve a generar las 52 cartas
     * buscando sus imágenes correspondientes en los recursos (drawable).
     */
    fun reiniciar() {
        cartas.clear()

        for (p in Palo.values()) {
            for (r in Rango.values()) {
                val carta = Carta(p, r)

                // 1. Obtener el nombre del palo (en minúsculas)
                // Ejemplo: TREBOLES -> "treboles"
                val nombrePalo = p.name.lowercase()

                // 2. Obtener el sufijo de número/letra (Lógica de mapeo)

                val sufijoNombre = when (r) {
                    Rango.DOS -> "02"
                    Rango.TRES -> "03"
                    Rango.CUATRO -> "04"
                    Rango.CINCO -> "05"
                    Rango.SEIS -> "06"
                    Rango.SIETE -> "07"
                    Rango.OCHO -> "08"
                    Rango.NUEVE -> "09"
                    Rango.DIEZ -> "10"
                    Rango.JOTA -> "j"
                    Rango.REINA -> "q"
                    Rango.REY -> "k"
                    Rango.AS -> "a"
                }

                // 3. Construir el nombre final del archivo
                // Resultado ejemplo: "carta_treboles_02", "carta_picas_k"
                val nombreArchivo = "carta_${nombrePalo}_${sufijoNombre}"

                // 4. Buscar el identificador del recurso (Resource ID)
                // Busca en la carpeta 'drawable' un archivo que coincida con el nombre generado
                val resId = context.resources.getIdentifier(
                    nombreArchivo,
                    "drawable",
                    context.packageName
                )

                // Si se encuentra la imagen (id != 0), se asigna a la carta
                if (resId != 0) {
                    carta.imageResId = resId
                }
                else {
                    Log.w("Baraja", "Imagen no encontrada: $nombreArchivo")
                }
                cartas.add(carta)
            }
        }
    }

    // Barajar las cartas (Mezclar)
    fun barajar() {
        cartas.shuffle()
    }

    // Robar (pedir) una carta del mazo
    fun robarCarta(): Carta? {
        if (cartas.isEmpty()) return null
        return cartas.removeAt(0)
    }
}