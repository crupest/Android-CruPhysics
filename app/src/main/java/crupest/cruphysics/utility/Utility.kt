package crupest.cruphysics.utility

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import java.util.*
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt
import android.os.Looper
import android.util.Log


fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float =
        sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2))

fun distance(p1: PointF, p2: PointF): Float =
        sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))

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

val random = Random()

fun generateRandomColor(): Int = Color.rgb(
        random.nextInt(256),
        random.nextInt(256),
        random.nextInt(256)
)

fun Float.toDegrees(): Float = this * 180.0f / PI.toFloat()

fun nowLong(): Long = Date().time

fun View.removeSelf() {
    (this.parent as ViewGroup).removeView(this)
}

fun postOnMainThread(action: () -> Unit) {
    Handler(Looper.getMainLooper()).post(action)
}
