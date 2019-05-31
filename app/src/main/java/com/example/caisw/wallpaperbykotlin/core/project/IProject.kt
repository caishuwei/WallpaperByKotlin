package com.example.caisw.wallpaperbykotlin.core.project

import com.example.caisw.wallpaperbykotlin.core.base.IMotionEventHandler
import com.example.caisw.wallpaperbykotlin.core.base.ISceneLifeCycle

/**
 * 一个Surface显示的场景，不知道叫什么，就叫项目吧
 */
interface IProject : ISceneLifeCycle, IMotionEventHandler {

    /**
     * 显示区域发生变化
     */
    fun onSizeChanged(width: Int, height: Int) {

    }

}