package com.labo.githubprofile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.lang.System.out

class MainActivity : AppCompatActivity() {

    var fetchUserTask: FetchUserTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Calls the view for the search
        setUpSearchView()

        // Initialize the views from the xml file
        val usernameTextView: TextView = findViewById(R.id.tv_user_name)
        val fullNameTextView: TextView = findViewById(R.id.tv_fullname)
        val avatarImageView: ImageView = findViewById((R.id.iv_avatar))

        // If the bundle was created previously
        if (savedInstanceState != null){
            with(savedInstanceState){

                // Get the text from the username and full name from the bundle using the keys
                // and set the texts in the views.
                usernameTextView.text = getCharSequence(STATE_USERNAME)
                fullNameTextView.text = getCharSequence(STATE_FULLNAME)

                val avatarBitmap = BitmapFactory.decodeByteArray(getByteArray(STATE_AVATAR), 0, getByteArray(
                    STATE_AVATAR)!!.size)

                // Get the bitmap from the bundle using the key and set the bitmap for the ImageView
                avatarImageView.setImageBitmap(avatarBitmap)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {

        // Initialize the views from the xml file
        val usernameTextView: TextView = findViewById(R.id.tv_user_name)
        val fullNameTextView: TextView = findViewById(R.id.tv_fullname)
        val avatarImageView: ImageView = findViewById((R.id.iv_avatar))

        // Get the text from the views
        val usernameText = usernameTextView.text
        val fullNameText = fullNameTextView.text

        if (avatarImageView.drawable != null){

            // Get the drawable from ImageView and cast it to BitmapDrawable to have access to the bitmap method
            val avatarBitmap = (avatarImageView.drawable!! as BitmapDrawable).bitmap

            var byteStream = ByteArrayOutputStream()
            avatarBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream)

            // Use the run method to put information in outstate to use with the onCreate method.
            outState?.run {

                // Put the text using key/value method
                putCharSequence(STATE_USERNAME, usernameText)
                putCharSequence(STATE_FULLNAME, fullNameText)

                // Get the bitmap from the BitmapDrawable to be compatible with the putParcelable method
                // and using key/value method.
                putByteArray(STATE_AVATAR, byteStream.toByteArray())
            }
        }

        // Call the super to get the default saved information
        super.onSaveInstanceState(outState)
    }

    // Declare object with the keys to restore the information
    companion object {
        val STATE_USERNAME = "usernameText"
        val STATE_FULLNAME = "fullNameText"
        val STATE_AVATAR = "image"
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
