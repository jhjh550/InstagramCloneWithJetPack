package com.fieldbear.instaclonewithjetpack.comment.detail

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.text.bold
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.fieldbear.instaclonewithjetpack.BaseFragment
import com.fieldbear.instaclonewithjetpack.MyApplication
import com.fieldbear.instaclonewithjetpack.R
import com.fieldbear.instaclonewithjetpack.comment.Comment
import com.fieldbear.instaclonewithjetpack.internals.GlideApp
import com.fieldbear.instaclonewithjetpack.internals.Utils
import com.fieldbear.instaclonewithjetpack.main.MainActivity
import com.fieldbear.instaclonewithjetpack.post.Post
import com.fieldbear.instaclonewithjetpack.userdata.UserData
import com.google.common.io.Files.append
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_post_comments.*


class PostCommentsFragment : BaseFragment() {

    private var myData: UserData? = null
    private var post: Post? = null
    private var postId: String? = null

    private lateinit var viewModelFactory: PostCommentsViewModelFactory
    private lateinit var viewModel: PostCommentsViewModel
    private lateinit var adapter: PostCommentsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val safeArgs = PostCommentsFragmentArgs.fromBundle(it)
            post = safeArgs.post
            postId = safeArgs.postId
        }
        (activity?.application as MyApplication).getMyData {
            myData = it
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_comments, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(postId != null && post != null && myData != null) {
            viewModelFactory = PostCommentsViewModelFactory(postId!!, post!!, myData!!)
            viewModel = ViewModelProviders.of(this, viewModelFactory).get(PostCommentsViewModel::class.java)

            initUI()
        }
    }

    private fun initUI(){
        Utils.showProfileImage(post!!.userId, myProfileImage)
        showPostText()
        btnAddComment.setOnClickListener {
            viewModel.saveComment(editTextComment.text.toString())
            editTextComment.setText("")
            Utils.hideKeyboard(btnAddComment)
        }
        FirebaseAuth.getInstance().currentUser?.uid?.let{
            Utils.showProfileImage(it, commentProfileImage)
        }

        adapter = PostCommentsAdapter()
        commentsRecyclerView.layoutManager = LinearLayoutManager(this.context)
        commentsRecyclerView.adapter = adapter
        viewModel.commentList.observe(this, Observer {
            adapter.setData(it)
        })
    }

    private fun showPostText(){
        val s = SpannableStringBuilder()
            .bold { append(myData?.nickName) }
            .append( " "+ post?.text )

        textViewPost.text = s

        textViewPostTimeString.text = Utils.displayTimeString(post!!.timeStamp)
    }


    override fun onDestroy() {
        super.onDestroy()

        (activity as MainActivity).apply{
            bottom_nav.visibility = View.VISIBLE
        }
    }

}
