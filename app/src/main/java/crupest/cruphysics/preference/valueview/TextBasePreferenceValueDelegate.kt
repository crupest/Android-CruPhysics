package crupest.cruphysics.preference.valueview

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import crupest.cruphysics.preference.INotifyValue
import crupest.cruphysics.preference.IViewCreator
import crupest.cruphysics.preference.IViewDelegate


/**
 * Base class of all [EditText] based preference. The delegate creates a
 * [EditText] for user to input.
 *
 * Subclass should overwrite all the abstract methods and methods in
 * [INotifyValue] to do specific work.
 *
 * When the text is changed, [onTextChanged] will be called according
 * to [notifyTrigger] to notify subclass of updating the value with calling
 * [raiseValueChanged].
 *
 * If the input is of bad format, subclass could do nothing. When [EditText]
 * loses focus, a call of [onRestoreText] will be called to amend the possible
 * wrong input by restoring to saved state.
 *
 * Each time the view is rebound, [onRestoreText] will be called to restore the
 * saved state.
 *
 * The subclass should also implement [INotifyValue.setCurrentValue]
 * which may call [setCurrentText] with proper transformation.
 */
abstract class TextBasePreferenceValueDelegate<TValue>(
        val context: Context,
        val inputType: Int,
        val notifyTrigger: NotifyTrigger = NotifyTrigger.ON_LOST_FOCUS
) : IViewDelegate, INotifyValue<TValue> {

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
    private var valueChangedListener: ((TValue) -> Unit)? = null

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            onTextChanged(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    override val viewCreator: IViewCreator
        get() = commonViewCreator

    final override fun bindView(view: View) {
        //bind it
        editView = view as EditText

        view.inputType = inputType
        setEditTextStyle(view)
        view.setText(onRestoreText())

        view.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // if lose focus
                if (notifyTrigger == NotifyTrigger.ON_LOST_FOCUS)
                    onTextChanged(view.text.toString())

                view.setText(onRestoreText())
            }
        }

        if (notifyTrigger == NotifyTrigger.ON_TEXT_CHANGED) {
            view.addTextChangedListener(textWatcher)
        }
    }

    final override fun unbindView(view: View) {
        check(view as EditText == editView) // use "as" for smart cast
        when (notifyTrigger) {
            NotifyTrigger.ON_LOST_FOCUS -> view.onFocusChangeListener = null
            NotifyTrigger.ON_TEXT_CHANGED -> view.removeTextChangedListener(textWatcher)
        }
    }

    protected open fun setEditTextStyle(editText: EditText) {
        editText.setTextColor(Color.BLACK)
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
    }

    override fun setValueChangedListener(listener: ((TValue) -> Unit)?) {
        valueChangedListener = listener
    }

    protected fun raiseValueChanged(value: TValue) {
        valueChangedListener?.invoke(value)
    }


    /**
     * This function should been called when the value is modified external
     * so the visual text should be changed.
     *
     * Note that if [notifyTrigger] is set to [NotifyTrigger.ON_TEXT_CHANGED]
     * the [onTextChanged] won't be called when you change text through this
     * method.
     */
    protected fun setCurrentText(text: String) {
        editView?.apply {
            if (notifyTrigger == NotifyTrigger.ON_TEXT_CHANGED)
                removeTextChangedListener(textWatcher)
            setText(text)
            if (notifyTrigger == NotifyTrigger.ON_TEXT_CHANGED)
                addTextChangedListener(textWatcher)
        }
    }

    /**
     * Restore text from saved value.
     *
     * This is called when the [EditText] is bound first (set the default text) or again.
     *
     * This is also called when the [EditText] loses focus to amend the possible bad-format
     * text. Note that when [notifyTrigger] is set to [NotifyTrigger.ON_LOST_FOCUS],
     * [onTextChanged] is called before [onRestoreText].
     */
    protected abstract fun onRestoreText(): String

    /**
     * This function is call when the trigger condition is satisfied.
     *
     * For example, if [notifyTrigger] is set to [NotifyTrigger.ON_LOST_FOCUS],
     * then when [editView] loses focus, this function will be called with current
     * text in [editView].
     *
     * Call [raiseValueChanged] in this function if you set the value.
     */
    protected abstract fun onTextChanged(text: String)
}
