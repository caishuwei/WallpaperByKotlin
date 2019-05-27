package com.example.caisw.wallpaperbykotlin.core.project.impl

import com.example.caisw.wallpaperbykotlin.core.spirit.search.AStar
import com.example.caisw.wallpaperbykotlin.core.spirit.search.SearchBase
import com.example.caisw.wallpaperbykotlin.core.surface.SurfaceHolderProvider

class AStarSearchProject (surfaceHolderProvider: SurfaceHolderProvider) : BaseSearchProject(surfaceHolderProvider) {
    override fun getSearchImpl(): SearchBase {
        return AStar()
    }
}