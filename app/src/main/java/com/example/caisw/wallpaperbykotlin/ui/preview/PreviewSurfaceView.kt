package com.example.caisw.wallpaperbykotlin.ui.preview

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.caisw.wallpaperbykotlin.core.SurfaceHolderProvider

/**
 * 预览视图
 * Created by caisw on 2018/3/12.
 */
class PreviewSurfaceView : SurfaceView, SurfaceHolderProvider {

    override fun getSurfaceHolder(): SurfaceHolder? {
        return holder
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

}