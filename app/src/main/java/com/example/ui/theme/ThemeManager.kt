package com.example.ui.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ThemeManager {
    private const val PREFS_NAME = "app_theme_prefs"
    private const val KEY_IS_DARK = "key_is_dark"
    private const val KEY_BG_NAME = "custom_background.jpg"

    var isDarkMode by mutableStateOf(true)
        private set

    var customBackgroundBitmap by mutableStateOf<ImageBitmap?>(null)
        private set

    var hasCustomBackground by mutableStateOf(false)
        private set

    fun init(context: Context) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isDarkMode = sp.getBoolean(KEY_IS_DARK, true)
        loadCustomBackground(context)
    }

    fun toggleDarkMode(context: Context) {
        isDarkMode = !isDarkMode
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_DARK, isDarkMode)
            .apply()
    }

    fun loadCustomBackground(context: Context) {
        val file = File(context.filesDir, KEY_BG_NAME)
        if (file.exists() && file.length() > 0) {
            try {
                // Decode with reasonable bounds to prevent OOM
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeFile(file.absolutePath, options)
                
                var inSampleSize = 1
                val maxDim = 1920
                if (options.outHeight > maxDim || options.outWidth > maxDim) {
                    val halfHeight = options.outHeight / 2
                    val halfWidth = options.outWidth / 2
                    while (halfHeight / inSampleSize >= maxDim && halfWidth / inSampleSize >= maxDim) {
                        inSampleSize *= 2
                    }
                }
                
                val decodeOptions = BitmapFactory.Options().apply {
                    this.inSampleSize = inSampleSize
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                val bitmap = BitmapFactory.decodeFile(file.absolutePath, decodeOptions)
                if (bitmap != null) {
                    customBackgroundBitmap = bitmap.asImageBitmap()
                    hasCustomBackground = true
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        customBackgroundBitmap = null
        hasCustomBackground = false
    }

    suspend fun saveCustomBackground(context: Context, uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext false
                val targetFile = File(context.filesDir, KEY_BG_NAME)
                FileOutputStream(targetFile).use { output ->
                    inputStream.copyTo(output)
                }
                inputStream.close()
                withContext(Dispatchers.Main) {
                    loadCustomBackground(context)
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    fun resetToDefaultBackground(context: Context) {
        val file = File(context.filesDir, KEY_BG_NAME)
        if (file.exists()) {
            file.delete()
        }
        customBackgroundBitmap = null
        hasCustomBackground = false
    }
}
