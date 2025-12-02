package com.example.practica_blackjack_yuboyangel

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.practica_blackjack_yuboyangel.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mibinding: ActivityMainBinding

    // Objetos de la lógica del juego
    private lateinit var baraja: Baraja
    private val manoJugador = ManoBlackjack()
    private val manoCrupier = ManoBlackjack()

    // Nuevo: Gestor de Apuestas (Delegamos la lógica del dinero aquí)
    private val gestorApuestas = GestorApuestas()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mibinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mibinding.root)

        baraja = Baraja(this)

        mibinding.btnPedir.setOnClickListener { turnoJugadorPedir() }
        mibinding.btnPlantarse.setOnClickListener { turnoCrupier() }

        // Al reiniciar, mostramos dialogo de apuesta
        mibinding.btnReiniciar.setOnClickListener { mostrarDialogoApuesta() }

        actualizarTextoDinero()
        mostrarDialogoApuesta()
    }

    // --- Lógica de Apuestas ---

    private fun mostrarDialogoApuesta() {
        // 1. Usar el gestor para comprobar bancarrota
        if (gestorApuestas.estaEnBancarrota()) {
            mostrarDialogoGameOver("¡Estás en bancarrota! Fin del juego.")
            return
        }

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.hint = "Mínimo 10"

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Realizar Apuesta")
        // 2. Leer datos del gestor
        builder.setMessage("Tienes: ${gestorApuestas.dineroJugador}$\nBanca: ${gestorApuestas.dineroCrupier}$")
        builder.setView(input)
        builder.setCancelable(false)

        builder.setPositiveButton("Apostar") { _, _ ->
            val cantidadStr = input.text.toString()
            if (cantidadStr.isNotEmpty()) {
                val cantidad = cantidadStr.toInt()

                // 3. Pedir al gestor que valide la apuesta
                if (gestorApuestas.realizarApuesta(cantidad)) {
                    iniciarPartida() // Éxito
                } else {
                    Toast.makeText(this, "Apuesta no válida (Fondos insuficientes)", Toast.LENGTH_SHORT).show()
                    mostrarDialogoApuesta() // Reintentar
                }
            } else {
                mostrarDialogoApuesta()
            }
        }
        builder.show()
    }

    private fun actualizarTextoDinero() {
        // 4. Actualizar UI leyendo del gestor
        mibinding.tvDineroJugador.text = "Dinero: ${gestorApuestas.dineroJugador}$"
        mibinding.tvDineroCrupier.text = "Banca: ${gestorApuestas.dineroCrupier}$"
    }

    // --- Lógica del flujo del juego (Igual que antes) ---

    private fun iniciarPartida() {
        baraja.reiniciar()
        baraja.barajar()
        manoJugador.limpiar()
        manoCrupier.limpiar()

        mibinding.layoutCartasCrupier.removeAllViews()
        mibinding.layoutCartasJugador.removeAllViews()
        mibinding.btnPedir.isEnabled = true
        mibinding.btnPlantarse.isEnabled = true
        mibinding.btnReiniciar.visibility = View.GONE

        actualizarTextoDinero() // Asegurar que se ve la apuesta actual si quisieras mostrarla

        manoJugador.anadirCarta(baraja.robarCarta()!!)
        manoCrupier.anadirCarta(baraja.robarCarta()!!)
        manoJugador.anadirCarta(baraja.robarCarta()!!)
        manoCrupier.anadirCarta(baraja.robarCarta()!!)

        actualizarUI(mostrarOcultaCrupier = true)

        if (manoJugador.esBlackjack()) {
            Toast.makeText(this, "¡Blackjack!", Toast.LENGTH_SHORT).show()
            turnoCrupier()
        }
    }

    private fun turnoJugadorPedir() {
        val carta = baraja.robarCarta()
        if (carta != null) {
            manoJugador.anadirCarta(carta)
            actualizarUI(mostrarOcultaCrupier = true)

            if (manoJugador.seHaPasado()) {
                // Pasamos quién ganó
                procesarResultadoFinal("Crupier", "¡Te has pasado! Pierdes.")
            }
        }
    }

    private fun turnoCrupier() {
        mibinding.btnPedir.isEnabled = false
        mibinding.btnPlantarse.isEnabled = false

        while (manoCrupier.calcularPuntuacion() < 17) {
            val carta = baraja.robarCarta()
            if (carta != null) manoCrupier.anadirCarta(carta)
        }

        actualizarUI(mostrarOcultaCrupier = false)
        determinarGanador()
    }

    private fun determinarGanador() {
        val ptsJugador = manoJugador.calcularPuntuacion()
        val ptsCrupier = manoCrupier.calcularPuntuacion()

        if (manoJugador.seHaPasado()) {
            procesarResultadoFinal("Crupier", "Perdiste. Te pasaste de 21.")
        } else if (manoCrupier.seHaPasado()) {
            procesarResultadoFinal("Jugador", "¡Ganaste! El crupier se pasó.")
        } else if (ptsJugador > ptsCrupier) {
            procesarResultadoFinal("Jugador", "¡Ganaste! ($ptsJugador vs $ptsCrupier)")
        } else if (ptsJugador < ptsCrupier) {
            procesarResultadoFinal("Crupier", "Perdiste. ($ptsJugador vs $ptsCrupier)")
        } else {
            procesarResultadoFinal("Empate", "Empate. ($ptsJugador iguales)")
        }
    }

    // --- Procesar dinero y mostrar resultado ---
    private fun procesarResultadoFinal(ganador: String, mensajeBase: String) {
        // 5. El gestor calcula el nuevo saldo y nos devuelve el texto del dinero
        val mensajeDinero = gestorApuestas.procesarResultado(ganador)

        val mensajeFinal = mensajeBase + mensajeDinero

        actualizarTextoDinero()
        mostrarDialogoFin(mensajeFinal)
    }

    // --- Métodos auxiliares UI (Sin cambios) ---

    private fun actualizarUI(mostrarOcultaCrupier: Boolean) {
        mibinding.layoutCartasJugador.removeAllViews()
        for (carta in manoJugador.cartas) agregarCartaALayout(carta, mibinding.layoutCartasJugador)
        mibinding.tvPuntosJugador.text = "Puntos: ${manoJugador.calcularPuntuacion()}"

        mibinding.layoutCartasCrupier.removeAllViews()
        if (mostrarOcultaCrupier) {
            if (manoCrupier.cartas.isNotEmpty()) {
                agregarCartaALayout(manoCrupier.cartas[0], mibinding.layoutCartasCrupier)
                val ivReverso = ImageView(this)
                ivReverso.setImageResource(R.drawable.reverso)
                val params = LinearLayout.LayoutParams(200, 300)
                params.setMargins(10, 0, 10, 0)
                ivReverso.layoutParams = params
                mibinding.layoutCartasCrupier.addView(ivReverso)
            }
        } else {
            for (carta in manoCrupier.cartas) agregarCartaALayout(carta, mibinding.layoutCartasCrupier)
        }
    }

    private fun agregarCartaALayout(carta: Carta, layout: LinearLayout) {
        val iv = ImageView(this)
        iv.setImageResource(carta.imageResId)
        val params = LinearLayout.LayoutParams(200, 300)
        params.setMargins(10, 0, 10, 0)
        iv.layoutParams = params
        layout.addView(iv)
    }

    private fun mostrarDialogoFin(mensaje: String) {
        // si jugado no tiene dinero
        if (gestorApuestas.estaEnBancarrota()) {
            mostrarDialogoGameOver("¡Te has quedado sin dinero!")
            return
        }

        // si crupier no tiene dinero
        if (gestorApuestas.haGanadoAlCrupier()) {
            mostrarDialogoVictoria()
            return
        }


        mibinding.btnReiniciar.visibility = View.VISIBLE

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Fin de la mano")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Nueva Apuesta") { _, _ ->
            mostrarDialogoApuesta()
        }
        builder.setNegativeButton("Salir al Menú") { _, _ ->
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }

    // has ganado
    private fun mostrarDialogoVictoria() {
        AlertDialog.Builder(this)
            .setTitle("¡FELICIDADES!") // 标题：恭喜
            .setMessage("¡Has arruinado a la banca!\nTe has llevado todo el dinero: ${gestorApuestas.dineroJugador}$")
            .setIcon(R.drawable.carta_corazones_a) // 可选：显示一张A作为奖杯 (确保你有这张图)
            .setPositiveButton("Volver al Menú") { _, _ ->
                val intent = Intent(this, InicioActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .show()
    }
    private fun mostrarDialogoGameOver(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("GAME OVER")
            .setMessage(mensaje)
            .setPositiveButton("Reiniciar Juego") { _, _ ->
                // 6. Reiniciar lógica en el gestor
                gestorApuestas.reiniciarJuego()
                mostrarDialogoApuesta()
            }
            .setNegativeButton("Salir") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }
}