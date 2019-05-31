package com.example.caisw.wallpaperbykotlin.utils

import android.graphics.Matrix
import android.graphics.RectF
import android.widget.ImageView.ScaleType

class MatrixUtils {

    companion object {
        fun setRectToRect(matrix: Matrix, src: RectF, dst: RectF, scaleType: ScaleType) {
            when (scaleType) {
                ScaleType.FIT_CENTER -> {
                    //等比缩放至边缘，并居中
                    matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER)
                }
                ScaleType.FIT_XY -> {
                    //缩放填充
                    matrix.setRectToRect(src, dst, Matrix.ScaleToFit.FILL)
                }
                ScaleType.FIT_START -> {
                    //缩放至宽度填满，置顶
                    matrix.setRectToRect(src, dst, Matrix.ScaleToFit.START)
                }
                ScaleType.FIT_END -> {
                    //缩放至宽度填满，置底
                    matrix.setRectToRect(src, dst, Matrix.ScaleToFit.END)
                }
                ScaleType.CENTER -> {
                    //不做缩放，居中
                    matrix.postTranslate(dst.centerX() - src.centerX(), dst.centerY() - src.centerY())
                }
                ScaleType.CENTER_CROP -> {
                    //居中，等比缩放将图片铺满，超出部分裁减
                    //1、居中
                    matrix.postTranslate(dst.centerX() - src.centerX(), dst.centerY() - src.centerY())
                    //2、缩放
                    val scaleX = dst.width() / src.width()
                    val scaleY = dst.height() / src.height()
                    //0~1为缩小 >1为放大
                    //x缩小，y缩小->取大值
                    //x缩小，y放大->取大值
                    //x放大，y缩小->取大值
                    //x放大，y放大->取大值
                    //所以无论如何都取大值喽
                    val maxScaleValue = Math.max(scaleX, scaleY)
                    matrix.postScale(maxScaleValue, maxScaleValue, dst.centerX(), dst.centerY())
                }
                ScaleType.CENTER_INSIDE -> {
                    //居中，让图片完全显示，只能缩小，不能放大
                    //1、居中
                    matrix.postTranslate(dst.centerX() - src.centerX(), dst.centerY() - src.centerY())
                    //2、缩放
                    val scaleX = Math.min(1f, dst.width() / src.width())
                    val scaleY = Math.min(1f, dst.height() / src.height())
                    //取小值，缩小幅度大，才能让图片完全显示
                    val minScaleValue = Math.min(scaleX, scaleY)
                    matrix.postScale(minScaleValue, minScaleValue, dst.centerX(), dst.centerY())
                }
                else -> {
                    setRectToRect(matrix, src, dst, ScaleType.FIT_CENTER)
                }
            }

        }
    }

}