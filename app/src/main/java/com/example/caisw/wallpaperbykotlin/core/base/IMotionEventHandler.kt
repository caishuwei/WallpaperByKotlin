package com.example.caisw.wallpaperbykotlin.core.base

import android.view.MotionEvent

/**
 * 触摸事件处理者
 * Created by caisw on 2018/3/16.
 */
interface IMotionEventHandler {

    /**
     * 判断是否需要处理触摸事件
     */
    fun needHandleMotionEvent(): Boolean

    /**
     * 触摸事件处理
     */
    fun handleMotionEvent(event: MotionEvent)

}