package crupest.cruphysics.component.adapter

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

abstract class ListLiveDataRecyclerAdapter<TElement, ViewHolder>(
        lifecycleOwner: LifecycleOwner,
        listLiveData: LiveData<List<TElement>>,
        private val diffTool: DiffTool<TElement>
) : RecyclerView.Adapter<ViewHolder>() where ViewHolder : RecyclerView.ViewHolder {

    interface DiffTool<TElement> {
        fun areItemSame(oldOne: TElement, newOne: TElement): Boolean
        fun areContentSame(oldOne: TElement, newOne: TElement): Boolean
    }

    private var currentList: List<TElement>

    init {
        val original = listLiveData.value
        if (original == null)
            currentList = listOf()
        else {
            currentList = original
            compareAndNotify(listOf(), original)
        }

        listLiveData.observe(lifecycleOwner, Observer {
            val oldList = currentList
            currentList = it
            compareAndNotify(oldList, it)
        })
    }

    override fun getItemCount(): Int = currentList.size

    protected fun getItem(position: Int): TElement = currentList[position]

    private data class Move(var oldPosition: Int, var newPosition: Int)

    private fun compareAndNotify(oldList: List<TElement>, newList: List<TElement>) {

        val removeList = mutableListOf<Int>()
        val moveMap: MutableList<Move> = mutableListOf()

        oldList.forEachIndexed old@{ oldIndex, oldOne ->
            newList.forEachIndexed new@{ newIndex, newOne ->
                if (diffTool.areItemSame(newOne, oldOne)) {
                    moveMap.add(Move(oldIndex, newIndex))
                    return@old
                }
            }
            removeList.add(oldIndex)
        }

        val addList = newList.indices.filter {
            moveMap.all { pair ->
                pair.newPosition != it
            }
        }

        moveMap.forEachIndexed { index, (oldPosition, newPosition) ->
            if (oldPosition != newPosition) {
                notifyItemMoved(oldPosition, newPosition)

                fun forEachRemain(action: (Move) -> Unit) {
                    for (i in index until moveMap.size)
                        action(moveMap[i])
                }

                if (oldPosition < newPosition)
                    forEachRemain {
                        if (it.oldPosition in (oldPosition + 1)..newPosition)
                            it.oldPosition--
                    }
                else
                    forEachRemain {
                        if (it.oldPosition in newPosition..(oldPosition-1))
                            it.oldPosition++
                    }
            }
            if (!diffTool.areContentSame(oldList[oldPosition], newList[newPosition]))
                notifyItemChanged(newPosition)
        }

        var removedCount = 0
        removeList.forEach {
            notifyItemRemoved(it - removedCount)
            removedCount++
        }

        addList.forEach {
            notifyItemInserted(it - removedCount)
            removedCount--
        }
    }
}
