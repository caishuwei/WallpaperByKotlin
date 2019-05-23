package com.example.caisw.wallpaperbykotlin.ui.base

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity

abstract class BaseActivity : FragmentActivity(), IUICreator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentViewResId())
        initView(window.decorView as ViewGroup, savedInstanceState)
        initAdapter()
        initListener()
        initData()
    }

    override fun initView(viewGroup: ViewGroup, savedInstanceState: Bundle?) {
    }

    override fun initAdapter() {
    }

    override fun initListener() {
    }

    override fun initData() {
    }
}