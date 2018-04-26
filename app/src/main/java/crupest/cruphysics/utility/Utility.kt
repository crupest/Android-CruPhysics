package crupest.cruphysics.utility

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

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


/**
 * @brief hit test whether a point is inside a square
 * @param x x of the test point
 * @param y y of the test point
 * @param centerX x of the center of the square
 * @param centerY y of the center of the square
 * @param radius the half length of the side of the square, default is <code>40.0f</code>
 * @return true if inside, otherwise false
 */
fun hitTestSquare(x: Float, y: Float,
                  centerX: Float, centerY: Float,
                  radius: Float = 40.0f): Boolean {
    return x > centerX - radius &&
            x < centerX + radius &&
            y > centerY - radius &&
            y < centerY + radius
}

val random = Random()

fun generateRandomColor(): Int = Color.rgb(
        random.nextInt(256),
        random.nextInt(256),
        random.nextInt(256)
)
