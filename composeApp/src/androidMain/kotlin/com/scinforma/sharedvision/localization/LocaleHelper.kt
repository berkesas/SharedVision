// ============================================================
// File: androidMain/kotlin/com/scinforma/sharedvision/localization/LocaleHelper.kt
// ============================================================
package com.scinforma.sharedvision.localization

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, languageCode: String?): Context {
        val locale = when (languageCode) {
            null -> getSystemLocale()
            "en-US" -> Locale.Builder().setLanguage("en").setRegion("US").build()
            "ru-RU" -> Locale.Builder().setLanguage("ru").setRegion("RU").build()
            "tk-TM" -> Locale.Builder().setLanguage("tk").setRegion("TM").build()
            else -> getSystemLocale()
        }

        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    fun getSystemLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            android.content.res.Resources.getSystem().configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            android.content.res.Resources.getSystem().configuration.locale
        }
    }

    fun getCurrentLanguageCode(): String {
        val locale = Locale.getDefault()
        return "${locale.language}-${locale.country}"
    }
}