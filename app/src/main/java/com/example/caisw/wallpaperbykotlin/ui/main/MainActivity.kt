package com.example.caisw.wallpaperbykotlin.ui.main

import com.example.caisw.wallpaperbykotlin.R
import com.example.caisw.wallpaperbykotlin.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun getContentViewResId(): Int {
        return R.layout.activity_main
    }

    override fun initListener() {
        super.initListener()
        v_bfs.setOnClickListener {

        }
        v_dfs.setOnClickListener {

        }
        v_ucs.setOnClickListener {

        }
        v_a.setOnClickListener {

        }
        v_ida.setOnClickListener {

        }
    }

}