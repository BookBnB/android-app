package com.example.bookbnb

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bookbnb.adapters.ChatMessagesRecyclerViewAdapter
import com.example.bookbnb.adapters.ChatsRecyclerViewAdapter
import com.example.bookbnb.adapters.PreguntasRecyclerViewAdapter
import com.example.bookbnb.models.CustomLocation
import com.example.bookbnb.models.Pregunta
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.models.Reserva
import com.example.bookbnb.models.Reserva.Companion.ESTADO_ACEPTADA
import com.example.bookbnb.models.Reserva.Companion.ESTADO_PENDIENTE
import com.example.bookbnb.models.Reserva.Companion.ESTADO_RECHAZADA
import com.example.bookbnb.models.chat.FirebaseChat
import com.example.bookbnb.models.chat.FirebaseChatMessage
import com.example.bookbnb.ui.publicaciones.PublicacionRecyclerViewAdapter
import com.example.bookbnb.ui.reservas.HuespedReservasRecyclerViewAdapter
import com.example.bookbnb.ui.reservas.ReservaRecyclerViewAdapter
import com.example.bookbnb.viewmodels.FirebaseChatVM
import com.google.android.material.slider.RangeSlider
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*
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

@BindingAdapter("reservasListData")
fun bindReservaRecyclerView(recyclerView: RecyclerView,
                     data: List<Reserva>?) {
    val adapter = recyclerView.adapter as ReservaRecyclerViewAdapter
    adapter.submitList(data)
}

@BindingAdapter("huespedReservasListData")
fun bindHuespedReservaRecyclerView(recyclerView: RecyclerView,
                            data: List<Reserva>?) {
    val adapter = recyclerView.adapter as HuespedReservasRecyclerViewAdapter
    adapter.submitList(data)
}

@BindingAdapter("messagesListData")
fun bindMessagesRecyclerView(recyclerView: RecyclerView,
                              data: List<FirebaseChatMessage>?) {
    val adapter = recyclerView.adapter as ChatMessagesRecyclerViewAdapter
    adapter.submitList(data)
    recyclerView.adapter!!.notifyDataSetChanged()
    recyclerView.scrollToPosition(data!!.size - 1)
}

@BindingAdapter("chatsListData")
fun bindChatsRecyclerView(recyclerView: RecyclerView,
                             data: List<FirebaseChatVM>?) {
    val adapter = recyclerView.adapter as ChatsRecyclerViewAdapter
    adapter.submitList(data)
}

@BindingAdapter("preguntasListData")
fun bindPreguntasRecyclerView(recyclerView: RecyclerView,
                     data: List<Pregunta>?) {
    val adapter = recyclerView.adapter as PreguntasRecyclerViewAdapter
    adapter.submitList(data)
}

@BindingAdapter("app:locationsAdapter")
fun setAdapter(actv: AutoCompleteTextView1, adapter: ArrayAdapter<CustomLocation>?) {
    adapter?.let {
        actv.setAdapter(adapter)
        adapter.notifyDataSetChanged();
    }
}

@BindingAdapter("dateShortString")
fun bindShortDate(txtView: TextView, date: Date?){
    date?.let {
        txtView.text = SimpleDateFormat("dd/MM/yyyy").format(date)
    }
}

@BindingAdapter("reservaDrawableTint")
fun setReservaDrawableTint(image: ImageView, estado: String){
    val color = when (estado){
        ESTADO_PENDIENTE -> ContextCompat.getColor(image.context, R.color.reservaPendiente)
        ESTADO_ACEPTADA -> ContextCompat.getColor(image.context, R.color.reservaAceptada)
        ESTADO_RECHAZADA -> ContextCompat.getColor(image.context, R.color.error)
        else -> ContextCompat.getColor(image.context, R.color.primaryDarkColor)
    }
    ImageViewCompat.setImageTintList(image, ColorStateList.valueOf(color))
}