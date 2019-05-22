package com.example.caisw.wallpaperbykotlin.core.surface

import android.view.SurfaceHolder

/**
 * 绘制面提供者
 * Created by caisw on 2018/3/12.
 */
interface SurfaceHolderProvider {

    /**
     * 返回SurfaceHolder
     */
    fun getSurfaceHolder(): SurfaceHolder?

}