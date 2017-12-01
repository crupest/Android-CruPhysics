package crupest.cruphysics.utility

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.PointF
import android.view.View

/**
 * Created by crupest on 2017/11/2.
 * Utilities.
 */

val View.activity: Activity?
    get() {
        var context = this.context
        while (context is ContextWrapper) {
            if (context is Activity)
                return context
            context = context.baseContext
        }
        return null
    }

fun distance(p1: PointF, p2: PointF): Float {
    return Math.sqrt(
            Math.pow((p1.x - p2.x).toDouble(), 2.0) + Math.pow((p1.y - p2.y).toDouble(), 2.0)
    ).toFloat()
}

/**
 * @brief create a simple alert dialog with a message (don't show)
 * @param context the context
 * @param message the message to show
 */
fun createAlertDialog(context: Context, message: String) : AlertDialog {
    val builder = AlertDialog.Builder(context)
    builder.setMessage(message)
    builder.setPositiveButton("Ok") {
        dialog, _ -> dialog.dismiss()
    }
    return builder.create()
}


/**
 * @brief show a simple alert dialog with a message
 * @param context the context
 * @param message the message to show
 */
fun showAlertDialog(context: Context, message: String) : AlertDialog {
    val builder = AlertDialog.Builder(context)
    builder.setMessage(message)
    builder.setPositiveButton("Ok") {
        dialog, _ -> dialog.dismiss()
    }
    return builder.show()
}


/**
 * @brief hit test whether a point is inside a square
 * @param x x of the test point
 * @param y y of the test point
 * @param centerX x of the center of the square
 * @param centerY y of the center of the square
 * @param radius the half length of the side of the square, default is <code>40.0f</code>
 * @return true if inside, otherwise false
 */
fun hitTestSquare(x: Float, y: Float, centerX: Float, centerY: Float, radius: Float = 40.0f) : Boolean {
    return x > centerX - radius &&
            x < centerX + radius &&
            y > centerY - radius &&
            y < centerY + radius
}
