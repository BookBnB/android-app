package com.example.bookbnb

import android.content.res.ColorStateList
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bookbnb.adapters.*
import com.example.bookbnb.models.*
import com.example.bookbnb.models.chat.FirebaseChatMessage
import com.example.bookbnb.adapters.PublicacionRecyclerViewAdapter
import com.example.bookbnb.utils.SessionManager
import com.example.bookbnb.viewmodels.CalificacionVM
import com.example.bookbnb.viewmodels.FirebaseChatVM
import com.example.bookbnb.viewmodels.ReservaVM
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

@BindingAdapter("recomendacionesListData")
fun bindRecomendacionesRecyclerView(recyclerView: RecyclerView, data: List<Publicacion>?){
    val adapter = recyclerView.adapter as RecomendacionesRecyclerViewAdapter
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
                            data: List<ReservaVM>?) {
    val adapter = recyclerView.adapter as HuespedReservasRecyclerViewAdapter
    adapter.submitList(data)
    adapter.notifyDataSetChanged()
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

@BindingAdapter("calificacionesListData")
fun bindCalificacionesRecyclerView(recyclerView: RecyclerView,
                              data: List<CalificacionVM>?) {
    val adapter = recyclerView.adapter as CalificacionesRecyclerViewAdapter
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
        EstadoReserva.PENDIENTE.estado -> ContextCompat.getColor(image.context, R.color.reservaPendiente)
        EstadoReserva.ACEPTADA.estado -> ContextCompat.getColor(image.context, R.color.reservaAceptada)
        EstadoReserva.RECHAZADA.estado -> ContextCompat.getColor(image.context, R.color.error)
        EstadoReserva.CANCELADA.estado -> ContextCompat.getColor(image.context, R.color.error)
        else -> ContextCompat.getColor(image.context, R.color.primaryDarkColor)
    }
    ImageViewCompat.setImageTintList(image, ColorStateList.valueOf(color))
}

@BindingAdapter("chatTitleColor")
fun setChatTitleColor(view: TextView, senderId: String){
    if (SessionManager(view.context).getUserId() == senderId) {
        view.setTextColor(ContextCompat.getColor(view.context, R.color.primaryColor))
    }
    else {
        view.setTextColor(ContextCompat.getColor(view.context, R.color.reservaAceptada))
    }
}

@BindingAdapter("messageGravity")
fun setMessageGravity(view: LinearLayout, senderId: String){
    if (SessionManager(view.context).getUserId() == senderId) {
        view.gravity = Gravity.START
    }
    else {
        view.gravity = Gravity.END
    }
}

@BindingAdapter("chatMessageDrawable")
fun setChatDrawable(view: ConstraintLayout, senderId: String){
    if (SessionManager(view.context).getUserId() == senderId) {
        view.setBackgroundResource(R.drawable.sender_msg_rounded_shape)
        //view.background = ContextCompat.getDrawable(view.context, R.drawable.sender_msg_rounded_shape)
    } else{
        view.setBackgroundResource(R.drawable.receiver_msg_rounded_shape)
        //view.background = ContextCompat.getDrawable(view.context, R.drawable.receiver_msg_rounded_shape)
    }
}