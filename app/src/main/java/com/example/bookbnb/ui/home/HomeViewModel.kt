package com.example.bookbnb.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private var _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    var text: MutableLiveData<String> = _text

}