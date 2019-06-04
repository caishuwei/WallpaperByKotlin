package com.example.caisw.wallpaperbykotlin.core.spirit.snaker

import android.content.res.Resources
import android.graphics.*
import android.widget.Toast
import com.example.caisw.wallpaperbykotlin.app.MyApplication
import com.example.caisw.wallpaperbykotlin.core.spirit.Spirit
import com.example.caisw.wallpaperbykotlin.utils.ScreenInfo
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * 寻路算法实现自动贪吃蛇
 */
class RetroSnaker : Spirit() {
    private var wNum: Int
    private var hNum: Int
    private val map: Array<Array<RectF>>//地图
    private val snaker = LinkedList<Point>()//蛇的身体（双向队列）
    private var fruit = Point()//果实

    private var snakerPath = Path()
    private val snakerPaint = Paint()


    init {
        val wSize = ScreenInfo.WIDTH.toFloat()
        val hSize = ScreenInfo.HEIGHT.toFloat()
        val unitSize = ScreenInfo.dp2px(20f)//预估一个方块的大小
        wNum = (wSize / unitSize).toInt()//横向方块数量
        hNum = (hSize / unitSize).toInt()//纵向方块数量
        //求得每个方块的区域
        map = Array<Array<RectF>>(wNum) { wIndex ->
            Array<RectF>(hNum) { hIndex ->
                return@Array RectF(
                        wSize * wIndex / wNum,
                        hSize * hIndex / hNum,
                        wSize * (wIndex + 1) / wNum,
                        hSize * (hIndex + 1) / hNum
                )
            }
        }
        //画笔设置
        snakerPaint.strokeWidth = unitSize / 2
        snakerPaint.strokeCap = Paint.Cap.SQUARE
        snakerPaint.color = Color.WHITE
        snakerPaint.style = Paint.Style.STROKE

        //初始化蛇
        val centerPoint = Point(wNum / 2, hNum / 2)
        snaker.offer(centerPoint)
        snaker.offer(Point(centerPoint.x, centerPoint.y + 1))
        snaker.offer(Point(centerPoint.x, centerPoint.y + 2))
        updateSnakerPath()

        randomFruit()
    }

    private fun updateSnakerPath() {
        val iterator = snaker.iterator()
        val newPath = Path()
        var point: Point
        while (iterator.hasNext()) {
            point = iterator.next()
            if (newPath.isEmpty) {
                newPath.moveTo(map[point.x][point.y].centerX(), map[point.x][point.y].centerY())
            } else {
                newPath.lineTo(map[point.x][point.y].centerX(), map[point.x][point.y].centerY())
            }
        }
        snakerPath = newPath
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        snakerPaint.color = Color.WHITE
        canvas.drawPath(snakerPath, snakerPaint)
        canvas.drawPoint(map[fruit.x][fruit.y].centerX(), map[fruit.x][fruit.y].centerY(), snakerPaint)
        if (!snaker.isEmpty()) {
            val snakerHead = snaker.peekLast()
            snakerPaint.color = Color.RED
            canvas.drawPoint(map[snakerHead.x][snakerHead.y].centerX(), map[snakerHead.x][snakerHead.y].centerY(), snakerPaint)
        }
    }


    private var aiExecuteDisposable: Disposable? = null
    fun start() {
        //采用RxJava实现绘制线程
        aiExecuteDisposable?.dispose()
        aiExecuteDisposable = Observable.create(ObservableOnSubscribe<Point> {
            while (!it.isDisposed) {
                var beginDrawTime: Long
                var endDrawTime: Long
                try {
                    it.onNext(findNextStep())
                    Thread.sleep(500)
                } catch (e: Exception) {
                    if (!it.isDisposed) {
                        it.onError(e)
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())//切换到子线程执行
                .subscribe(
                        {
                            snaker.offer(it)
                            if (it.equals(fruit)) {
                                //吃到食物
                                //生成新的食物
                                randomFruit()
                            } else {
                                //没吃到食物，去掉蛇尾一个点
                                snaker.pop()
                            }
                            updateSnakerPath()
                        },
                        {
                            Toast.makeText(MyApplication.instance, it.message, Toast.LENGTH_SHORT).show()
                        }
                )
    }

    //寻找下一步
    private fun findNextStep(): Point {
        //建立地图单位数组
        val unitArray = Array<IntArray>(wNum) { IntArray(hNum) }
        //赋值蛇的单位为1
        val iterator = snaker.iterator()
        var point: Point
        while (iterator.hasNext()) {
            point = iterator.next()
            unitArray[point.x][point.y] = 1
        }
        //从起点开始A*搜索到终点的路径
        val nodeList = LinkedList<Node>()
        val snakerHead = snaker.peekLast()
        nodeList.offer(Node(snakerHead.x, snakerHead.y, null))
        var result: Point?
        do {
            result = aStartSearch(nodeList, unitArray)
        } while (result == null && !nodeList.isEmpty())
        if (result == null) {
            throw Resources.NotFoundException("无路可走")
        } else {
            return result
        }
    }

    private fun aStartSearch(nodeList: LinkedList<Node>, unitArray: Array<IntArray>): Point? {
        var node = nodeList.poll()
        if (node.equals(fruit)) {
            //已经找到终点，返回下一步要走的点
            var parent: Node?
            while (true) {
                parent = node.parent
                if (parent != null) {
                    if (parent.parent == null) {//父节点是开始节点
                        return node//返回这个节点
                    } else {
                        node = parent
                    }
                } else {
                    return null
                }
            }
        } else {
            //不是终点,搜寻附近点加入队列
            insertNearNodes(nodeList, unitArray, node)
        }
        return null
    }

    /**
     * 插入附近节点到队列
     */
    private fun insertNearNodes(nodeList: LinkedList<Node>, unitArray: Array<IntArray>, node: Node) {
        //左
        var nearNode = getNodeIfEnable(node.x - 1, node.y, node, unitArray)
        if (nearNode != null) {
            insertToLinkList(nodeList, nearNode)
        }
        nearNode = getNodeIfEnable(node.x, node.y - 1, node, unitArray)
        if (nearNode != null) {
            insertToLinkList(nodeList, nearNode)
        }
        nearNode = getNodeIfEnable(node.x + 1, node.y, node, unitArray)
        if (nearNode != null) {
            insertToLinkList(nodeList, nearNode)
        }
        nearNode = getNodeIfEnable(node.x, node.y + 1, node, unitArray)
        if (nearNode != null) {
            insertToLinkList(nodeList, nearNode)
        }
    }


    /**
     * 将节点插入队列
     */
    private fun insertToLinkList(nodeList: LinkedList<Node>, nearNode: Node) {
        //计算代价
        val parent = nearNode.parent
        parent?.let {
            nearNode.g = it.g + 1
        }
        //使用曼哈顿距离，计算与终点的距离
        nearNode.h = (Math.abs(nearNode.x - fruit.x) + Math.abs(nearNode.y - fruit.y)).toFloat()
        //欧几里得距离
//        nearNode.h = Math.sqrt(Math.pow((nearNode.x - endPoint.x).toDouble(), 2.0) + Math.pow((nearNode.y - endPoint.y).toDouble(), 2.0)).toFloat()
        //按代价大小插入队列
        val iterator = nodeList.iterator()
        var index = 0
        while (iterator.hasNext()) {
            if (iterator.next().f() > nearNode.f()) {
                break
            }
            index++
        }
        nodeList.add(index, nearNode)
    }

    /**
     * 如果该坐标节点可用则返回
     */
    private fun getNodeIfEnable(x: Int, y: Int, parent: Node?, unitArray: Array<IntArray>): Node? {
        if (x in 0 until wNum
                && y in 0 until hNum) {
            if (unitArray[x][y] == 0) {
                unitArray[x][y] == 2//2为已经搜索过的点
                return Node(x, y, parent)
            }
        }
        return null
    }

    /**
     * 随机一个果实
     */
    private fun randomFruit() {
        //建立地图单位数组
        val unitArray = Array<IntArray>(wNum) { IntArray(hNum) }
        //赋值蛇的单位为1
        val iterator = snaker.iterator()
        var point: Point
        while (iterator.hasNext()) {
            point = iterator.next()
            unitArray[point.x][point.y] = 1
        }
        //生成未使用的单位列表
        var unUseUnitCount = 0
        val unUserUnitList = LinkedList<Point>()
        for (w in 0 until wNum) {
            for (h in 0 until hNum) {
                if (unitArray[w][h] != 1) {
                    unUseUnitCount++
                    unUserUnitList.add(Point(w, h))
                }
            }
        }
        if (unUserUnitList.isEmpty()) {
            stop()
        } else {
            val destIndex = (Math.random() * unUseUnitCount).toInt()
            fruit = unUserUnitList[destIndex]
        }
    }

    fun stop() {
        aiExecuteDisposable?.dispose()
        aiExecuteDisposable = null
    }

    private inner class Node : Point {
        val parent: Node?

        constructor(x: Int, y: Int, parent: Node?) : super(x, y) {
            this.parent = parent
        }

        /*起点移动到当前点的代价*/
        var g = 0F
        /**
         * 当前点距离终点的启发式评估代价
         * 启发值的计算常用的有两种（A->B）
         * 1、曼哈顿距离：曼哈顿街区出租车的最短行车距离，h = Math.abs(A.x - B.x)+ Math.abs(A.y-B.y)
         * 2、欧几里得距离：h = Math.sqrt(Math.pow(A.x - B.x)+Math.pow(A.y-B.y))
         */
        var h = 0F

        fun f(): Float {
            return g + h
        }
    }

}