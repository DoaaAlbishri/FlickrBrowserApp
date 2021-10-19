package com.example.flickrbrowserapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_row.view.*

class RecyclerViewAdapter(val mainActivity: MainActivity , private val images: ArrayList<Image>) : RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>(){
    class ItemViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.item_row, parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val image=images[position]

        holder.itemView.apply{
            tvImage.text= image.title
            Glide.with(this)
                .load(image.link)
                .into(ivImage)
            llItem.setOnClickListener {
                mainActivity.openImage(image.link)
            }
        }

    }

    override fun getItemCount(): Int =images.size

}