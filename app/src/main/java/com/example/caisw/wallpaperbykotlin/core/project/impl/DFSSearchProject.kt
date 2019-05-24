package com.example.caisw.wallpaperbykotlin.core.project.impl

import com.example.caisw.wallpaperbykotlin.core.spirit.search.DepthFirstSearch
import com.example.caisw.wallpaperbykotlin.core.spirit.search.SearchBase
import com.example.caisw.wallpaperbykotlin.core.surface.SurfaceHolderProvider

class DFSSearchProject(surfaceHolderProvider: SurfaceHolderProvider) : BaseSearchProject(surfaceHolderProvider) {
    override fun getSearchImpl(): SearchBase {
        return DepthFirstSearch()
    }
}