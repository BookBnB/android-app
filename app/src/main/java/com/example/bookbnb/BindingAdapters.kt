package com.example.bookbnb

import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bookbnb.models.CustomLocation
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.ui.publicaciones.PublicacionRecyclerViewAdapter
import com.google.android.material.textfield.TextInputLayout
import android.widget.AutoCompleteTextView as AutoCompleteTextView1


@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}

@BindingAdapter("app:errorText")
fun setErrorMessage(view: TextInputLayout, errorMessage: String) {
    view.error = errorMessage
}

@BindingAdapter("publicacionesListData")
fun bindRecyclerView(recyclerView: RecyclerView,
                     data: List<Publicacion>?) {
    val adapter = recyclerView.adapter as PublicacionRecyclerViewAdapter
    adapter.submitList(data)
}

@BindingAdapter("app:locationsAdapter")
fun setAdapter(actv: AutoCompleteTextView1, adapter: ArrayAdapter<CustomLocation>?) {
    adapter?.let {
        actv.setAdapter(adapter)
        adapter.notifyDataSetChanged();
    }
}
