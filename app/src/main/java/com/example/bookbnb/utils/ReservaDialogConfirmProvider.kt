package com.example.bookbnb.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.example.bookbnb.R
import com.example.bookbnb.databinding.DialogConfirmTemplateReservaBinding
import com.example.bookbnb.databinding.DialogConfirmacionCancelarReservaBinding
import com.example.bookbnb.models.Reserva

class ReservaDialogConfirmProvider {
    companion object{
        fun showDialogConfirm(context: Context,
                              dialogText: String,
                              selectedReserva : Reserva,
                              onPossitiveButtonClick: (Reserva) -> Unit) {
            val builder = AlertDialog.Builder(context)
            val bindingDialog: DialogConfirmTemplateReservaBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_confirm_template_reserva,
                null,
                false
            )
            bindingDialog.viewModel = selectedReserva
            bindingDialog.dialogText.text = dialogText
            val reservaAceptadaDialog = builder
                .setPositiveButton(
                    "Confirmar"
                ) { _: DialogInterface?, _: Int -> onPossitiveButtonClick(selectedReserva) }
                .setNegativeButton(
                    "Cancelar"
                ) { _: DialogInterface?, _: Int -> bindingDialog.viewModel = null }
                .create()
            reservaAceptadaDialog.setView(bindingDialog.root)
            reservaAceptadaDialog.show()
        }
    }
}