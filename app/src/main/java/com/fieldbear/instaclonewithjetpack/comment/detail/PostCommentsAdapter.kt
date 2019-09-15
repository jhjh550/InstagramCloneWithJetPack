package com.fieldbear.instaclonewithjetpack.comment.detail

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.fieldbear.instaclonewithjetpack.R
import com.fieldbear.instaclonewithjetpack.comment.Comment
import com.fieldbear.instaclonewithjetpack.internals.Utils
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.android.synthetic.main.fragment_post_comments.*

class PostCommentsAdapter : RecyclerView.Adapter<PostCommentsAdapter.CommentViewHolder>(){

    private var comments: ArrayList<QueryDocumentSnapshot> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)

        return CommentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments.get(position).toObject(Comment::class.java)
        holder.bind(comment)
    }

    fun setData(comments: ArrayList<QueryDocumentSnapshot>){
        this.comments = comments
        notifyDataSetChanged()
    }

    class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        val timeStringTextView: TextView = itemView.findViewById(R.id.timeStringTextView)
        val leaveCommentTextView: TextView = itemView.findViewById(R.id.leaveCommentTextView)

        fun bind(comment: Comment){
            Utils.showProfileImage(comment.userId, profileImage)
            showPostText(comment)
        }

        private fun showPostText(comment: Comment){
            val s = SpannableStringBuilder()
                .bold { append(comment.nickName) }
                .append( " "+ comment.text )

            commentTextView.text = s

            timeStringTextView.text = Utils.displayTimeString(comment.timeStamp)
        }

    }
}