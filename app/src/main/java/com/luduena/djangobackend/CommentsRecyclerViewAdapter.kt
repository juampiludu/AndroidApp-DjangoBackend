package com.luduena.djangobackend

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luduena.djangobackend.api.ApiService
import com.luduena.djangobackend.api.Comment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CommentsRecyclerViewAdapter(
    context: Context,
    commentList: ArrayList<Comment>
) : RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder>() {

    private val mCommentList: ArrayList<Comment> = commentList
    private val mContext: Context = context
    private val preferences: SharedPreferences = mContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.comments_recycler, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem: Comment = mCommentList[position]
        val username = preferences.getString("username", "").toString()

        holder.commentAuthor.text = currentItem.author
        holder.commentContent.text = currentItem.content
        holder.commentDate.text = currentItem.published_date

        if (username == currentItem.author) {

            holder.itemView.setOnClickListener { v: View ->

                Toast.makeText(v.context, "Keep pressed to delete comment", Toast.LENGTH_SHORT).show()

            }

            holder.itemView.setOnLongClickListener {v: View ->

                MaterialAlertDialogBuilder(v.context, R.style.AlertDialogTheme)
                    .setTitle("Delete comment?")
                    .setMessage("This comment will be deleted permanently.")
                    .setPositiveButton("Delete") {_,_->

                        holder.deleteComment(currentItem.id)

                    }
                    .setNegativeButton("Cancel") {_,_->

                    }
                    .show()

                return@setOnLongClickListener true

            }

        }

    }

    override fun getItemCount(): Int {
        return mCommentList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var commentAuthor: TextView = itemView.findViewById(R.id.comment_author)
        var commentContent: TextView = itemView.findViewById(R.id.comment_content)
        var commentDate: TextView = itemView.findViewById(R.id.comment_published_date)

        fun deleteComment(id: Int) {

            val retrofit = Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val authToken = SharedDataGetSet.getMySavedToken(itemView.context)

            val service = retrofit.create(ApiService::class.java)
            val call = service.deleteComment(authToken, id)

            call.enqueue(object: Callback<List<Comment>> {
                override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                    Toast.makeText(
                        itemView.context,
                        "Comment deleted successfully!",
                        Toast.LENGTH_SHORT)
                        .show()

                    commentAuthor.text = ""
                    commentContent.text = "Comment deleted"
                    commentDate.text = ""

                }

                override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                    Toast.makeText(itemView.context, "Error deleting comment. Try again later.", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

}