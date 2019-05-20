package com.example.caisw.wallpaperbykotlin.core

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.example.caisw.wallpaperbykotlin.core.base.IDrawController
import com.example.caisw.wallpaperbykotlin.core.base.SurfaceHolderProvider
import com.example.caisw.wallpaperbykotlin.spirit.BaseSpirit
import com.example.caisw.wallpaperbykotlin.spirit.Frame
import java.lang.Exception
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by caisw on 2018/3/12.
 */
class SurfaceDrawController : IDrawController {

    private var drawing: Boolean = false
    private var drawThread: DrawThread? = null
    val spiritHolder: SpiritHolder
    private val surfaceHolderProvider: SurfaceHolderProvider
    private val frameDisplay = Frame()

    constructor(surfaceHolderProvider: SurfaceHolderProvider) {
        spiritHolder = SpiritHolder()
        this.surfaceHolderProvider = surfaceHolderProvider
    }

    override fun startDraw() {
        drawing = true
        val dt = drawThread
        if (dt != null) {
            if (!dt.cancel) {
                return
            } else {
                drawThread = null
            }
        }
        drawThread = DrawThread()
        drawThread?.start()
    }

    override fun stopDraw() {
        drawing = false
        if (drawThread != null) {
            drawThread?.cancel()
            drawThread = null
        }
    }

    private fun doDraw() {
        val surfaceHolder = surfaceHolderProvider.getSurfaceHolder()
        if (surfaceHolder != null) {
            val canvas: Canvas?
            if (dirtyRect.isEmpty) {
                canvas = surfaceHolder.lockCanvas()
            } else {
                Log.e("Frame", dirtyRect.toString())
                canvas = surfaceHolder.lockCanvas(dirtyRect)
            }
            if (canvas != null) {
                onDraw(canvas)
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private val dirtyRect = Rect()

    private fun onDraw(canvas: Canvas) {
        canvas.drawARGB(255, 0, 0, 0)//清屏
        canvas.save()
//        dirtyRect.set(0, 0, 0, 0)
        val iterator = spiritHolder.spiritList.iterator()
        var spirit: BaseSpirit
        while (iterator.hasNext()) {
            spirit = iterator.next()
            if (spirit.isRelease) {
                spiritHolder.spiritList.remove(spirit)
            } else {
                spirit.drawMySelf(canvas)
                if (WallpaperController.DEBUG) {
                    spirit.drawBounds(canvas)
                }
//                dirtyRect.union(
//                        (spirit.boundsRect.left - 0.5F).toInt(),
//                        (spirit.boundsRect.top - 0.5F).toInt(),
//                        (spirit.boundsRect.right + 0.5F).toInt(),
//                        (spirit.boundsRect.bottom + 0.5F).toInt()
//                )
            }
        }
        if (WallpaperController.DEBUG) {
            frameDisplay.drawMySelf(canvas)
            frameDisplay.drawBounds(canvas)
//            dirtyRect.union(
//                    (frameDisplay.boundsRect.left ).toInt(),
//                    (frameDisplay.boundsRect.top - 0.5F).toInt(),
//                    (frameDisplay.boundsRect.right + 0.5F).toInt(),
//                    (frameDisplay.boundsRect.bottom + 0.5F).toInt()
//            )
        }
        canvas.restore()
    }

    inner class SpiritHolder {
        val spiritList: CopyOnWriteArrayList<BaseSpirit>
        private val mainHandle: Handler

        constructor() {
            spiritList = CopyOnWriteArrayList()
            mainHandle = Handler(Looper.getMainLooper())
        }

        fun addSpirit(spirit: BaseSpirit) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mainHandle.post(Runnable {
                    addSpirit(spirit)
                })
                return
            }
            spiritList.add(spirit)
        }

        fun removeSpirit(spirit: BaseSpirit) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mainHandle.post(Runnable {
                    removeSpirit(spirit)
                })
                return
            }
            spiritList.remove(spirit)
        }

    }

    private inner class DrawThread : Thread {

        /**
         * 帧速上限（默认每秒最高60帧）
         */
        private var frameUpperLimit = 60
        /**
         * 帧速上限平稳刷帧时，每帧最多花费的时间
         */
        private var timeForOneFrame: Float = 0F;

        constructor() : super("绘制线程") {
            frameUpperLimit = 60
            timeForOneFrame = 1000F / frameUpperLimit
        }

        var cancel: Boolean = false
            private set
        private var drawSpaceTime = 0L

        override fun run() {
            super.run()
            var beginDrawTime : Long
            var endDrawTime : Long
            var sleepTime : Long
            try {
                while (!cancel) {
                    beginDrawTime = SystemClock.elapsedRealtime();
                    doDraw()
                    frameDisplay.onFrameDrawCompleted()
                    endDrawTime = SystemClock.elapsedRealtime();
                    drawSpaceTime = endDrawTime - beginDrawTime
                    sleepTime = (timeForOneFrame - drawSpaceTime + 0.5).toLong()
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                cancel()
                if (drawing) {
                    //若出异常时，程序还处于绘制状态，那么开启新线程继续绘制
                    startDraw()
                }
            }
        }

        fun cancel() {
            cancel = true
        }
    }
}