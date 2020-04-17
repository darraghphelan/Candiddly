package classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.candiddly.R


class ConnectionRecyclerAdapter (
    private val users: List<User>,
    private val itemClick: (User) -> Unit
): RecyclerView.Adapter<ConnectionRecyclerAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_connections, parent, false)
        return Holder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return users.count()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindUser(users[position])
    }

    inner class Holder(itemView: View, val itemClick: (User) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.connectionsUsernameTextView)

        fun bindUser(user: User) {
            userName.text = user.username
            itemView.setOnClickListener { itemClick(user) }
        }
    }
}
