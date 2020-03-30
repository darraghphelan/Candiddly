package classes

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_connections.view.*

class ConnectionListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(user: User) {
        itemView.connectionsUsernameTextView.text = user.username
    }

}