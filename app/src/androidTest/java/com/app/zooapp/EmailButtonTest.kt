package com.app.zooapp

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.assertion.ViewAssertions.matches
import org.hamcrest.Matchers.not
import android.net.Uri
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Prueba instrumental que verifica el flujo completo:
 * 1. La lista de animales carga correctamente.
 * 2. Se puede navegar al detalle del primer animal.
 * 3. En la pantalla de detalle, el botón de correo lanza un intent ACTION_SENDTO
 *    con el destinatario correcto (info@tuzoologico.com), aunque incluya asunto y cuerpo.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class EmailButtonTest {

    /**
     * Regla que lanza MainActivity antes de cada test y la cierra al finalizar.
     */
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * Inicializa el framework Intents antes del test para poder registrar los intents salientes.
     */
    @Before
    fun setup() {
        Intents.init()
    }

    /**
     * Libera el framework Intents después del test.
     */
    @After
    fun cleanup() {
        Intents.release()
    }

    /**
     * Espera activamente hasta que el detalle del animal se haya cargado.
     * Comprueba que el TextView con id tv_nombre ya no tenga el texto por defecto "Nombre",
     * lo que indica que los datos reales (obtenidos de la API o BD) se han mostrado.
     * Si supera los 5 segundos, lanza una excepción para que el test falle.
     */
    private fun waitForDetailData() {
        val maxTime = 5000L
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < maxTime) {
            try {
                onView(withId(R.id.tv_nombre)).check(matches(not(withText("Nombre")))) // Verifica que el texto haya cambiado
                return
            } catch (e: Exception) {
                // Aún no está listo, esperamos 200 ms y reintentamos
                Thread.sleep(200)
            }
        }
        throw AssertionError("Detalle no cargado después de $maxTime ms")
    }

    /**
     * Test principal:
     * - Espera a que la lista cargue (con una pausa simple)
     * - Hace clic en el primer elemento de la lista
     * - Espera a que el detalle cargue con waitForDetailData()
     * - Hace clic en el botón de enviar correo
     * - Verifica que se haya lanzado un Intent con acción ACTION_SENDTO y
     *   que la URI comience con "mailto:info@tuzoologico.com" (ignorando los parámetros
     *   adicionales como subject y body)
     */
    @Test
    fun testEmailButtonIntent() {
        // Espera a que la lista cargue
        Thread.sleep(3000)

        // Hacer clic en el primer elemento de recyclerview
        onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // Esperar a que el detalle del animal se cargue
        waitForDetailData()

        // Hacer clic en el botón de email
        onView(withId(R.id.btn_send_email)).perform(click())

        // Verificar la acción del intent
        Intents.intended(hasAction(Intent.ACTION_SENDTO))

        // Verificar que la URI comienza con el destinatario esperado.
        // Usamos un matcher personalizado porque la URI real incluye parámetros
        // (subject y body) y no coincide exactamente con la cadena simple.
        Intents.intended(hasData(withUriStartingWith("mailto:info@tuzoologico.com")))
    }

    /**
     * Matcher personalizado para Uri que verifica que su representación como String
     * comienza con el prefijo dado. Útil cuando el intent contiene parámetros adicionales
     * en la URI y solo queremos validar el destinatario.
     * @param prefix Prefijo esperado (ej. "mailto:info@tuzoologico.com")
     * @return Matcher<Uri> que cumple la condición.
     */
    private fun withUriStartingWith(prefix: String): Matcher<Uri> {
        return object : TypeSafeMatcher<Uri>() {
            override fun matchesSafely(uri: Uri): Boolean = uri.toString().startsWith(prefix)
            override fun describeTo(description: Description) {
                description.appendText("URI starting with \"$prefix\"")
            }
        }
    }
}