package com.example.caisw.wallpaperbykotlin.core.spirit.search

import android.graphics.Canvas
import java.util.*

/**
 * A*算法，是BFS的优化，在BFS的基础上加上用启发函数判断接下去应该走的方向
 *
 * BFS是从距离起点最近的点开始找，犹如洪水向四面八方扩散，边缘的点距离都是相等的，扩散到终点就找到了
 *
 * A*算法在此基础上，加上当前点到终点的代价作为启发值，这样同一层的点越往终点方向的点启发值越低，先从启发值小的路走
 */
class AStar : SearchBase(60) {
    val nodeList = LinkedList<Node>()
    private var currNode: Node? = null

    override fun start() {
        nodeList.clear()
        currNode = null
        startPoint?.let {
            nodeList.offer(Node(it.x, it.y, null))
        }
        super.start()
    }

    override fun end() {
        //清除检索过程产生的标记
        for (x in 0 until count) {
            for (y in 0 until count) {
                if (map[x][y] == FLAG_WAITING_CHECK || map[x][y] > 0) {
                    map[x][y] = 0
                }
            }
        }
        nodeList.clear()
        currNode = null
        super.end()
    }

    override fun nextStep(): Boolean {
        if (!nodeList.isEmpty()) {
            val node = nodeList.poll()
            currNode = node
            if (map[node.x][node.y] == FLAG_END) {
                //已经找到终点
                nodeList.clear()
                return false
            } else {
                //不是终点
                if (map[node.x][node.y] != FLAG_START) {
                    //标记这个点已经检索过
                    map[node.x][node.y] = node.g.toInt()
                }
                insertNearNodes(node)

            }
            if (nodeList.isEmpty()) {
                //已经检索完所有点都没找到
                currNode = null
            } else {
                //还需要检索
                return true
            }
        }
        return false
    }

    /**
     * 将附近节点，插入搜寻队列
     */
    open fun insertNearNodes(node: Node) {
        //左
        var nearNode = createNode(node.x - 1, node.y, node)
        if (nearNode != null) {
            insertToLinkList(nearNode);
        }
        //上
        nearNode = createNode(node.x, node.y - 1, node)
        if (nearNode != null) {
            insertToLinkList(nearNode);
        }
        //右
        nearNode = createNode(node.x + 1, node.y, node)
        if (nearNode != null) {
            insertToLinkList(nearNode);
        }
        //下
        nearNode = createNode(node.x, node.y + 1, node)
        if (nearNode != null) {
            insertToLinkList(nearNode);
        }
    }

    /**
     * 将节点插入队列
     */
    open fun insertToLinkList(nearNode: Node) {
        //计算代价
        val parent = nearNode.parent
        parent?.let {
            nearNode.g = it.g + 1
        }
        //使用曼哈顿距离，计算与终点的距离
        nearNode.h = (Math.abs(nearNode.x - endPoint.x) + Math.abs(nearNode.y - endPoint.y)).toFloat()
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

    private fun createNode(x: Int, y: Int, node: Node): Node? {
        if (x in 0 until count
                && y in 0 until count
        ) {
            var result: Node? = null
            if (map[x][y] == FLAG_DEFAULT) {
                map[x][y] = FLAG_WAITING_CHECK//标记为待检查，避免该点出现在多个路径中
                result = Node(x, y, node)
            } else if (map[x][y] == FLAG_END) {
                result = Node(x, y, node)
            }
            return result
        }
        return null
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        for (x in 0 until count) {
            for (y in 0 until count) {
                when (map[x][y]) {
                    FLAG_OBSTACLE -> {
                        commonPaint.color = COLOR_OBSTACLE
                        drawRect(x, y, canvas, commonPaint)
                    }
                    FLAG_START -> {
                        commonPaint.color = COLOR_START
                        drawRect(x, y, canvas, commonPaint)
                    }
                    FLAG_END -> {
                        commonPaint.color = COLOR_END
                        drawRect(x, y, canvas, commonPaint)
                    }
                    FLAG_WAITING_CHECK -> {
                        commonPaint.color = COLOR_WAITING_CHECK
                        drawRect(x, y, canvas, commonPaint)
                    }
                    in 1..count * count -> {
                        //已经检索过的位置
                        commonPaint.color = getCheckedColor(map[x][y])
                        drawRect(x, y, canvas, commonPaint)
                    }
                }
            }
        }
        //绘制当前检索的路径
        var node = currNode
        while (node != null) {
            if (map[node.x][node.y] > 0) {
                commonPaint.color = COLOR_PATH
                drawRect(node.x, node.y, canvas, commonPaint)
            }
            node = node.parent
        }
    }

    private fun getCheckedColor(g: Int): Int {
        var rate = 0F
        currNode?.let {
            if (it.g > 0) {
                rate = g * 1F / it.g
            }
        }
        val alpha = (192 - 90 * rate).toInt()
//        val startA = COLOR_START ushr 24
//        val startR = COLOR_START ushr 16 and 0xFF
//        val startG = COLOR_START ushr 8 and 0xFF
//        val startB = COLOR_START and 0xFF
//        val endA = COLOR_END ushr 24
//        val endR = COLOR_END ushr 16 and 0xFF
//        val endG = COLOR_END ushr 8 and 0xFF
//        val endB = COLOR_END and 0xFF
//        return Color.argb(
//                (startA + (endA - startA) * rate).toInt(),
//                (startR + (endR - startR) * rate).toInt(),
//                (startG + (endG - startG) * rate).toInt(),
//                (startB + (endB - startB) * rate).toInt()
//        )
        return alpha shl 24 or (COLOR_PATH and 0x00ffffff)
    }

    inner class Node {
        val x: Int
        val y: Int
        val parent: Node?

        /*起点移动到当前点的代价*/
        var g = 0F
        /**
         * 当前点距离终点的启发式评估代价
         * 启发值的计算常用的有两种（A->B）
         * 1、曼哈顿距离：曼哈顿街区出租车的最短行车距离，h = Math.abs(A.x - B.x)+ Math.abs(A.y-B.y)
         * 2、欧几里得距离：h = Math.sqrt(Math.pow(A.x - B.x)+Math.pow(A.y-B.y))
         */
        var h = 0F

        constructor(x: Int, y: Int, parent: Node?) {
            this.x = x
            this.y = y
            this.parent = parent
        }

        fun f(): Float {
            return g + h
        }
    }
}