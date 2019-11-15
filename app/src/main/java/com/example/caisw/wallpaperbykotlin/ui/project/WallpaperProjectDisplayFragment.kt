package com.example.caisw.wallpaperbykotlin.ui.project

import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import com.example.caisw.wallpaperbykotlin.app.Common
import com.example.caisw.wallpaperbykotlin.app.MyApplication
import com.example.caisw.wallpaperbykotlin.core.ProjectFactory

open class WallpaperProjectDisplayFragment : ProjectDisplayFragment() {
    companion object {

        fun createArguments(): Bundle {
            val data = Bundle()
            data.putString("projectTag", ProjectFactory.TAG_PICTURE_WALLPAPER)
            return data
        }

    }

    var settingWallpaperButton: Button? = null
        private set

    override fun initView(viewGroup: ViewGroup, savedInstanceState: Bundle?) {
        super.initView(viewGroup, savedInstanceState)
        settingWallpaperButton = Button(viewGroup.context)
        settingWallpaperButton?.setText("设置壁纸")
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.RIGHT or Gravity.TOP
        viewGroup.addView(settingWallpaperButton, lp)
    }

    override fun initListener() {
        super.initListener()
        settingWallpaperButton?.setOnClickListener {
            MyApplication.instance.spDataCache.common.setWallPaperType(ProjectFactory.TAG_PICTURE_WALLPAPER)
            var intent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        settingWallpaperButton = null
        super.onDestroyView()
    }
}