package crupest.cruphysics.component.adapter

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import crupest.cruphysics.viewmodel.*
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

abstract class ListObserverRecyclerAdapter<TElement, ViewHolder>(
        lifecycleOwner: LifecycleOwner,
        val list: List<TElement>,
        listChangeFlow: Flowable<ListChange>
) : RecyclerView.Adapter<ViewHolder>() where ViewHolder : RecyclerView.ViewHolder {

    private var disposable: Disposable? = null

    init {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun subscribe() {
                disposable = listChangeFlow
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            when (it) {
                                is ListItemAdd -> notifyItemInserted(it.position)
                                is ListItemRemove -> notifyItemRemoved(it.position)
                                is ListItemMove -> notifyItemMoved(it.oldPosition, it.newPosition)
                                is ListItemContentChange -> notifyItemChanged(it.position)
                                is ListRangeAdd -> notifyItemRangeInserted(it.position, it.count)
                            }
                        }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun unsubscribe() {
                disposable?.dispose()
            }

        })
    }

    override fun getItemCount(): Int = list.size

    protected fun getItem(position: Int): TElement = list[position]

    /* precious code
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
                        if (it.oldPosition in newPosition..(oldPosition - 1))
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
    */
}
