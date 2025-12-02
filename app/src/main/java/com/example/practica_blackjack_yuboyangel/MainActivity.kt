package com.example.practica_blackjack_yuboyangel

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.practica_blackjack_yuboyangel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mibinding: ActivityMainBinding

    // 1. Declaración de componentes de la UI


    // 2. Objetos de la lógica del juego
    private lateinit var baraja: Baraja
    private val manoJugador = ManoBlackjack()
    private val manoCrupier = ManoBlackjack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mibinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mibinding.root)

        // Inicializar la baraja (pasamos 'context' para cargar recursos de imagen)
        baraja = Baraja(this)

        // Configurar eventos de los botones (Listeners)

        mibinding.btnPedir.setOnClickListener { turnoJugadorPedir() }
        mibinding.btnPlantarse.setOnClickListener { turnoCrupier() }
        mibinding.btnReiniciar.setOnClickListener { iniciarPartida() }

        // Iniciar la primera partida
        iniciarPartida()
    }

    // --- Lógica del flujo del juego ---

    private fun iniciarPartida() {
        // Reiniciar datos
        baraja.reiniciar()
        baraja.barajar()
        manoJugador.limpiar()
        manoCrupier.limpiar()

        // Reiniciar UI
        mibinding.layoutCartasCrupier.removeAllViews()
        mibinding.layoutCartasJugador.removeAllViews()
        mibinding.btnPedir.isEnabled = true
        mibinding.btnPlantarse.isEnabled = true
        mibinding.btnReiniciar.visibility = View.GONE

        // Repartir cartas iniciales: 2 para cada uno
        manoJugador.anadirCarta(baraja.robarCarta()!!)
        manoCrupier.anadirCarta(baraja.robarCarta()!!)
        manoJugador.anadirCarta(baraja.robarCarta()!!)
        manoCrupier.anadirCarta(baraja.robarCarta()!!)

        // Actualizar la interfaz (mostrando la carta oculta del crupier)
        actualizarUI(mostrarOcultaCrupier = true)

        // Comprobar si hay Blackjack inicial (21 puntos directos)
        if (manoJugador.esBlackjack()) {
            Toast.makeText(this, "¡Blackjack!", Toast.LENGTH_SHORT).show()
            turnoCrupier() // Pasar directamente a la fase final
        }
    }

    private fun turnoJugadorPedir() {
        val carta = baraja.robarCarta()
        if (carta != null) {
            manoJugador.anadirCarta(carta)
            actualizarUI(mostrarOcultaCrupier = true)

            // Comprobar si el jugador se ha pasado
            if (manoJugador.seHaPasado()) {
                finalizarPartida("¡Te has pasado! Gana el Crupier.")
            }
        }
    }

    private fun turnoCrupier() {
        // 1. El jugador se planta. Deshabilitar botones.
        mibinding.btnPedir.isEnabled = false
        mibinding.btnPlantarse.isEnabled = false

        // 2. Lógica del Crupier (CPU): Debe pedir carta si tiene menos de 17 puntos
        while (manoCrupier.calcularPuntuacion() < 17) {
            val carta = baraja.robarCarta()
            if (carta != null) {
                manoCrupier.anadirCarta(carta)
            }
        }

        // 3. Revelar todas las cartas del crupier y determinar ganador
        actualizarUI(mostrarOcultaCrupier = false)
        determinarGanador()
    }

    private fun determinarGanador() {
        val puntosJugador = manoJugador.calcularPuntuacion()
        val puntosCrupier = manoCrupier.calcularPuntuacion()

        var mensaje = ""

        if (manoJugador.seHaPasado()) {
            mensaje = "Perdiste. Te pasaste de 21."
        } else if (manoCrupier.seHaPasado()) {
            mensaje = "¡Ganaste! El crupier se pasó."
        } else if (puntosJugador > puntosCrupier) {
            mensaje = "¡Ganaste! ($puntosJugador vs $puntosCrupier)"
        } else if (puntosJugador < puntosCrupier) {
            mensaje = "Perdiste. ($puntosJugador vs $puntosCrupier)"
        } else {
            mensaje = "Empate. ($puntosJugador iguales)"
        }

        mostrarDialogoFin(mensaje)
    }

    // --- Métodos auxiliares para la UI ---

    private fun actualizarUI(mostrarOcultaCrupier: Boolean) {
        // 1. Actualizar zona del jugador
        mibinding.layoutCartasJugador.removeAllViews()
        for (carta in manoJugador.cartas) {
            agregarCartaALayout(carta, mibinding.layoutCartasJugador)
        }
        mibinding.tvPuntosJugador.text = "Puntos: ${manoJugador.calcularPuntuacion()}"

        // 2. Actualizar zona del crupier
        mibinding.layoutCartasCrupier.removeAllViews()

        // Lógica para mostrar o ocultar la carta boca abajo
        if (mostrarOcultaCrupier) {
            // Durante el juego: mostrar la primera carta y la segunda oculta (reverso)
            if (manoCrupier.cartas.isNotEmpty()) {
                agregarCartaALayout(manoCrupier.cartas[0], mibinding.layoutCartasCrupier) // Carta visible

                // Añadir imagen del reverso para la carta oculta
                val ivReverso = ImageView(this)
                ivReverso.setImageResource(R.drawable.reverso)
                val params = LinearLayout.LayoutParams(200, 300) // Tamaño de la carta
                params.setMargins(10, 0, 10, 0)
                ivReverso.layoutParams = params
                mibinding.layoutCartasCrupier.addView(ivReverso)
            }
        } else {
            // Fin del juego: mostrar todas las cartas reales
            for (carta in manoCrupier.cartas) {
                agregarCartaALayout(carta, mibinding.layoutCartasCrupier)
            }
        }
    }

    // Método para añadir dinámicamente un ImageView al layout
    private fun agregarCartaALayout(carta: Carta, layout: LinearLayout) {
        val iv = ImageView(this)
        iv.setImageResource(carta.imageResId) // Usar el ID obtenido en la clase Baraja

        // Configurar tamaño de la imagen (200x300 px) y márgenes
        val params = LinearLayout.LayoutParams(200, 300)
        params.setMargins(10, 0, 10, 0)
        iv.layoutParams = params

        layout.addView(iv)
    }

    private fun finalizarPartida(mensaje: String) {
        mostrarDialogoFin(mensaje)
    }

    // Mostrar diálogo de resultado (AlertDialog)
    private fun mostrarDialogoFin(mensaje: String) {
        mibinding.btnReiniciar.visibility = View.VISIBLE // Mostrar botón de reinicio como opción alternativa

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Fin de la partida")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Nueva Partida") { _, _ ->
            iniciarPartida()
        }
        builder.setNegativeButton("Salir") { _, _ ->
            finish() // Cerrar la app
        }
        builder.setCancelable(false) // Evitar cerrar al tocar fuera
        builder.show()
    }
}