package com.example.caisw.wallpaperbykotlin.ui.project

import android.graphics.PixelFormat
import android.os.Bundle
import android.view.SurfaceHolder
import com.example.caisw.wallpaperbykotlin.R
import com.example.caisw.wallpaperbykotlin.core.ProjectFactory
import com.example.caisw.wallpaperbykotlin.core.project.IProject
import com.example.caisw.wallpaperbykotlin.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_project_display.*

open class ProjectDisplayFragment : BaseFragment() {

    companion object {

        fun createArguments(projectTag: String): Bundle {
            val data = Bundle()
            data.putString("projectTag", projectTag)
            return data
        }

    }

    private var project: IProject? = null

    override fun getContentViewResId(): Int {
        return R.layout.fragment_project_display
    }

    override fun initListener() {
        super.initListener()
        surfaceView.setOnTouchListener { v, event ->
            project?.let {
                if (it.needHandleMotionEvent()) {
                    it.handleMotionEvent(event)
                }
            }
            return@setOnTouchListener true
        }
        surfaceView.getSurfaceHolder()?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                project?.onSizeChanged(width, height)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
            }
        })
    }

    override fun initData() {
        super.initData()
        val projectTag = arguments?.getString("projectTag")
        if (projectTag != null) {
            project = ProjectFactory.getProject(projectTag, surfaceView)
            project?.onCreate()
        }
    }

    override fun onResume() {
        super.onResume()
        project?.onResume()
    }

    override fun onPause() {
        super.onPause()
        project?.onPause()
    }

    override fun onDestroyView() {
        project?.onDestroy()
        super.onDestroyView()
    }
}