package com.example.caisw.wallpaperbykotlin.core.project.impl

import com.example.caisw.wallpaperbykotlin.core.spirit.snaker.RetroSnaker
import com.example.caisw.wallpaperbykotlin.core.spirit.search.SearchBase
import com.example.caisw.wallpaperbykotlin.core.surface.SurfaceHolderProvider

class RetroSnakerProject(surfaceHolderProvider: SurfaceHolderProvider) : BaseSearchProject(surfaceHolderProvider) {
    override fun getSearchImpl(): SearchBase {
        return RetroSnaker()
    }
}