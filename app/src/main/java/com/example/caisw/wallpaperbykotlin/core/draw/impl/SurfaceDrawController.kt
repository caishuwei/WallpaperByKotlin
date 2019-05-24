package com.example.caisw.wallpaperbykotlin.core.draw.impl

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.example.caisw.wallpaperbykotlin.core.draw.IDrawController
import com.example.caisw.wallpaperbykotlin.core.spirit.Spirit
import com.example.caisw.wallpaperbykotlin.core.surface.SurfaceHolderProvider
import com.example.caisw.wallpaperbykotlin.utils.Utils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by caisw on 2018/3/12.
 */
class SurfaceDrawController(private val surfaceHolderProvider: SurfaceHolderProvider) : IDrawController {

    val spiritHolder: SpiritHolder

    private var drawDisposable: Disposable? = null

    init {
        spiritHolder = SpiritHolder()
    }

    override fun startDraw() {
        //采用RxJava实现绘制线程
        drawDisposable?.dispose()
        drawDisposable = Observable.create(ObservableOnSubscribe<Canvas> {
            while (!it.isDisposed) {
                var beginDrawTime: Long
                var endDrawTime: Long
                var sleepTime: Long
                var drawSpaceTime = 0L
                val timeForOneFrame = 1000F / 60
                val surfaceHolder = surfaceHolderProvider.getSurfaceHolder()
                var canvas: Canvas? = null
                try {
                    beginDrawTime = SystemClock.elapsedRealtime();
                    if (surfaceHolder != null) {
                        if (dirtyRect.isEmpty) {
                            canvas = surfaceHolder.lockCanvas()
                        } else {
                            Log.e("Frame", dirtyRect.toString())
                            canvas = surfaceHolder.lockCanvas(dirtyRect)
                        }
                        if (canvas != null) {
                            it.onNext(canvas)
                        }
                    }
                    endDrawTime = SystemClock.elapsedRealtime();
                    drawSpaceTime = endDrawTime - beginDrawTime
                    sleepTime = (timeForOneFrame - drawSpaceTime + 0.5).toLong()
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime)
                    }
                } catch (e: Exception) {
                    if (!it.isDisposed) {
                        it.onError(e)
                    }
                } finally {
                    Utils.runIfNotNull(surfaceHolder, canvas) { s, c -> s.unlockCanvasAndPost(c) }
                }
            }
        })
                .subscribeOn(Schedulers.io())//切换到子线程执行
                .subscribe(
                        {
                            //执行绘制
                            onDraw(it)
                        },
                        {
                            //绘制过程出现异常
                            startDraw()
                        }
                )
    }

    override fun stopDraw() {
        drawDisposable?.dispose()
        drawDisposable = null
    }

    private val dirtyRect = Rect()

    private fun onDraw(canvas: Canvas) {
        canvas.drawARGB(255, 0, 0, 0)//清屏
        canvas.save()
//        dirtyRect.set(0, 0, 0, 0)
        val iterator = spiritHolder.spiritList.iterator()
        var spirit: Spirit
        while (iterator.hasNext()) {
            spirit = iterator.next()
            spirit.draw(canvas)
//                spirit.drawBounds(canvas)
//                dirtyRect.union(
//                        (spirit.boundsRect.left - 0.5F).toInt(),
//                        (spirit.boundsRect.top - 0.5F).toInt(),
//                        (spirit.boundsRect.right + 0.5F).toInt(),
//                        (spirit.boundsRect.bottom + 0.5F).toInt()
//                )
        }
        canvas.restore()
    }

    inner class SpiritHolder {
        val spiritList: CopyOnWriteArrayList<Spirit>
        private val mainHandle: Handler

        constructor() {
            spiritList = CopyOnWriteArrayList()
            mainHandle = Handler(Looper.getMainLooper())
        }

        fun addSpirit(spirit: Spirit) {
            spiritList.add(spirit)
        }

        fun removeSpirit(spirit: Spirit) {
            spiritList.remove(spirit)
        }

    }


}