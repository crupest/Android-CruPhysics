package crupest.cruphysics.preference.valueview

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText


/**
 *  Base class of all [EditText] based preference. Subclass should overwrite all the abstract
 *  methods to do specific work. The delegate first creates a [EditText] and saves it. And when the
 *  text is changed, it will invoke [onUpdateValue] according to [notifyTrigger] to notify subclass
 *  of updating the value based on the text or throw an error and make a fallback text. And when the
 *  value is set externally, subclass should invoke [setCurrentText] to reflect this change.
 */
abstract class TextBasePreferenceValueDelegate<TValue>(
        val inputType: Int,
        val notifyTrigger: NotifyTrigger = NotifyTrigger.ON_LOST_FOCUS
) : IPreferenceValueDelegate<TValue> {

    enum class NotifyTrigger {
        ON_LOST_FOCUS,
        ON_TEXT_CHANGED
    }

    private lateinit var view: EditText
    private var valueChangedListener: ((TValue) -> Unit)? = null
    private var errorHandler: ((String) -> Unit)? = null

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            onUpdateValue(view.text.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    final override fun setValueChangedListener(listener: ((TValue) -> Unit)?) {
        valueChangedListener = listener
    }

    protected fun raiseValueChanged(value: TValue) {
        valueChangedListener?.invoke(value)
    }

    fun setErrorHandler(handler: ((String) -> Unit)?) {
        errorHandler = handler
    }

    protected fun raiseError(message: String) {
        errorHandler?.invoke(message)
    }

    final override fun createValueView(context: Context): View {
        view = EditText(context)
        view.setText(getInitText())
        setEditTextStyle(view)
        when(notifyTrigger) {
            NotifyTrigger.ON_LOST_FOCUS -> view.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    onUpdateValue(view.text.toString())
                }
            }
            NotifyTrigger.ON_TEXT_CHANGED -> view.addTextChangedListener(textWatcher)
        }
        return view
    }

    protected open fun setEditTextStyle(editText: EditText) {
        editText.setTextColor(Color.BLACK)
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
    }

    /**
     *  This function should been called when the value is modified external
     *  so the visual text should be changed or when some error happened so
     *  the visual text should be corrected.
     *  Note that if [notifyTrigger] is set to [NotifyTrigger.ON_TEXT_CHANGED]
     *  the [onUpdateValue] won't be called when you change text through this
     *  method.
     */
    protected fun setCurrentText(text: String) {
        if (notifyTrigger == NotifyTrigger.ON_TEXT_CHANGED)
            view.removeTextChangedListener(textWatcher)
        view.setText(text)
        if (notifyTrigger == NotifyTrigger.ON_TEXT_CHANGED)
            view.addTextChangedListener(textWatcher)
    }

    /**
     *  Get the text that should be set when the [EditText] is created.
     */
    protected abstract fun getInitText(): String

    /**
     *  This function is call when the trigger condition is satisfied.
     *  For example, is [notifyTrigger] is set to [NotifyTrigger.ON_LOST_FOCUS],
     *  then when [view] loses focus, this function will be called with current
     *  text in [view].
     *  Call [raiseValueChanged] in this function if you set the value, or call
     *  [raiseError] and set the fallback text via [setCurrentText] when
     *  some errors happens like illegal input.
     */
    protected abstract fun onUpdateValue(text: String)
}
