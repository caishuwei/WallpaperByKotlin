package com.example.caisw.wallpaperbykotlin.ui.preview

import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.caisw.wallpaperbykotlin.core.SceneController
import com.example.caisw.wallpaperbykotlin.core.spirit.group.Scene
import com.example.caisw.wallpaperbykotlin.core.surface.impl.SurfaceView
import com.example.caisw.wallpaperbykotlin.utils.MyGestureDetector

/**
 * 开发时预览用的界面
 * Created by caisw on 2018/3/2.
 */
class SearchTestActivity : Activity() {
    private lateinit var surfaceView: SurfaceView
    private lateinit var wallpaperController: SceneController
//    private var myGestureDetector = object : MyGestureDetector() {
//
//        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
//            scene.translate(-distanceX, -distanceY)
//            return super.onScroll(e1, e2, distanceX, distanceY)
//        }
//
//        override fun onScale(detector: ScaleGestureDetector?): Boolean {
//            detector?.let {
//                scene.scale(it.scaleFactor,it.scaleFactor,it.focusX,it.focusY)
//            }
//            return super.onScale(detector)
//        }
//
//    }
//    private val scene = Scene();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surfaceView = SurfaceView(this)
        setContentView(surfaceView);
        wallpaperController = SceneController(surfaceView)
        wallpaperController.onCreate()
    }

    override fun onResume() {
        super.onResume()
        wallpaperController.onResume()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            wallpaperController.handleMotionEvent(event)
        }
        return true
    }


    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setTitle("前往设置壁纸")
                .setNegativeButton("确认") { _, _ ->
                    var intent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                    startActivity(intent)
                }
                .setPositiveButton("退出") { _, _ ->
                    super.onBackPressed()
                }
                .setOnCancelListener { _ ->
                    super.onBackPressed()
                }
                .create().show()
    }

    override fun onPause() {
        super.onPause()
        wallpaperController.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        wallpaperController.onDestroy()
    }


}