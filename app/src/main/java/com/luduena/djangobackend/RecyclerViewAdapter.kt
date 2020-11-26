package com.luduena.djangobackend

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luduena.djangobackend.api.Post


class RecyclerViewAdapter(
    postList: ArrayList<Post>
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val mPostList: ArrayList<Post> = postList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem: Post = mPostList[position]

        holder.postTitle.text = currentItem.title
        holder.postInfo.text = "${currentItem.author} • ${currentItem.published_date}"

        holder.itemView.setOnClickListener {v: View ->

            val intent = Intent(v.context, PostDetail::class.java)
                .putExtra("postId", currentItem.id)
                .putExtra("authorName", currentItem.author)
            v.context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return mPostList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var postTitle: TextView = itemView.findViewById(R.id.item_title)
        var postInfo: TextView = itemView.findViewById(R.id.postInfoText)

    }

}