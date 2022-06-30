package com.example.testwidget1.model

class Stock(
    private var _ID: String,
    var SYMBOL: String,
    var BIDPRICE: String,
    var PERCENT_CHANGE: String,
    var CHANGE: String,
    var ISUP: String
) {
    fun get_ID(): String {
        return _ID
    }

    fun set_ID(_ID: String) {
        this._ID = _ID
    }
}