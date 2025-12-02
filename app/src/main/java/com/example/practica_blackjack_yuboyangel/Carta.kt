package com.example.practica_blackjack_yuboyangel

/**
 * 1. Definición de palos (Palos)
 * Usar nombres en español, conforme a la terminología de naipes
 */
enum class Palo {
    CORAZONES, // Corazones (Hearts)
    DIAMANTES, // Diamantes (Diamonds)
    TREBOLES, // Tréboles (Clubs)
    PICAS // Picas (Spades)
}

/**
 * 2. Definición de rangos (Rango) y su valor base correspondiente
 * Regla del documento: 2-10 según valor numérico, J/Q/K valen 10, As vale 1 o 11 [cite: 47]
 */
enum class Rango(val valor: Int) {
    DOS(2), TRES(3), CUATRO(4), CINCO(5), SEIS(6), SIETE(7), OCHO(8), NUEVE(9), DIEZ(10),
    JOTA(10), // J
    REINA(10), // Q (Reina)
    REY(10), // K (Rey)
    AS(11); // As (por defecto 11, la lógica lo ajusta dinámicamente)
}

/**
 * 3. Clase Carta
 * imageResId se usa para almacenar el ID de la imagen agregada luego en drawable
 */
data class Carta(val palo: Palo, val rango: Rango, var imageResId: Int = 0) {
    override fun toString(): String {
        return "${rango.name} de ${palo.name}"
    }
}

/**
 * 4. Clase Baraja
 * Encargada de generar 52 cartas, barajar y repartir
 */
class Baraja {
    private val cartas = mutableListOf<Carta>()

    init {
        reiniciar() // Inicializar
    }

    // Reiniciar y generar una baraja nueva
    fun reiniciar() {
        cartas.clear()
        for (p in Palo.values()) {
            for (r in Rango.values()) {
                cartas.add(Carta(p, r))
            }
        }
    }

    // Barajar
    fun barajar() {
        cartas.shuffle()
    }

    // Robar una carta
    fun robarCarta(): Carta? {
        if (cartas.isEmpty()) return null
        return cartas.removeAt(0)
    }
}

/**
 * 5. Clase ManoBlackjack - Lógica principal de la mano
 */
class ManoBlackjack {
    val cartas = mutableListOf<Carta>()

    // Añadir una carta
    fun anadirCarta(carta: Carta) {
        cartas.add(carta)
    }

    // Limpiar la mano (nueva partida)
    fun limpiar() {
        cartas.clear()
    }

    /**
     * Calcular puntuación
     * Maneja automáticamente la lógica de AS (1 o 11)
     */
    fun calcularPuntuacion(): Int {
        var total = 0
        var contadorAses = 0

        // Primera ronda: sumar todos los valores, AS contado como 11 temporalmente
        for (carta in cartas) {
            total += carta.rango.valor
            if (carta.rango == Rango.AS) {
                contadorAses++
            }
        }

        // Segunda ronda: si el total supera 21 y hay AS, convertir AS en 1 (restar 10)
        while (total > 21 && contadorAses > 0) {
            total -= 10
            contadorAses--
        }

        return total
    }

    // Verificar si la mano se ha pasado (Busted)
    fun seHaPasado(): Boolean {
        return calcularPuntuacion() > 21
    }

    // Verificar si es Blackjack
    fun esBlackjack(): Boolean {
        return cartas.size == 2 && calcularPuntuacion() == 21
    }
}
