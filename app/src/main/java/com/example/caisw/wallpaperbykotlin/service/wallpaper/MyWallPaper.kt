package com.example.caisw.wallpaperbykotlin.service.wallpaper

import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.example.caisw.wallpaperbykotlin.core.SurfaceDrawController
import com.example.caisw.wallpaperbykotlin.core.SurfaceHolderProvider
import com.example.caisw.wallpaperbykotlin.spirit.Ring

/**
 * Created by caisw on 2018/3/2.
 */
class MyWallPaper : WallpaperService() {
    private lateinit var surfaceDrawController: SurfaceDrawController
    private lateinit var myEngine: MyEngine

    override fun onCreate() {
        super.onCreate()
        myEngine = MyEngine()
        surfaceDrawController = SurfaceDrawController(myEngine)
        surfaceDrawController.spiritHolder.addSpirit(Ring())
    }

    /**
     * 创建引擎
     */
    override fun onCreateEngine(): Engine {
        return myEngine
    }

    /**
     * 创建内部类：渲染引擎
     */
    inner class MyEngine : Engine(), SurfaceHolderProvider {


        /**
         * 引擎创建后调用的方法，接收一个SurfaceHolder<br/>
         * 在这里做一些初始化操作
         */
        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(true);//接收触摸事件
        }

        /**
         * 动态壁纸是否可见的回调方法，用于控制界面刷新等，在不可见时节省cpu资源
         */
        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                surfaceDrawController.startDraw()
            } else {
                surfaceDrawController.stopDraw()
            }

        }

        /**
         * surface创建时回调，此时可以用于刷新
         */
        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            surfaceDrawController.startDraw()
        }

        /**
         * surface大小改变时回调
         */
        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
        }

        /**
         * surface销毁时回调
         */
        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            surfaceDrawController.stopDraw()
        }

        /**
         * 触摸事件处理
         */
        override fun onTouchEvent(event: MotionEvent?) {
            super.onTouchEvent(event)
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

        }

    }

}
