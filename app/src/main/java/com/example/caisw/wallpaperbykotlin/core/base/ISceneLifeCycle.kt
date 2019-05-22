package com.example.caisw.wallpaperbykotlin.core.base

/**
 * 场景生命周期
 * Created by caisw on 2018/3/16.
 */
interface ISceneLifeCycle {

    /**
     * 场景创建
     */
    fun onCreate()

    /**
     * 场景恢复
     */
    fun onResume()

    /**
     * 场景暂停
     */
    fun onPause()

    /**
     * 场景销毁
     */
    fun onDestroy()

}