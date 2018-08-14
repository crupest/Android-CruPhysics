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
 *  Base class of all [EditText] based preference. The delegate creates a
 *  [EditText] for user to input.
 *
 *  Subclass should overwrite all the abstract methods and methods in
 *  [INotifyValue] to do specific work.
 *
 *  When the text is changed, this class will invoke [onTextChanged] according
 *  to [notifyTrigger] to notify subclass of updating the value with calling
 *  [raiseValueChanged] or throw an error and make a fallback text if the text
 *  is of bad format. The subclass should also implement [INotifyValue.setCurrentValue]
 *  which may call [setCurrentText] with proper transformation.
 *
 */
abstract class TextBasePreferenceValueDelegate<TValue>(
        val inputType: Int,
        val notifyTrigger: NotifyTrigger = NotifyTrigger.ON_LOST_FOCUS
) : IViewDelegate, INotifyValue<TValue> {

    companion object {
        val commonViewCreator : IViewCreator = object : IViewCreator {
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
        view.setText(getInitText())

        when(notifyTrigger) {
            NotifyTrigger.ON_LOST_FOCUS -> view.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    onTextChanged(view.text.toString())
                }
            }
            NotifyTrigger.ON_TEXT_CHANGED -> view.addTextChangedListener(textWatcher)
        }

    }

    final override fun unbindView(view: View) {
        check(view as EditText == editView) // use "as" for smart cast
        when(notifyTrigger) {
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
     *  This function should been called when the value is modified external
     *  so the visual text should be changed or when some error happened so
     *  the visual text should be corrected.
     *  Note that if [notifyTrigger] is set to [NotifyTrigger.ON_TEXT_CHANGED]
     *  the [onTextChanged] won't be called when you change text through this
     *  method.
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
     *  Get the text that should be set when the [EditText] is bound.
     *  TODO: Redesign the interface.
     */
    protected abstract fun getInitText(): String

    /**
     *  This function is call when the trigger condition is satisfied.
     *  For example, if [notifyTrigger] is set to [NotifyTrigger.ON_LOST_FOCUS],
     *  then when [editView] loses focus, this function will be called with current
     *  text in [editView].
     *  Call [raiseValueChanged] in this function if you set the value, or
     *  set the fallback text via [setCurrentText] when some errors happens
     *  like illegal input.
     */
    protected abstract fun onTextChanged(text: String)
}
