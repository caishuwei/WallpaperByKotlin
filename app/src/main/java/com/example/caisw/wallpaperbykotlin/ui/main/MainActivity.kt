package com.example.caisw.wallpaperbykotlin.ui.main

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

        }
    }

}