package com.example.caisw.wallpaperbykotlin.ui.preview

import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.example.caisw.wallpaperbykotlin.core.SurfaceDrawController
import com.example.caisw.wallpaperbykotlin.entities.MyPointF
import com.example.caisw.wallpaperbykotlin.spirit.Picture
import com.example.caisw.wallpaperbykotlin.spirit.TouchLine

/**
 * 开发时预览用的界面
 * Created by caisw on 2018/3/2.
 */
class PreviewActivity : Activity() {
    private lateinit var previewSurfaceView: PreviewSurfaceView
    private lateinit var surfaceDrawController: SurfaceDrawController
    private lateinit var touchLineArray: Array<TouchLine>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previewSurfaceView = PreviewSurfaceView(this)
        setContentView(previewSurfaceView);
        surfaceDrawController = SurfaceDrawController(previewSurfaceView)
//        surfaceDrawController.spiritHolder.addSpirit(Ring())
//        surfaceDrawController.spiritHolder.addSpirit(Number(Constants.Number_1))
        surfaceDrawController.spiritHolder.addSpirit(Picture())
        touchLineArray = Array(10, { index ->
            val touchLine = TouchLine()
            surfaceDrawController.spiritHolder.addSpirit(touchLine)
            touchLine
        })
        previewSurfaceView.getSurfaceHolder()?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                surfaceDrawController.stopDraw()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                surfaceDrawController.startDraw()
            }

        })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    findTouchLine(event.getPointerId(event.actionIndex))?.clearPoints()
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    findTouchLine(event.getPointerId(event.actionIndex))?.clearPoints()
                }
                MotionEvent.ACTION_MOVE -> {
                    for (i in 0 until event.pointerCount) {
                        if (event.getHistorySize() > 0) {
                            findTouchLine(event.getPointerId(i))?.addTouchPoint(MyPointF(event.getHistoricalX(i, 0), event.getHistoricalY(i, 0)))
                        }
                    }
                }
            }
        }
        return true
    }

    private fun findTouchLine(pointerId: Int): TouchLine? {
        return touchLineArray[pointerId]
//        if (pointerId >= 0 && pointerId < touchLineArray.size) {
//        } else {
//            return null
//        }
    }

    private fun print(s: String, event: MotionEvent) {
        val sb = StringBuffer()
        for (i in 0 until event.pointerCount) {
            sb.append(event.getPointerId(i))
            sb.append(",")
        }
        Log.e(s, sb.toString())
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setTitle("前往设置壁纸")
                .setNegativeButton("确认", { dialog, which ->
                    var intent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                    startActivity(intent)
                })
                .setPositiveButton("退出", { dialog, which ->
                    super.onBackPressed()
                })
                .setOnCancelListener({ dialog ->
                    super.onBackPressed()
                })
                .create().show()
    }

}