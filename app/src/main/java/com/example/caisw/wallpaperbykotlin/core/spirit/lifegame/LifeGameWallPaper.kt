package com.example.caisw.wallpaperbykotlin.core.spirit.lifegame

import android.graphics.*
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import com.example.caisw.wallpaperbykotlin.core.spirit.Spirit
import com.example.caisw.wallpaperbykotlin.utils.ScreenInfo
import java.util.*
import kotlin.collections.ArrayList

class LifeGameWallPaper : Spirit() {

    private val data = Data()

    init {
//        var cell: Cell? = null
//        for (x in 0..1) {
//            for (y in 0..1) {
//                cell = data.getCell(x, y, true)
//                cell?.let {
//                    it.setAlive(true)
//                    data.onCellAliveUpdate(it)
//                }
//            }
//        }
        startEvolve()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        data.lockBitmap()?.let {
            canvas.drawBitmap(it, data.getBitmapMatrix(it), null)
            data.unlockBitmap(it)
        }
    }

    fun onTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                stopEvolve()
            }
            MotionEvent.ACTION_MOVE -> {
                val cell = data.getCell(event.x.toInt(), event.y.toInt(), true)
                cell?.let {
                    it.setAlive(true)
                    data.onCellAliveUpdate(it)
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                startEvolve()
            }
        }
    }

    private var subThreadHandler: SubThreadHandler? = null
    public fun startEvolve() {
        stopEvolve()
        subThreadHandler = SubThreadHandler()
        subThreadHandler?.executeEvolve()
    }

    public fun stopEvolve() {
        subThreadHandler?.destroy()
        subThreadHandler = null
    }

    fun setVisibleRectF(l: Float, t: Float, r: Float, b: Float) {
        data.visibleRectF.set(l, t, r, b)
    }


    private inner class Data() {
        val cellColor: Int = Color.WHITE
        val minWidth = 30
        val minHeight = 30
        val maxWidth = 200
        val maxHeight = 200
        val visibleRectF = RectF(
                0f,
                0f,
                ScreenInfo.WIDTH.toFloat(),
                ScreenInfo.HEIGHT.toFloat()
        )
        private var lockBitmap: Bitmap? = null
        private var bitmap: Bitmap? = null
        private var bitmapMatrix = Matrix()
        private val cacheCellMap = HashMap<Point, Cell>()
        private val aliveCellMap = HashMap<Point, Cell>()
        val evolveTime: Long = 200

        fun getCell(x: Int, y: Int, createIfIsNull: Boolean = false): Cell? {
            val cell = cacheCellMap[Point(x, y)]
            return if (createIfIsNull && cell == null) {
                if (x < 0
                        || y < 0
                        || x > maxWidth
                        || y > maxHeight) {
                    return null
                }
                val newInstance = Cell(x, y)
                cacheCellMap[Point(x, y)] = newInstance
                newInstance
            } else {
                cell
            }
        }

        fun onCellAliveUpdate(cell: Cell) {
            if (cell.isAlive(cell.lastEvolveTime)) {
                aliveCellMap[Point(cell.x, cell.y)] = cell
            } else {
                aliveCellMap.remove(Point(cell.x, cell.y))
            }
        }

        fun getAliveCells(): MutableCollection<Cell> {
            val values = aliveCellMap.values
            val copy = ArrayList<Cell>()
            copy.addAll(values)
            return copy
        }

        fun updateBimmap(bitmap: Bitmap) {
            this.bitmap?.let {
                if (it != lockBitmap) {
                    //当前bitmap没被锁,那这个bitmap可以回收
                    it.recycle()
                }
            }
            this.bitmap = bitmap
        }

        fun lockBitmap(): Bitmap? {
            bitmap?.let {
                lockBitmap = it
            }
            return bitmap
        }

        fun unlockBitmap(bitmap: Bitmap) {
            if (this.bitmap != bitmap) {
                bitmap.recycle()
            }
        }

        fun getBitmapMatrix(bitmap: Bitmap): Matrix {
            //将bitmap显示到屏幕中间
            val bitmapW = bitmap.width
            val bitmapH = bitmap.height
            val containerW = visibleRectF.width()
            val containerH = visibleRectF.height()
            val scaleX = containerW / bitmapW
            val scaleY = containerH / bitmapH
            //图片要居中到屏幕，不管宽高是放大还是缩小，我们都最小缩放值
            val scale = Math.min(scaleX, scaleY)
            bitmapMatrix.reset()
//            bitmapMatrix.postScale(10f, 10f)//先缩放
            bitmapMatrix.postScale(scale, scale)//先缩放
            bitmapMatrix.postTranslate((containerW - bitmapW * scale) / 2, (containerH - bitmapH * scale) / 2)//再偏移
            return bitmapMatrix
        }

    }

    /**
     * 细胞
     */
    private inner class Cell(val x: Int, val y: Int) {
        private val cells = Array<Array<Cell?>>(3) {
            Array<Cell?>(3) {
                null
            }
        }

        /**
         * 取得细胞，此方法可以通过传入偏移量取得周边细胞，偏移量范围[-1,1]
         */
        fun getCell(offsetX: Int = 0, offsetY: Int = 0, createIfIsNull: Boolean = false): Cell? {
            val cell = cells[offsetX + 1][offsetY + 1]
            if (cell == null) {
                val instance = data.getCell(x + offsetX, y + offsetY, createIfIsNull)
                cells[offsetX + 1][offsetY + 1] = instance
                return instance
            }
            return cell
        }

        /*最后演变时间*/
        var lastEvolveTime: Long? = null
        /*旧存活状态*/
        var aliveBeforeEvlove = false
        /*存活状态*/
        private var aliveAfterEvolve = false

        /**
         * 执行演变
         * @return 演变后细胞是否还存活
         */
        fun executeEvolve(evolveTime: Long): Boolean {
            if (lastEvolveTime == evolveTime) {
                return aliveAfterEvolve
            }
            lastEvolveTime = evolveTime
            var aliveCount = 0
            countAlive@ for (offsetX in -1..1) {
                for (offsetY in -1..1) {
                    if (offsetX != 0 || offsetY != 0) {
                        getCell(offsetX, offsetY)?.let {
                            if (it.isAlive(evolveTime - 1)) {
                                aliveCount++
                            }
                        }
                        if (aliveCount > 3) {
                            break@countAlive
                        }
                    }
                }
            }
            aliveBeforeEvlove = aliveAfterEvolve
            if (aliveCount == 2) {
                //no change
            } else if (aliveCount == 3) {
                //存活
                if (!aliveAfterEvolve) {
                    aliveAfterEvolve = true
                    data.onCellAliveUpdate(this)
                }
            } else {
                //死亡
                if (aliveAfterEvolve) {
                    aliveAfterEvolve = false
                    data.onCellAliveUpdate(this)
                }
            }
            return aliveAfterEvolve
        }

        /**
         * 判断细胞在演变时间是否存活
         * @param evolveTime 演变时间
         */
        fun isAlive(evolveTime: Long? = null): Boolean {
            val let = lastEvolveTime
            if (let != null && evolveTime != null) {
                return if (let <= evolveTime) {
                    aliveAfterEvolve
                } else {
                    aliveBeforeEvlove
                }
            }
            return aliveBeforeEvlove
        }

        fun setAlive(alive: Boolean) {
            aliveAfterEvolve = alive
            aliveBeforeEvlove = alive
            data.onCellAliveUpdate(this)
        }
    }

    private inner class SubThreadHandler(val myHandlerThread: MyHandlerThread = MyHandlerThread()) : Handler(myHandlerThread.looper) {
        var destroyed = false
        val msgWhatDestroy = -1
        val msgWhatExecuteEvolve = 1

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg == null) {
                return
            }
            when (msg.what) {
                msgWhatDestroy -> {
                    myHandlerThread.quit()
                }
                msgWhatExecuteEvolve -> {
                    //迭代演变存活细胞及其附近的细胞
                    val aliveCells = data.getAliveCells()
                    val evolveTime = System.currentTimeMillis()
                    val aliveRange = Rect()
                    var aliveCount = 0
                    for (cell in aliveCells) {
                        //将该cell周围包括自身9个细胞全部演变一次
                        for (x in -1..1) {
                            for (y in -1..1) {
                                cell.getCell(x, y, true)?.let {
                                    if (it.executeEvolve(evolveTime)) {
                                        aliveCount++;
                                        updateAliveRange(aliveRange, it.x, it.y)
                                    }
                                }
                            }
                        }
                    }
                    Log.i(this@LifeGameWallPaper::class.java.name, "aliveCount:$aliveCount")
                    //区域校正
                    if (aliveRange.width() < data.minWidth) {
                        val centerX = aliveRange.centerX()
                        aliveRange.left = centerX - data.minWidth / 2
                        aliveRange.right = centerX + data.minWidth / 2
                    } else if (aliveRange.width() > data.maxWidth) {
                        val centerX = aliveRange.centerX()
                        aliveRange.left = centerX - data.maxWidth / 2
                        aliveRange.right = centerX + data.maxWidth / 2
                    }
                    if (aliveRange.height() < data.minHeight) {
                        val centerY = aliveRange.centerY()
                        aliveRange.top = centerY - data.minHeight / 2
                        aliveRange.bottom = centerY + data.minHeight / 2
                    } else if (aliveRange.height() > data.maxHeight) {
                        val centerY = aliveRange.centerY()
                        aliveRange.top = centerY - data.maxHeight / 2
                        aliveRange.bottom = centerY + data.maxHeight / 2
                    }
                    //绘图
                    if (!aliveRange.isEmpty) {
                        val bitmap = Bitmap.createBitmap(aliveRange.width(), aliveRange.height(), Bitmap.Config.ARGB_8888)
                        val aliveCells2 = data.getAliveCells()
                        for (cell in aliveCells2) {
                            if (aliveRange.contains(cell.x, cell.y)) {
                                bitmap.setPixel(cell.x - aliveRange.left, cell.y - aliveRange.top, data.cellColor)
                            }
                        }
                        data.updateBimmap(bitmap)
                    }
                    //进行下一次演变
                    if (!destroyed) {
                        sendEmptyMessageDelayed(msgWhatExecuteEvolve, data.evolveTime)
                    }
                }
            }
        }

        private fun updateAliveRange(aliveRange: Rect, x: Int, y: Int) {
            if (aliveRange.left == 0
                    && aliveRange.right == 0
                    && aliveRange.top == 0
                    && aliveRange.bottom == 0) {
                aliveRange.set(x, y, x, y)
            } else {
                aliveRange.set(
                        Math.min(aliveRange.left, x),
                        Math.min(aliveRange.top, y),
                        Math.max(aliveRange.right, x),
                        Math.max(aliveRange.bottom, y)
                )
            }
        }

        /**
         * 关闭子线程
         */
        fun destroy() {
            destroyed = true
            Message.obtain(this, msgWhatDestroy).sendToTarget()
        }

        /**
         * 执行一次演变
         */
        fun executeEvolve() {
            Message.obtain(this, msgWhatExecuteEvolve).sendToTarget()
        }

    }

    private inner class MyHandlerThread : HandlerThread("生命游戏演变处理线程") {

        init {
            priority = Thread.MAX_PRIORITY
            start()
        }

    }

}

