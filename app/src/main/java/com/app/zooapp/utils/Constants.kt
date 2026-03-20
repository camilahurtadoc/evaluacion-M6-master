package com.app.zooapp.utils

/**
 * Constantes globales de la aplicación.
 * Aquí se definen URLs, direcciones de correo y plantillas.
 */
object Constants {
    const val BASE_URL = "https://zoo-api.vercel.app/"
    // Datos para el envío de correo
    const val EMAIL_RECIPIENT = "info@tuzoologico.com"
    const val EMAIL_SUBJECT = "Información sobre: %s"
    const val EMAIL_BODY = "Solicito más información sobre el o los " +
            "Zoológicos dentro de Chile que tienen un/a %s. Me gustaría realizar una " +
            "reserva para visitarlo junto a mi familia."
}