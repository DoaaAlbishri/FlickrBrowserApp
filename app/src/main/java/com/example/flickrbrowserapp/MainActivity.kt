package com.example.flickrbrowserapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    //xml
    lateinit var editText: EditText
    lateinit var button: Button
    lateinit var myRv : RecyclerView
    lateinit var imageView: ImageView
    lateinit var llBottom : LinearLayout
    // array of images -- Image class
    var images = ArrayList<com.example.flickrbrowserapp.Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //xml
        editText = findViewById(R.id.editText)
        button = findViewById(R.id.button)
        imageView = findViewById(R.id.imageView)
        llBottom = findViewById(R.id.llBottom)
        myRv = findViewById(R.id.recyclerView)
        // recycler view
        //context , array
        myRv.adapter = RecyclerViewAdapter(this,images)
        myRv.layoutManager = LinearLayoutManager(this)

        button.setOnClickListener {
        // fetch data
            display()
        }
        // close image
        imageView.setOnClickListener { closeImage() }

    }

    private fun display(){
        CoroutineScope(IO).launch {
            //fetch data and update data
            val data = async {
                fetchData()
            }.await()
            if(data.isNotEmpty()){
                imageData(data)
            }else{
                //show error
                Toast.makeText(this@MainActivity, "Not Founds", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //fetch data from API
    private fun fetchData(): String{
        val name = editText.text.toString()
        var response = ""
        try {
            response = URL("https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=9cf6eae618774b19d20c9c0d747c820a&tags=$name&format=json&nojsoncallback=1")
                    .readText(Charsets.UTF_8)
        }catch (e: Exception){
            println("Error: $e")
        }
        return response
    }
    //fill data
    private suspend fun imageData(result: String) {
        withContext(Main) {
            //GET data
            val jsonObj = JSONObject(result)
            val photos = jsonObj.getJSONObject("photos")
            val photo = photos.getJSONArray("photo")
            // loop in photo array
            for(i in 0 until photo.length()){
                val title = photo.getJSONObject(i).getString("title")
                val farmID = photo.getJSONObject(i).getString("farm")
                val serverID = photo.getJSONObject(i).getString("server")
                val id = photo.getJSONObject(i).getString("id")
                val secret = photo.getJSONObject(i).getString("secret")
                //can use this
                //https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
                //https://live.staticflickr.com/${serverID}/${id}_${secret}.jpg
                val photoLink = "https://farm${farmID}.staticflickr.com/${serverID}/${id}_${secret}.jpg"
                // add title of image , link of image
               images.add(Image(title, photoLink))
            }
            myRv.adapter?.notifyDataSetChanged()
        }
    }
    //open image glide
    fun openImage(link: String){
        Glide.with(this)
                .load(link)
                .into(imageView)
        imageView.isVisible = true
        myRv.isVisible = false
        llBottom.isVisible = false
    }
    // close image
    private fun closeImage(){
        imageView.isVisible = false
        myRv.isVisible = true
        llBottom.isVisible = true
    }
}