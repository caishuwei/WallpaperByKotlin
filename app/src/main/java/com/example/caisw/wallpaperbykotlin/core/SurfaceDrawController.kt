package com.example.caisw.wallpaperbykotlin.core

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.example.caisw.wallpaperbykotlin.spirit.BaseSpirit
import com.example.caisw.wallpaperbykotlin.spirit.Frame
import java.lang.Exception
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by caisw on 2018/3/12.
 */
class SurfaceDrawController : IDrawController {

    companion object {
        var DEBUG = true
    }

    private var drawing: Boolean = false
    private var drawThread: SurfaceDrawController.DrawThread? = null
    val spiritHolder: SpiritHolder
    private val surfaceHolderProvider: SurfaceHolderProvider
    private val frameDisplay = Frame()

    constructor(surfaceHolderProvider: SurfaceHolderProvider) {
        spiritHolder = SpiritHolder()
        this.surfaceHolderProvider = surfaceHolderProvider
    }

    override fun startDraw() {
        drawing = true
        if (drawThread != null) {
            drawThread?.cancel()
            drawThread = null
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
            val canvas: Canvas? = surfaceHolder.lockCanvas()
            if (canvas != null) {
                onDraw(canvas)
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private fun onDraw(canvas: Canvas) {
        canvas.drawARGB(255, 0, 0, 0)//清屏
        canvas.save()
        val iterator = spiritHolder.spiritList.iterator()
        while (iterator.hasNext()) {
            iterator.next().drawMySelf(canvas)
        }
        if (DEBUG) {
            frameDisplay.drawMySelf(canvas)
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

        private var cancel: Boolean = false
        private var drawSpaceTime = 0L

        override fun run() {
            super.run()
            var beginDrawTime = 0L
            var endDrawTime = 0L
            var sleepTime = 0L
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