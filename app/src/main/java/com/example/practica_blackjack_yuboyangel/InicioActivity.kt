package com.example.practica_blackjack_yuboyangel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class InicioActivity : AppCompatActivity() {

    private lateinit var muteButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        // *** CAMBIO CLAVE 1: Inicializar la música a través del Singleton ***
        MusicManager.startMusic(this)

        val btnJugar = findViewById<Button>(R.id.btnJugar)
        btnJugar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // !!! IMPORTANTE: NO usamos finish() para que esta Activity
            // no se destruya inmediatamente y el MediaPlayer del Singleton no sea liberado.
        }

        // *** CAMBIO CLAVE 2: Conectar el botón al Singleton ***
        muteButton = findViewById<ImageButton>(R.id.btnSonido)

        // Cargar el icono correcto al inicio, según el estado global
        muteButton.setImageResource(MusicManager.getCurrentIcon())

        muteButton.setOnClickListener {
            // Al hacer click, alternar el estado y actualizar la imagen
            val newIcon = MusicManager.toggleMute()
            muteButton.setImageResource(newIcon)
        }
    }

    override fun onPause() {
        super.onPause()
        // No pausamos la música aquí, ya que queremos que siga sonando cuando pasemos a MainActivity.
        // Si quieres pausarla cuando el usuario sale de la app completamente, puedes usar: MusicManager.pauseMusic()
    }

    override fun onResume() {
        super.onResume()
        // Reanudar la música si se pausó por un evento del sistema.
        MusicManager.startMusic(this)
    }

    // Nota: El onDestroy() se omite para evitar que el MusicManager libere la música
    // mientras la aplicación sigue en memoria.
}