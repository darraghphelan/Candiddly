package com.example.candiddly

import android.graphics.drawable.Drawable

data class User(var username: String,
                var email: String,
                var id: String) {
    constructor() : this("",  "", "")
}