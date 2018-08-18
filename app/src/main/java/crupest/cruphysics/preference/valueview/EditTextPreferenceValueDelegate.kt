package crupest.cruphysics.preference.valueview

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import crupest.cruphysics.preference.IViewCreator
import crupest.cruphysics.preference.IViewDelegate


/**
 * Base class of all [EditText] based preference. The delegate creates a
 * [EditText] for user to input.
 *
 * When the text is changed, [onTextChanged] will be called according
 * to [notifyTrigger] to notify the listener. If [onTextChanged] returns
 * false, then the text is marked error. When it loses focus, [onRestoreText]
 * will be called.
 *
 * Each time the view is rebound, [onRestoreText] will be called to restore the
 * saved state.
 *
 * @param onRestoreText callback that restores text from saved value.
 * @param onTextChanged This function is call when the trigger condition is satisfied.
 */
class EditTextPreferenceValueDelegate(
        val inputType: Int,
        val onRestoreText: () -> String,
        val onTextChanged: (String) -> Boolean,
        val notifyTrigger: NotifyTrigger = NotifyTrigger.ON_LOST_FOCUS,
        var setStyleListener: ((EditText) -> Unit)? = null
) : IViewDelegate {

    companion object {
        val commonViewCreator: IViewCreator = object : IViewCreator {
            override fun createView(context: Context): View = EditText(context)
        }
    }

    enum class NotifyTrigger {
        ON_LOST_FOCUS,
        ON_TEXT_CHANGED
    }

    private var editView: EditText? = null
    private var error: Boolean = false

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            error = !onTextChanged(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    override val viewCreator: IViewCreator
        get() = commonViewCreator


    override fun bindView(view: View) {
        //bind it
        editView = view as EditText

        //set style
        view.inputType = inputType
        setEditTextStyle(view)
        setStyleListener?.invoke(view)

        //set init text
        view.setText(onRestoreText())

        view.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // if lose focus
                if (notifyTrigger == NotifyTrigger.ON_LOST_FOCUS)
                    error = !onTextChanged(view.text.toString())

                if (error)
                    view.setText(onRestoreText())
            }
        }

        if (notifyTrigger == NotifyTrigger.ON_TEXT_CHANGED) {
            view.addTextChangedListener(textWatcher)
        }
    }

    override fun unbindView(view: View) {
        check(view as EditText == editView) // use "as" for smart cast
        when (notifyTrigger) {
            NotifyTrigger.ON_LOST_FOCUS -> view.onFocusChangeListener = null
            NotifyTrigger.ON_TEXT_CHANGED -> view.removeTextChangedListener(textWatcher)
        }
    }

    private fun setEditTextStyle(editText: EditText) {
        editText.setTextColor(Color.BLACK)
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
    }

    /**
     * This function should been called when the value is modified external
     * so the visual text should be changed.
     *
     * Note that if [notifyTrigger] is set to [NotifyTrigger.ON_TEXT_CHANGED]
     * the [onTextChanged] won't be called when you change text through this
     * method.
     */
    fun setCurrentText(text: String) {
        editView?.apply {
            if (notifyTrigger == NotifyTrigger.ON_TEXT_CHANGED)
                removeTextChangedListener(textWatcher)
            setText(text)
            if (notifyTrigger == NotifyTrigger.ON_TEXT_CHANGED)
                addTextChangedListener(textWatcher)
        }
    }
}
