package com.example.caisw.wallpaperbykotlin.ui.project

import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import com.example.caisw.wallpaperbykotlin.app.MyApplication
import com.example.caisw.wallpaperbykotlin.core.ProjectFactory

class LifeGameWallPaperProjectDisplayFragment : WallpaperProjectDisplayFragment() {
    companion object {

        fun createArguments(): Bundle {
            val data = Bundle()
            data.putString("projectTag", ProjectFactory.TAG_LIFE_GAME_WALLPAPER)
            return data
        }

    }

    override fun initListener() {
        super.initListener()
        settingWallpaperButton?.setOnClickListener {
            MyApplication.instance.spDataCache.common.setWallPaperType(ProjectFactory.TAG_LIFE_GAME_WALLPAPER)
            var intent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
            startActivity(intent)
        }
    }
}