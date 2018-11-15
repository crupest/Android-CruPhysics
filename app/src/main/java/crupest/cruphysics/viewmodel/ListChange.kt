package crupest.cruphysics.viewmodel

sealed class ListChange
class ListItemAdd(val position: Int) : ListChange()
class ListItemRemove(val position: Int) : ListChange()
class ListItemMove(val oldPosition: Int, val newPosition: Int) : ListChange()
class ListItemContentChange(val position: Int) : ListChange()
class ListRangeAdd(val position: Int, val count: Int) : ListChange()
