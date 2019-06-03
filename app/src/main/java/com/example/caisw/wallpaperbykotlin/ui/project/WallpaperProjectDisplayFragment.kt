package com.example.caisw.wallpaperbykotlin.ui.project

import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import com.example.caisw.wallpaperbykotlin.core.ProjectFactory

class WallpaperProjectDisplayFragment : ProjectDisplayFragment() {
    companion object {

        fun createArguments(): Bundle {
            val data = Bundle()
            data.putString("projectTag", ProjectFactory.TAG_WALLPAPER)
            return data
        }

    }

    override fun initView(viewGroup: ViewGroup, savedInstanceState: Bundle?) {
        super.initView(viewGroup, savedInstanceState)
        val settingWallpaperButton = Button(viewGroup.context)
        settingWallpaperButton.setText("设置壁纸")
        settingWallpaperButton.setOnClickListener {
            var intent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
            startActivity(intent)
        }
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.RIGHT or Gravity.TOP
        viewGroup.addView(settingWallpaperButton, lp)
    }
}