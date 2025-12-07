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


        MusicManager.startMusic(this)

        val btnJugar = findViewById<Button>(R.id.btnJugar)
        btnJugar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

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
    }

    override fun onResume() {
        super.onResume()
        MusicManager.startMusic(this)
    }

}