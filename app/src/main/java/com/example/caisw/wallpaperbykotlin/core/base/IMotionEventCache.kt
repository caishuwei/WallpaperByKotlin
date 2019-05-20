package com.example.caisw.wallpaperbykotlin.core.base

import android.view.MotionEvent

/**
 * Created by caisw on 2018/3/16.
 */
interface IMotionEventHandler {

    fun needHandleMotionEvent(): Boolean

    fun handleMotionEvent(event: MotionEvent)

}