package crupest.cruphysics.preference

interface IPreferenceItem : IViewDelegate {
    val labelViewDelegate: IViewDelegate
    val valueViewDelegate: IViewDelegate
}
