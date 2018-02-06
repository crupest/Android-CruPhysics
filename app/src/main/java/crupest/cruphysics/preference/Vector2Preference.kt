package crupest.cruphysics.preference

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.TypedArray
import android.os.Build
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.TextView
import crupest.cruphysics.R
import org.dyn4j.geometry.Vector2
import android.os.Parcel
import android.os.Parcelable
import crupest.cruphysics.physics.serialization.JsonParser
import crupest.cruphysics.physics.serialization.createDefaultKotlinMoshi


/**
 * Created by crupest on 2017/12/22.
 * Preference class [Vector2Preference].
 */

class Vector2Preference(context: Context, attributeSet: AttributeSet) : DialogPreference(context, attributeSet) {

    companion object {
        val DEFAULT_VALUE by lazy {
            val moshi = createDefaultKotlinMoshi()
            val adapter = moshi.adapter(Vector2::class.java)
            adapter.toJson(Vector2())!!
        }
    }

    private val json = JsonParser()
    private val myVector = Vector2()

    private var widgetText: TextView? = null

    init {
        if (Build.VERSION.SDK_INT >= 26)
            isRecycleEnabled = false

        dialogTitle = "Pick a vector:"
        dialogLayoutResource = R.layout.vector_preference_dialog

        widgetLayoutResource = R.layout.vector_preference_widget
    }

    private fun Vector2.toJson(): String = json.moshi.adapter(Vector2::class.java).toJson(this)
    private fun String.toVector2(): Vector2 = json.moshi.adapter(Vector2::class.java).fromJson(this)!!

    @SuppressLint("SetTextI18n")
    private fun updateWidget() {
        widgetText?.text = "(${myVector.x}, ${myVector.y})"
    }

    var vector: Vector2
        get() = myVector
        set(value) {
            if (myVector == value)
                return

            myVector.set(value)
            persistString(myVector.toJson())
            updateWidget()
            notifyChanged()
        }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        super.onClick(dialog, which)
        if (which == DialogInterface.BUTTON_POSITIVE && dialog is Dialog) {

            val newValue = Vector2()
            newValue.x = dialog.findViewById<EditText>(R.id.edit_x).text.toString().toDoubleOrNull() ?: 0.0
            newValue.y = dialog.findViewById<EditText>(R.id.edit_y).text.toString().toDoubleOrNull() ?: 0.0

            if (callChangeListener(newValue)) {
                vector = newValue
            }
        }
    }

    override fun onCreateDialogView(): View {
        val rootView = super.onCreateDialogView()!!

        rootView.findViewById<EditText>(R.id.edit_x).setText(vector.x.toString())
        rootView.findViewById<EditText>(R.id.edit_y).setText(vector.y.toString())

        return rootView
    }

    override fun onBindView(view: View?) {
        super.onBindView(view)

        widgetText = view!!.findViewById(R.id.text)
        updateWidget()
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            myVector.set(getPersistedString(DEFAULT_VALUE).toVector2())
        } else {
            myVector.set(0.0, 0.0)
            persistString(myVector.toString())
        }
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any = a!!.getString(index).toVector2()

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent) {
            // No need to save instance state since it's persistent,
            // use superclass state
            return superState
        }

        // Create instance of custom BaseSavedState
        val myState = SavedState(superState)
        // Set the state's value with the class member that holds current
        // setting value
        myState.vector.set(myVector)
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || state.javaClass != SavedState::class.java) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state)
            return
        }

        // Cast state to custom BaseSavedState and pass to superclass
        val myState = state as SavedState
        super.onRestoreInstanceState(myState.superState)

        // Set this Preference's widget to reflect the restored state
        myVector.set(myState.vector)
        updateWidget()
    }

    private class SavedState : BaseSavedState {
        internal val vector = Vector2()

        constructor(superState: Parcelable) : super(superState)

        constructor(source: Parcel) : super(source) {
            vector.x = source.readDouble()
            vector.y = source.readDouble()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeDouble(vector.x)
            dest.writeDouble(vector.y)
        }

        companion object {
            @Suppress("unused")
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {

                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}
