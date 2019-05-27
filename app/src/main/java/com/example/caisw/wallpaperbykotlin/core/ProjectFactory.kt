package com.example.caisw.wallpaperbykotlin.core

import com.example.caisw.wallpaperbykotlin.core.project.IProject
import com.example.caisw.wallpaperbykotlin.core.project.impl.*
import com.example.caisw.wallpaperbykotlin.core.surface.SurfaceHolderProvider

class ProjectFactory {

    companion object {
        const val TAG_BEN_XI = "本兮壁纸"
        const val TAG_BFS = "广度优先搜索"
        const val TAG_DFS = "深度优先搜索"
        const val TAG_UCS = "一致代价搜索"
        const val TAG_A_STAR = "A星搜索"
        const val TAG_IDA_STAR = "迭代加深搜索"


        fun getProject(tag: String, surfaceHolderProvider: SurfaceHolderProvider): IProject? {
            return when (tag) {
                TAG_BEN_XI -> BenxiWallpaperProject(surfaceHolderProvider)
                TAG_BFS -> BFSSearchProject(surfaceHolderProvider)
                TAG_DFS -> DFSSearchProject(surfaceHolderProvider)
                TAG_UCS -> UCSSearchProject(surfaceHolderProvider)
                TAG_A_STAR -> AStarSearchProject(surfaceHolderProvider)
                TAG_IDA_STAR -> UCSSearchProject(surfaceHolderProvider)
                else -> null
            }
        }

    }


}