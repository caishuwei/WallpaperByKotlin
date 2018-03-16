package com.example.caisw.wallpaperbykotlin.core.base

import android.view.SurfaceHolder

/**
 * Created by caisw on 2018/3/12.
 */
interface SurfaceHolderProvider {

    /**
     * 返回SurfaceHolder
     */
    fun getSurfaceHolder(): SurfaceHolder?

}