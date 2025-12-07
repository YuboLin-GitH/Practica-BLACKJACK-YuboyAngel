package com.example.practica_blackjack_yuboyangel

import android.content.Context
import android.media.MediaPlayer

/**
 * clase que está diseñada para tener una única instancia (un solo objeto)
 * en toda la vida de la aplicación y proporcionar un punto de acceso global a ella.
 * Se crea un objeto que "vive" por encima de las actividades, de esta manera ambas activities
 * interactuan con el mismo objeto, garantizando que el estado de mute persista incluso al destruirse
 * la actividad
 */
object MusicManager {

    // El MediaPlayer es nullable y persistirá mientras la app esté abierta.
    private var mediaPlayer: MediaPlayer? = null

    // El estado de muteo es persistente en toda la aplicación.
    var isMuted: Boolean = false
        private set

    private const val VOLUME_ON = 1.0f
    private const val VOLUME_OFF = 0.0f

    private val ICON_ON = R.drawable.sonido
    private val ICON_OFF = R.drawable.nosonido

    /**
     * Inicializa el reproductor si es la primera vez, o lo reanuda si fue pausado.
     */
    fun startMusic(context: Context) {
        if (mediaPlayer == null) {
            // Inicialización completa si no existe
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.musicafondo)
            mediaPlayer?.isLooping = true

            // Aplicar el volumen basado en el estado persistente
            val volume = if (isMuted) VOLUME_OFF else VOLUME_ON
            mediaPlayer?.setVolume(volume, volume)

            mediaPlayer?.start()
        } else if (!mediaPlayer!!.isPlaying && !isMuted) {
            // Si existe pero se pausó (por un evento de sistema), lo reanuda si no está muteado
            mediaPlayer?.start()
        }
    }

    /**
     * Alterna el estado de muteo y devuelve el ID del recurso de la imagen del nuevo estado.
     */
    fun toggleMute(): Int {
        if (isMuted) {
            // Desmutear: Subir el volumen, actualizar estado y devolver icono ON
            mediaPlayer?.setVolume(VOLUME_ON, VOLUME_ON)
            isMuted = false
            return ICON_ON
        } else {
            // Mutear: Poner volumen a cero, actualizar estado y devolver icono OFF
            mediaPlayer?.setVolume(VOLUME_OFF, VOLUME_OFF)
            isMuted = true
            return ICON_OFF
        }
    }

    /**
     * Devuelve el ID del recurso de la imagen actual (útil para inicializar el botón).
     */
    fun getCurrentIcon(): Int {
        return if (isMuted) ICON_OFF else ICON_ON
    }

    /**
     * Pausa la música (opcional, si el sistema lo requiere, pero el estado de muteo se mantiene).
     */
    fun pauseMusic() {
        mediaPlayer?.pause()
    }

    /**
     * Libera el recurso (solo debería llamarse al cerrar completamente la aplicación).
     */
    fun releaseMusic() {
        mediaPlayer?.release()
        mediaPlayer = null
        isMuted = false
    }
}