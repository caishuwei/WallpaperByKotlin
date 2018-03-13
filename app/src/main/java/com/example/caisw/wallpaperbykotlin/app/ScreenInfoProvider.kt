package com.example.caisw.wallpaperbykotlin.app

import android.app.Application

/**
 * Created by caisw on 2018/3/12.
 */
class ScreenInfoProvider {

    private lateinit var app: Application

    constructor(app: Application) {
        this.app = app
    }

    fun screenWidth(): Int {
        return app.resources.displayMetrics.widthPixels
    }

    fun screenHeight(): Int {
        return app.resources.displayMetrics.heightPixels
    }

    fun dp2px(value: Float): Int {
        return (app.resources.displayMetrics.density * value + 0.5F).toInt()
    }

    fun px2dp(value: Float): Int {
        return (value / app.resources.displayMetrics.density + 0.5F).toInt()
    }

}
