package com.example.bookbnb.adapters

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.bookbnb.R
import com.example.bookbnb.models.Reserva
import com.example.bookbnb.ui.reservas.ListaReservasFragment


class ReservasPagerAdapter(
    private val mContext: Context,
    fm: FragmentManager?,
    val publicacionId: String
) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    companion object {
        val TAB_TITLES =
            intArrayOf(R.string.reservas_aceptas_tab_title, R.string.reservas_pendientes_tab_title)
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            ListaReservasFragment(publicacionId, Reserva.ESTADO_ACEPTADA)
        } else ListaReservasFragment(publicacionId, Reserva.ESTADO_PENDIENTE)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mContext.resources
            .getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }

}
