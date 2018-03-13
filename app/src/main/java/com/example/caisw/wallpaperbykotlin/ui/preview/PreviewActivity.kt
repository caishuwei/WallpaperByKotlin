package com.example.caisw.wallpaperbykotlin.ui.preview

import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import android.view.SurfaceHolder
import com.example.caisw.wallpaperbykotlin.core.SurfaceDrawController
import com.example.caisw.wallpaperbykotlin.module.Constants
import com.example.caisw.wallpaperbykotlin.spirit.Number
import com.example.caisw.wallpaperbykotlin.spirit.Ring

/**
 * 开发时预览用的界面
 * Created by caisw on 2018/3/2.
 */
class PreviewActivity : Activity() {
    private lateinit var previewSurfaceView: PreviewSurfaceView
    private lateinit var surfaceDrawController: SurfaceDrawController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previewSurfaceView = PreviewSurfaceView(this)
        setContentView(previewSurfaceView);
        surfaceDrawController = SurfaceDrawController(previewSurfaceView)
        surfaceDrawController.spiritHolder.addSpirit(Ring())
        surfaceDrawController.spiritHolder.addSpirit(Number(Constants.Number_1))
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
                .create().show()
    }

}