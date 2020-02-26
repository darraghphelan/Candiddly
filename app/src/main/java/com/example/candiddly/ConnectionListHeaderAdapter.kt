package com.example.candiddly

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ConnectionListHeaderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listOfFriends = listOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CellType.HEADER.ordinal -> TopViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_recycler_header, parent, false))
            CellType.CONTENT.ordinal -> ConnectionListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_connections, parent, false))
            CellType.FOOTER.ordinal -> BottomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_recycler_header, parent, false))
            else -> ConnectionListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_connections, parent, false))
        }
    }

    override fun getItemCount(): Int = listOfFriends.size + 2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            CellType.HEADER.ordinal -> {
                val headerViewHolder = holder as TopViewHolder
                headerViewHolder.bindView()
            }
            CellType.CONTENT.ordinal -> {
                val headerViewHolder = holder as ConnectionListViewHolder
                headerViewHolder.bindView(listOfFriends[position - 1])
            }
            CellType.FOOTER.ordinal -> {
                val footerViewHolder = holder as BottomViewHolder
                footerViewHolder.bindView()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> CellType.HEADER.ordinal
            listOfFriends.size + 1 -> CellType.FOOTER.ordinal
            else -> CellType.CONTENT.ordinal
        }
    }

    enum class CellType {
        HEADER,
        FOOTER,
        CONTENT
    }

    fun setFriendList(listOfFriends: List<User>) {
        this.listOfFriends = listOfFriends
        notifyDataSetChanged()
    }
}