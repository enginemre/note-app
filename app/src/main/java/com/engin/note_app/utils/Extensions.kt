package com.engin.note_app.utils

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.ColorInt
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 *
 * Set icon for snack bar
 *
 */
fun Snackbar.setIcon(drawable: Drawable, @ColorInt colorTint: Int): Snackbar {
    return this.apply {
        setAction(" ") {}
        val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        textView.text = ""
        drawable.setTint(colorTint)
        drawable.setTintMode(PorterDuff.Mode.SRC_ATOP)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }
}


/**
 *
 * Formatting date to string pattern of  dd/MM/yyyy
 *
 */
fun Date.toFormattedDate() : String{
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
   return format.format(this)
}