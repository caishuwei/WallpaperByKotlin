package com.example.caisw.wallpaperbykotlin.module

import android.graphics.Point

/**
 * Created by caisw on 2018/3/13.
 */
class Constants {

    companion object {
        val POINT_IN_2D = Array<Array<Point>>(5) { index -> Array<Point>(8) { index2 -> Point(index, index2) } }

        val Number_1 = Array<MutableList<Point>>(1) { _ ->
            val facePoints = mutableListOf<Point>()
            facePoints.add(POINT_IN_2D[3][0])
            facePoints.add(POINT_IN_2D[4][0])
            facePoints.add(POINT_IN_2D[4][7])
            facePoints.add(POINT_IN_2D[3][7])
            facePoints
        }

        val Number_2 = Array<MutableList<Point>>(1) { _ ->
            val facePoints = mutableListOf<Point>()
            facePoints.add(POINT_IN_2D[0][0])
            facePoints.add(POINT_IN_2D[4][0])
            facePoints.add(POINT_IN_2D[4][4])
            facePoints.add(POINT_IN_2D[1][4])
            facePoints.add(POINT_IN_2D[1][6])
            facePoints.add(POINT_IN_2D[4][6])
            facePoints.add(POINT_IN_2D[4][7])
            facePoints.add(POINT_IN_2D[0][7])
            facePoints.add(POINT_IN_2D[0][3])
            facePoints.add(POINT_IN_2D[3][3])
            facePoints.add(POINT_IN_2D[3][1])
            facePoints.add(POINT_IN_2D[0][1])
            facePoints
        }

    }

}