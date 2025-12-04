package com.example.practica_blackjack_yuboyangel

/**
 * Clase GestorApuestas
 * Encargada de gestionar el dinero del jugador y del crupier.
 */
class GestorApuestas {

    // Propiedades del dinero
    var dineroJugador: Int = 500
        private set // solo leer , no puede modificar fuera

    var dineroCrupier: Int = 2000
        private set

    var apuestaActual: Int = 0
        private set

    /**
     * Intentar realizar una apuesta.
     * Retorna true si es válida, false si no hay fondos.
     */
    fun realizarApuesta(cantidad: Int): Boolean {
        if (cantidad > 10 && cantidad <= dineroJugador) {
            apuestaActual = cantidad
            return true
        }
        return false
    }

    /**
     * Procesar el resultado de la partida y actualizar saldos.
     * Retorna un mensaje describiendo el cambio de dinero.
     */
    fun procesarResultado(ganador: String): String {
        var mensaje = ""

        when (ganador) {
            "Jugador" -> {
                dineroJugador += apuestaActual
                dineroCrupier -= apuestaActual
                mensaje = "\nGanaste +$apuestaActual$"
            }
            "Crupier" -> {
                dineroJugador -= apuestaActual
                dineroCrupier += apuestaActual
                mensaje = "\nPerdiste -$apuestaActual$"
            }
            "Empate" -> {
                mensaje = "\nApuesta devuelta."
            }
        }

        // Reiniciar apuesta para la siguiente mano
        // apuestaActual = 0
        return mensaje
    }

    /**
     * Comprobar si el jugador está en bancarrota
     */
    fun estaEnBancarrota(): Boolean {
        return dineroJugador <= 0
    }

    /**
     * Comprobar si el jugador ha ganado todo el dinero de la banca (Victory Condition)
     */
    fun haGanadoAlCrupier(): Boolean {
        return dineroCrupier <= 0
    }
    /**
     * Reiniciar el juego completo (Game Over)
     */
    fun reiniciarJuego() {
        dineroJugador = 500
        dineroCrupier = 2000
        apuestaActual = 0
    }
}