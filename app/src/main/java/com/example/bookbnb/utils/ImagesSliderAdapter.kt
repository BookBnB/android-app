package com.example.bookbnb.utils

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.bookbnb.R
import com.smarteist.autoimageslider.SliderViewAdapter

class CustomImageUri(var uri: Uri?){
    fun getImageUri(): Uri?{
        return uri
    }
}

class ImagesSliderAdapter(private val context: Context, private val loadFromFirebase: Boolean = false) : SliderViewAdapter<ImagesSliderAdapter.SliderAdapterVH>() {

    private var mSliderItems: MutableList<CustomImageUri> = ArrayList()
    fun renewItems(sliderItems: MutableList<CustomImageUri>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: CustomImageUri) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(
        viewHolder: SliderAdapterVH,
        position: Int
    ) {
        val sliderItem: CustomImageUri = mSliderItems[position]
        //viewHolder.textViewDescription.text = "Test"
        viewHolder.textViewDescription.textSize = 16f
        viewHolder.textViewDescription.setTextColor(Color.WHITE)
        if (loadFromFirebase) {
            Glide.with(viewHolder.itemView)
            .load(sliderItem.uri)
            .fitCenter()
            .into(viewHolder.imageViewBackground)
        }
        else{
            viewHolder.imageViewBackground.setImageURI(sliderItem.getImageUri())
        }
        viewHolder.itemView.setOnClickListener {
            Toast.makeText(context, "This is item in position $position", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return mSliderItems.size
    }

    inner class SliderAdapterVH(anItemView: View) : ViewHolder(anItemView) {
        var imageViewBackground: ImageView = itemView.findViewById(R.id.iv_auto_image_slider)
        var imageGifContainer: ImageView = itemView.findViewById(R.id.iv_gif_container)
        var textViewDescription: TextView = itemView.findViewById(R.id.tv_auto_image_slider)

    }

}