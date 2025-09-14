package com.example.eccomerce_app.data.Room.Model

import androidx.room.PrimaryKey

abstract  class IsPassCondition {
    @PrimaryKey()
    var id: Int? = 0;
    var condition: Boolean=false;
}
