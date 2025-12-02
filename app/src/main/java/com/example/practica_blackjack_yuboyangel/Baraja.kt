package com.example.practica_blackjack_yuboyangel

import android.content.Context

class Baraja(private val context: Context) {
    private val cartas = mutableListOf<Carta>()

    init {
        reiniciar()
    }

    fun reiniciar() {
        cartas.clear()

        for (p in Palo.values()) {
            for (r in Rango.values()) {
                val carta = Carta(p, r)

                // 1. 获取花色名称 (西班牙语, 小写)
                // 例如: TREBOLES -> "treboles"
                val nombrePalo = p.name.lowercase()

                // 2. 获取数字/字母后缀 (映射逻辑)
                // 这里把枚举变成了你的文件名格式: "02", "10", "k", "a"
                val sufijoNombre = when (r) {
                    Rango.DOS -> "02"    // 如果你的图片是 "2" 而不是 "02"，这里就删掉 0
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

                // 3. 拼接最终文件名
                // 结果类似: "carta_treboles_02", "carta_picas_k"
                val nombreArchivo = "carta_${nombrePalo}_${sufijoNombre}"

                // 4. 查找资源 ID
                val resId = context.resources.getIdentifier(
                    nombreArchivo,
                    "drawable",
                    context.packageName
                )

                if (resId != 0) {
                    carta.imageResId = resId
                }

                cartas.add(carta)
            }
        }
    }

    fun barajar() {
        cartas.shuffle()
    }

    fun robarCarta(): Carta? {
        if (cartas.isEmpty()) return null
        return cartas.removeAt(0)
    }
}