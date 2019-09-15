package com.fieldbear.instaclonewithjetpack.post.list

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.fieldbear.instaclonewithjetpack.BaseFragment
import com.fieldbear.instaclonewithjetpack.MyApplication
import com.fieldbear.instaclonewithjetpack.R
import com.fieldbear.instaclonewithjetpack.SplashActivity
import com.fieldbear.instaclonewithjetpack.main.MainViewModel
import com.fieldbear.instaclonewithjetpack.userdata.UserData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_main.*

class PostListFragment : BaseFragment(){

    private lateinit var postListAdapter: PostListAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            viewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }

        (activity?.application as MyApplication).getMyData {
            bindUI(it)
        }
        setHasOptionsMenu(true)
    }

    private fun bindUI(myData: UserData?){
        if(myData == null)
            return

        postListAdapter = PostListAdapter(myData)

        postRecyclerView.adapter = postListAdapter
        postRecyclerView.layoutManager = LinearLayoutManager(activity!!)


        btnNewPost.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.postAddFragment)
        }

        viewModel.postDataList.observe(this, Observer {
            postListAdapter.setDataList(it)
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.settings_signout -> {
                FirebaseAuth.getInstance().signOut()

                activity?.let {
                    val intent = Intent(it, SplashActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }



}