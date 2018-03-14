package com.example.caisw.wallpaperbykotlin.spirit

import android.graphics.*
import android.os.SystemClock
import com.example.caisw.wallpaperbykotlin.R
import com.example.caisw.wallpaperbykotlin.app.MyApplication

/**
 * Created by caisw on 2018/3/14.
 */
class Picture : BaseSpirit {
    private val benxi: Bitmap//图片
    private val camera: Camera//3d变换应用
    private var pictureMatrix: Matrix
    private var createTime = 0L
    private var loopTime = 5 * 1000

    private var degress: Float

    constructor() : super() {
        benxi = BitmapFactory.decodeResource(MyApplication.instance.resources, R.drawable.benxi)
        camera = Camera()
        //设置摄像机位置（一个单位(英寸)72像素）
        camera.setLocation(-(MyApplication.instance.screenInfoProvider.screenWidth() - benxi.width) / 2F / 72, (MyApplication.instance.screenInfoProvider.screenHeight() - benxi.height) / 2F / 72, -100F)
        pictureMatrix = Matrix()
        createTime = SystemClock.uptimeMillis()
        degress = Math.toDegrees(Math.atan(benxi.width * 1.0 / benxi.height)).toFloat()
    }

    override fun drawMySelf(canvas: Canvas) {
//        canvas.drawBitmap(benxi, (MyApplication.instance.screenInfoProvider.screenWidth() - benxi.width) / 2F, (MyApplication.instance.screenInfoProvider.screenHeight() - benxi.height) / 2F, null)
        canvas.save()
//        camera.applyToCanvas(canvas)
        camera.save()
        camera.rotateY(getRotateYByCurrTime())
        camera.getMatrix(pictureMatrix)
        camera.restore()
        //场景移动，使图片中心在原点上，进行旋转使对角线与Y轴重合
        pictureMatrix.preRotate(degress)
        pictureMatrix.preTranslate(-benxi.width / 2F, -benxi.height / 2F)
        //最后对场景移回开始的坐标
        pictureMatrix.postTranslate(benxi.width / 2F, benxi.height / 2F)
        canvas.concat(pictureMatrix)
        canvas.drawBitmap(benxi, 0F, 0F, null)
        canvas.restore()
    }

    private fun getRotateYByCurrTime(): Float {
        return (SystemClock.uptimeMillis() - createTime) % loopTime * 360F / loopTime
    }
}