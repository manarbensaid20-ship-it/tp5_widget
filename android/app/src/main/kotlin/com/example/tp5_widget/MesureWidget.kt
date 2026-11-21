
package com.example.tp5_widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer

import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ROUGE = ColorProvider(
    day = Color(0xFFB3261E),
    night = Color(0xFFB3261E)
)

private val BLANC = ColorProvider(
    day = Color.White,
    night = Color.White
)

class MesureWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()

            val titre = prefs[CleWidget.TITRE] ?: "Capteur"
            val valeur = prefs[CleWidget.VALEUR]
            val unite = prefs[CleWidget.UNITE] ?: "°C"
            val heure = prefs[CleWidget.HEURE]
            val seuil = prefs[CleWidget.SEUIL]

            val alerte = valeur != null &&
                    seuil != null &&
                    valeur >= seuil

            GlanceTheme {
                Contenu(
                    titre,
                    valeur,
                    unite,
                    heure,
                    seuil,
                    alerte
                )
            }
        }
    }
}

@Composable
private fun Contenu(
    titre: String,
    valeur: Float?,
    unite: String,
    heure: Long?,
    seuil: Float?,
    alerte: Boolean
) {
    val fond = if (alerte) ROUGE
               else GlanceTheme.colors.widgetBackground

    val couleur = if (alerte) BLANC
                  else GlanceTheme.colors.onSurface

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(fond)
            .cornerRadius(16.dp)
            .padding(12.dp)
           
    ) {
      Text(
    titre,
    modifier = GlanceModifier.clickable(
        actionStartActivity<MainActivity>()
    ),
    style = TextStyle(
        color = couleur,
        fontSize = 14.sp
    )
)
        Text(
            if (valeur == null) "—"
            else "%.1f %s".format(Locale.FRANCE, valeur, unite),
            style = TextStyle(
                color = couleur,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        )

        if (seuil != null) {
            Text(
                "Seuil : %.1f %s".format(Locale.FRANCE, seuil, unite),
                style = TextStyle(
                    color = couleur,
                    fontSize = 12.sp
                )
            )
        }

      Text(
    when {
        alerte -> "ALERTE"
        heure == null -> "En attente"
        else -> "À ${formatHeure(heure)}"
    },
    style = TextStyle(
        color = couleur,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )
)

        Text(
            "↻ Actualiser",
            style = TextStyle(
                color = couleur,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = GlanceModifier
                .padding(top = 8.dp)
                .clickable(
                    onClick = actionRunCallback<RafraichirAction>()
                )
        )
    }
}

private fun formatHeure(ms: Long): String =
    SimpleDateFormat("HH:mm", Locale.FRANCE).format(Date(ms))