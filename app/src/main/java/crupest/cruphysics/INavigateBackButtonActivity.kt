package crupest.cruphysics

interface INavigationButtonActivity {
    enum class Button {
        NONE,
        BACK,
        MENU
    }

    fun setNavigationButton(button: Button)
}
