package crupest.cruphysics.utility

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper

/**
 * @brief show a simple alert dialog with a message
 * @param context the context
 * @param message the message to show
 */
fun showAlertDialog(context: Context, message: String): AlertDialog =
        AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()

fun postOnMainThread(action: () -> Unit) {
    Handler(Looper.getMainLooper()).post(action)
}
