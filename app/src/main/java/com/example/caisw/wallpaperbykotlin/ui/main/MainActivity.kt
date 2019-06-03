package com.example.caisw.wallpaperbykotlin.ui.main

import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Intent
import com.example.caisw.wallpaperbykotlin.R
import com.example.caisw.wallpaperbykotlin.core.ProjectFactory
import com.example.caisw.wallpaperbykotlin.ui.base.BaseActivity
import com.example.caisw.wallpaperbykotlin.ui.base.CommonFragmentActivity
import com.example.caisw.wallpaperbykotlin.ui.project.ProjectDisplayFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun getContentViewResId(): Int {
        return R.layout.activity_main
    }

    override fun initListener() {
        super.initListener()
        v_benxi.setOnClickListener {
            CommonFragmentActivity.openActivity(
                    this@MainActivity,
                    ProjectDisplayFragment::class.java,
                    ProjectDisplayFragment.createArguments(ProjectFactory.TAG_BEN_XI)
            )
        }
        v_wallpaper.setOnClickListener {
            CommonFragmentActivity.openActivity(
                    this@MainActivity,
                    ProjectDisplayFragment::class.java,
                    ProjectDisplayFragment.createArguments(ProjectFactory.TAG_WALLPAPER)
            )
        }
        v_retro_snaker.setOnClickListener {
            CommonFragmentActivity.openActivity(
                    this@MainActivity,
                    ProjectDisplayFragment::class.java,
                    ProjectDisplayFragment.createArguments(ProjectFactory.TAG_RETRO_SNAKER)
            )
        }
        v_bfs.setOnClickListener {
            CommonFragmentActivity.openActivity(
                    this@MainActivity,
                    ProjectDisplayFragment::class.java,
                    ProjectDisplayFragment.createArguments(ProjectFactory.TAG_BFS)
            )
        }
        v_dfs.setOnClickListener {
            CommonFragmentActivity.openActivity(
                    this@MainActivity,
                    ProjectDisplayFragment::class.java,
                    ProjectDisplayFragment.createArguments(ProjectFactory.TAG_DFS)
            )
        }
        v_ucs.setOnClickListener {
            CommonFragmentActivity.openActivity(
                    this@MainActivity,
                    ProjectDisplayFragment::class.java,
                    ProjectDisplayFragment.createArguments(ProjectFactory.TAG_UCS)
            )
        }
        v_a.setOnClickListener {
            CommonFragmentActivity.openActivity(
                    this@MainActivity,
                    ProjectDisplayFragment::class.java,
                    ProjectDisplayFragment.createArguments(ProjectFactory.TAG_A_STAR)
            )
        }
        v_ida.setOnClickListener {
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
    }

}