package com.fieldbear.instaclonewithjetpack.post.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.fieldbear.instaclonewithjetpack.MyProgress
import com.fieldbear.instaclonewithjetpack.R
import com.fieldbear.instaclonewithjetpack.comment.Comment
import com.fieldbear.instaclonewithjetpack.internals.GlideApp
import com.fieldbear.instaclonewithjetpack.internals.Utils
import com.fieldbear.instaclonewithjetpack.main.MainViewModel
import com.fieldbear.instaclonewithjetpack.post.Post
import com.fieldbear.instaclonewithjetpack.userdata.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage

class PostListAdapter(val myData: UserData) : RecyclerView.Adapter<PostListAdapter.PostListViewHolder>(){

    private var postList = arrayListOf<QueryDocumentSnapshot>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_post, parent, false)

        return PostListViewHolder(view, myData)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostListViewHolder, position: Int) {
        val post = postList.get(position)
        holder.bind(post)
    }


    fun setDataList(list: ArrayList<QueryDocumentSnapshot>){
        postList = list
        notifyDataSetChanged()
    }

//    fun addDataList(list: Array<Post>){
//        val beforeSize = postList.size
//        postList.addAll(list)
//        notifyItemRangeChanged(beforeSize-1, postList.size-1)
//    }

    class PostListViewHolder(itemView:View, myData: UserData): RecyclerView.ViewHolder(itemView){
        private lateinit var viewModel: MainViewModel
        val postImageView: ImageView = itemView.findViewById(R.id.postImageView)
        val postTextView: TextView = itemView.findViewById(R.id.postTextView)
        val btnAddComment: TextView = itemView.findViewById(R.id.btnAddComment)
        val editTextComment: EditText = itemView.findViewById(R.id.editTextComment)
        val textViewTimeString: TextView = itemView.findViewById(R.id.textViewTimeString);

        lateinit var postDocument: QueryDocumentSnapshot
        lateinit var post: Post
        var myData:UserData = myData

        init {
            btnAddComment.setOnClickListener {
                addComment()
            }
            itemView.setOnClickListener{
                val action =
                    PostListFragmentDirections.actionAddComment(postDocument.id, post)
                Navigation.findNavController(it).navigate(action)
            }
        }

        fun bind(doc: QueryDocumentSnapshot){
            postDocument = doc
            post = doc.toObject(Post::class.java)

            val ref =  FirebaseStorage.getInstance().reference.child(post.imagePath)
            GlideApp.with(postImageView).load(ref).into(postImageView)

            postTextView.text = post.text
            textViewTimeString.text = Utils.displayTimeString(post.timeStamp)
        }


        private fun addComment(){
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            uid?.let {
                val progress = MyProgress(postTextView.context)
                progress.show()

                val text = editTextComment.text.toString()
                val comment = Comment(
                    postDocument.id, "", uid,
                    text, myData.nickName, System.currentTimeMillis()
                )

                FirebaseFirestore.getInstance().collection("comments").add(comment)
                    .addOnCompleteListener {
                        Utils.hideKeyboard(editTextComment)
                        editTextComment.setText("")
                        progress.dismiss()
                        Toast.makeText(
                            postTextView.context,
                            "Comment add " + it.isSuccessful,
                            Toast.LENGTH_LONG
                        ).show()
//                    if(it.isSuccessful){ }

                    }
            }
        }
    }
}
