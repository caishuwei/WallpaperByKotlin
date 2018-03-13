package com.example.caisw.wallpaperbykotlin.app

import android.app.Application

/**
 * 自定义应用类，用于提供跟随整个应用生命周期的全局变量
 * Created by caisw on 2018/3/2.
 */
class MyApplication : Application() {

    companion object {
        /**应用实例*/
        lateinit var instance: MyApplication

    }

    lateinit var screenInfoProvider: ScreenInfoProvider
        private set

    override fun onCreate() {
        super.onCreate()
        MyApplication.instance = this
        screenInfoProvider = ScreenInfoProvider(this)
    }


}