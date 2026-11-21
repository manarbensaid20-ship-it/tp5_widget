
package com.example.tp5_widget

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.launch

class MainActivity : FlutterActivity() {

    private val CHANNEL = "widget/maj"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->

            when (call.method) {

                "mettreAJour" -> {
                    val valeur = call.argument<Double>("valeur")
                    val titre = call.argument<String>("titre")
                    val unite = call.argument<String>("unite")
                    val seuil = call.argument<Double>("seuil")

                    if (valeur == null) {
                        result.error("PARAMETRE_INVALIDE", "Valeur manquante", null)
                    } else {
                        lifecycleScope.launch {
                            try {
                                val nombre = WidgetMaj.enregistrer(
                                    context = applicationContext,
                                    valeur = valeur,
                                    titre = titre,
                                    unite = unite,
                                    seuil = seuil
                                )
                                result.success(nombre)
                            } catch (e: Exception) {
                                result.error("ERREUR_WIDGET", e.message, null)
                            }
                        }
                    }
                }

                "nombreWidgets" -> {
                    lifecycleScope.launch {
                        try {
                            val nombre = WidgetMaj.nombre(applicationContext)
                            result.success(nombre)
                        } catch (e: Exception) {
                            result.error("ERREUR_WIDGET", e.message, null)
                        }
                    }
                }

                else -> result.notImplemented()
            }
        }
    }
}

