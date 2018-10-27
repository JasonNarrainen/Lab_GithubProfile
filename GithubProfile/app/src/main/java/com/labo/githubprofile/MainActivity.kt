package com.labo.githubprofile

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var fetchUserTask: FetchUserTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Calls the view for the search
        setUpSearchView()

        val loginTextView: TextView = findViewById(R.id.tv_user_name)
        val nameTextView: TextView = findViewById(R.id.tv_fullname)

        if (savedInstanceState != null){
            with(savedInstanceState){
                loginTextView.text = getCharSequence(STATE_LOGIN)
                nameTextView.text = getCharSequence(STATE_NAME)

                System.out.println(loginTextView.text)
                System.out.println(nameTextView.text)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {

        val loginTextView: TextView = findViewById(R.id.tv_user_name)
        val nameTextView: TextView = findViewById(R.id.tv_fullname)

        val loginText = loginTextView.text
        val nameText = nameTextView.text

        outState?.run {

            putCharSequence(STATE_LOGIN, loginText)
            putCharSequence(STATE_NAME, nameText)
        }

        super.onSaveInstanceState(outState)
    }

    companion object {
        val STATE_LOGIN = "loginText"
        val STATE_NAME = "nameText"
    }

    /***************************************************
     * Method that assigns a listener on the search bar
     */
    private fun setUpSearchView() {

        search_view_user.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {

            // Defines what happens when a text is submitted in the search bar
            override fun onQueryTextSubmit(query: String?): Boolean {

                // Calls the function that searches for the user
                searchUser(query)

                return true
            }

            // Defines what happens when the text in the search bar is changed
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        search_view_user.isSubmitButtonEnabled = true
    }

    /****************************************************************
     * Method that displays the progress bar of the ongoing search
     * @param userName: String?
     */
    private fun searchUser(userName: String?) {
        layout_profile.visibility = View.GONE
        progress_bar_profile.visibility = View.VISIBLE

        if (fetchUserTask != null)
            fetchUserTask!!.cancel(true)

        fetchUserTask = FetchUserTask(this)
        fetchUserTask!!.execute(userName)
    }

    /****************************************************
     * Method that displays an error message in a toast.
     */
    fun displayErrorMessage() {
        progress_bar_profile.visibility = View.GONE
        Toast.makeText(this, this.getString(R.string.error_message), Toast.LENGTH_SHORT).show()
    }

    /*****************************************************
     * Method that displays the user information
     * @param user: String, loading: Boolean
     */
    fun displayUser(user: User, loading: Boolean) {
        layout_profile.visibility = View.VISIBLE
        tv_user_name.text = user.login
        tv_fullname.text = user.name

        Glide.with(this).load(user.avatarUrl).into(iv_avatar)

        if (!loading)
            progress_bar_profile.visibility = View.GONE
    }

}
