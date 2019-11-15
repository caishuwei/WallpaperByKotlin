package com.example.caisw.wallpaperbykotlin.service.wallpaper

import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.example.caisw.wallpaperbykotlin.app.MyApplication
import com.example.caisw.wallpaperbykotlin.core.ProjectFactory
import com.example.caisw.wallpaperbykotlin.core.surface.SurfaceHolderProvider

/**
 * Created by caisw on 2018/3/2.
 */
class MyWallPaper : WallpaperService() {

    /**
     * 创建引擎
     */
    override fun onCreateEngine(): Engine {
        return MyEngine()
    }

    /**
     * 创建内部类：渲染引擎
     */
    inner class MyEngine : Engine(), SurfaceHolderProvider {

        private var project = ProjectFactory.getProject(MyApplication.instance.spDataCache.common.getWallPaperType(), this)

        /**
         * 引擎创建后调用的方法，接收一个SurfaceHolder<br/>
         * 在这里做一些初始化操作
         */
        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            project?.let {
                setTouchEventsEnabled(it.needHandleMotionEvent())//接收触摸事件
            }
            project?.onCreate()
        }

        /**
         * 动态壁纸是否可见的回调方法，用于控制界面刷新等，在不可见时节省cpu资源
         */
        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                project?.onResume()
            } else {
                project?.onPause()
            }
        }

        /**
         * surface创建时回调，此时可以用于刷新
         */
        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            project?.onResume()
        }

        /**
         * surface大小改变时回调
         */
        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            project?.onSizeChanged(width, height)
        }

        /**
         * surface销毁时回调
         */
        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            project?.onPause()
        }

        /**
         * 触摸事件处理
         */
        override fun onTouchEvent(event: MotionEvent?) {
            super.onTouchEvent(event)
            if (event != null) {
                project?.handleMotionEvent(event)
            }
        }

        /**
         * 当用户启动器有多个页面时，在不同页面切换会回调这里，可以根据切换进度响应滚动画面
         */
        override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset)
        }

        /**
         * 引擎销毁，回收资源
         */
        override fun onDestroy() {
            super.onDestroy()
            project?.onDestroy()
        }

    }

}
