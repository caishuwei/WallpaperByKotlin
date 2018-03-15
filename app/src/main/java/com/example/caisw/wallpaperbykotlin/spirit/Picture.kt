package com.example.caisw.wallpaperbykotlin.spirit

import android.graphics.*
import android.os.SystemClock
import com.example.caisw.wallpaperbykotlin.R
import com.example.caisw.wallpaperbykotlin.app.MyApplication

/**
 * Created by caisw on 2018/3/14.
 */
class Picture : BaseSpirit {
    private var scaleRate = 0.8F

    private var benxi: Bitmap//图片
    private val camera: Camera//3d变换应用
    private var pictureMatrix: Matrix
    private var createTime = 0L
    private var loopTime = 5 * 1000

    private var degress: Float

    constructor() : super() {
        benxi = BitmapFactory.decodeResource(MyApplication.instance.resources, R.drawable.benxi)
        benxi = pictureDecorate(benxi)

        camera = Camera()
        camera.setLocation(0F, 0F, -100F)//设置摄像机位置（一个单位(英寸)72像素，修改Z位置拉远镜头，可以减轻图像变形）
        pictureMatrix = Matrix()
        createTime = SystemClock.uptimeMillis()
        degress = Math.toDegrees(Math.atan(benxi.width * 1.0 / benxi.height)).toFloat()
        x = MyApplication.instance.screenInfoProvider.screenWidth() / 2F
        y = MyApplication.instance.screenInfoProvider.screenHeight() / 2F

    }

    /**
     * 图片加工
     */
    private fun pictureDecorate(benxi: Bitmap): Bitmap {
        val bm = Bitmap.createBitmap(benxi.width, benxi.height, Bitmap.Config.ARGB_8888)
        val bmC = Canvas(bm)
        val paint = Paint()
        paint.maskFilter = BlurMaskFilter(scaleRate * 40, BlurMaskFilter.Blur.INNER)//边缘模糊
        bmC.drawBitmap(benxi, 0F, 0F, paint)

        val name = "本兮"
        val textPaint = Paint()
        val textRect = Rect()
        textPaint.color = Color.WHITE
        textPaint.textSize = 30F
        textPaint.getTextBounds(name, 0, name.length, textRect)
        bmC.drawText(name, bm.width - textRect.width().toFloat() - scaleRate * 40, bm.height * 0.9F - textRect.top, textPaint)
        val day = "1994.06.30~2016.12.24"
        textPaint.getTextBounds(day, 0, day.length, textRect)
        bmC.drawText(day, bm.width - textRect.width().toFloat() - scaleRate * 40, bm.height * 0.95F - textRect.top, textPaint)

        return bm
    }

    override fun drawMySelf(canvas: Canvas) {
        canvas.save()
        //1、生成图片变幻矩阵（图片的缩放，旋转，位移）
        camera.save()
        camera.rotate(0F, getRotateYByCurrTime(), -degress)//进行旋转
        camera.getMatrix(pictureMatrix)//读取变幻矩阵
        camera.restore()
        pictureMatrix.preTranslate(-benxi.width / 2F, -benxi.height / 2F)//使用相机进行变换之前进行场景移动，使图片中心在原点上
        pictureMatrix.postScale(scaleRate, scaleRate)//进行矩阵缩放，无法直接在相机里面缩放

        //2、场景位移到精灵所在位置进行绘制
        canvas.translate(x, y)
        canvas.drawBitmap(benxi, pictureMatrix, null)//绘制图片
        canvas.restore()
    }

    private fun getRotateYByCurrTime(): Float {
        return (SystemClock.uptimeMillis() - createTime) % loopTime * 360F / loopTime
    }
}