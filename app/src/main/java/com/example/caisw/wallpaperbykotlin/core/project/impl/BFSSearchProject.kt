package com.example.caisw.wallpaperbykotlin.core.project.impl

import com.example.caisw.wallpaperbykotlin.core.spirit.search.BreadthFirstSearch
import com.example.caisw.wallpaperbykotlin.core.spirit.search.SearchBase
import com.example.caisw.wallpaperbykotlin.core.surface.SurfaceHolderProvider

class BFSSearchProject(surfaceHolderProvider: SurfaceHolderProvider) : BaseSearchProject(surfaceHolderProvider) {
    override fun getSearchImpl(): SearchBase {
        return BreadthFirstSearch()
    }

}