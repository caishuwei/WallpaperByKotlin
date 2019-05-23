package com.example.caisw.wallpaperbykotlin.ui.base

import android.os.Bundle
import android.view.ViewGroup

interface IUICreator {
    fun getContentViewResId(): Int
    fun initView(viewGroup: ViewGroup, savedInstanceState: Bundle?)
    fun initAdapter()
    fun initListener()
    fun initData()
}