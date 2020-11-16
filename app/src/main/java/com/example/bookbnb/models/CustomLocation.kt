package com.example.bookbnb.models

import android.os.Parcel
import android.os.Parcelable

class Coordenada(var latitud: Double, var longitud: Double) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun toString(): String {
        return "{'latitud': $latitud, 'longitud':$longitud}"
        return "$latitud|$longitud"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitud)
        parcel.writeDouble(longitud)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Coordenada> {
        override fun createFromParcel(parcel: Parcel): Coordenada {
            return Coordenada(parcel)
        }

        override fun newArray(size: Int): Array<Coordenada?> {
            return arrayOfNulls(size)
        }
    }
}

class CustomLocation(var pais: String, var provincia: String, var municipio: String?, var ciudad: String?, var direccion: String?, var coordenadas: Coordenada) {

    override fun toString(): String {
        var location = "";
        if (direccion != null) {
            location += "$direccion, "
        }
        if (municipio != null) {
            location += "$municipio, "
        }
        if (ciudad != null) {
            location += "$ciudad, "
        }
        location += "$provincia, $pais"
        return location
    }
}