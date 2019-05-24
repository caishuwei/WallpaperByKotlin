package com.example.caisw.wallpaperbykotlin.core.spirit.search

import android.graphics.Canvas
import com.example.caisw.wallpaperbykotlin.entities.NodeWithPathCost
import java.util.*

/**
 * 一致代价搜索UCS（寻找最短路径）
 *
 * BFS是基于每一步花费的代价相同的图上，对于通过每一格的花费的代价不同的图来讲，BFS并不是最好的做法
 *
 * BFS是特定情况下的UCS
 * UCS基于每一格花费的代价不同，按照当前路径花费的代价高低加入队列，由低到高排序，
 * 取出最低花费的搜索路径，寻找下一步，判断是否是终点，不是则按照当前花费加入队列
 *
 * BFS可以确定寻找的下一个路径距离是大于或等于之前的路径的，所以对走过的点可以排除出检索范围
 *
 * UCS由于不同的两个点组成的路径代价不同，所以已经走过的点从别的地方走可能又变近了，所以之前走过的点，别的路径也可以走，
 * 而搜索过程是将路径短的开始搜，直到终点，这个距离内的路径有好多种排列组合，所以终点距离稍远的话，就要花费巨大的代价去搜索
 * 这里已10*10的范围为例
 *
 * 这个算法是属于盲目搜索，用这个做寻路很亏，接下来了解启发式搜索，会大大提高寻路性能
 */
class UniformCostSearch : SearchBase(10) {
    private val BASE_HEIGHT = 1000//基础高度
    private val HEIGHT_VALUE_RAGNE = 6//高度变化范围（整个地图横向才60，高度的取值要适当，免得某些地方变成珠穆朗玛峰）
    val height = Array(count) { IntArray(count) }//高度信息存储
    private val nodeLinkList = LinkedList<NodeWithPathCost>()
    private var currNode: NodeWithPathCost? = null

    init {
        //为空白区域生成高度信息,两个不同高度区域间的跨越代价不同，用勾股定理来计算：水平方向距离 = 1,高度差 = ?,求斜边
        for (x in 0 until count) {
            for (y in 0 until count) {
                //这里进行赋值
                height[x][y] = BASE_HEIGHT + ((Math.random() - 0.5F) * HEIGHT_VALUE_RAGNE).toInt()
            }
        }
    }

    override fun nextStep(): Boolean {
        super.nextStep()
        if (!nodeLinkList.isEmpty()) {
            val node = nodeLinkList.poll()
            currNode = node
            if (map[node.x][node.y] == FLAG_END) {
                //已经找到终点
                nodeLinkList.clear()
                return false
            } else {
                //不是终点
                //将临近点加入队列(左上右下)
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
            if (nodeLinkList.isEmpty()) {
                //已经检索完所有点都没找到
                currNode = null
            } else {
                //还需要检索
                return true
            }
        }
        return false
    }

    private fun insertToLinkList(nearNode: NodeWithPathCost) {
        //确定这个点不在当前的路径里面
        var parent = nearNode.parent
        while (parent != null) {
            if (parent.x == nearNode.x && parent.y == nearNode.y) {
                return
            }
            parent = parent.parent
        }
        //计算代价
        parent = nearNode.parent
        parent?.let {
            nearNode.cost = it.cost + (Math.sqrt(Math.pow(1.0, 2.0) + Math.pow((height[nearNode.x][nearNode.y] - height[it.x][it.y]).toDouble(), 2.0))).toFloat()
        }
        //按代价大小插入队列
        val iterator = nodeLinkList.iterator()
        var index = 0
        while (iterator.hasNext()) {
            if (iterator.next().cost > nearNode.cost) {
                break
            }
            index++
        }
        nodeLinkList.add(index, nearNode)
    }

    private fun createNode(x: Int, y: Int, node: NodeWithPathCost): NodeWithPathCost? {
        if (x in 0 until count
                && y in 0 until count
        ) {
            var result: NodeWithPathCost? = null
            if (map[x][y] == FLAG_DEFAULT) {
                result = NodeWithPathCost(x, y, node)
            } else if (map[x][y] == FLAG_END) {
                result = NodeWithPathCost(x, y, node)
            }
            return result
        }
        return null
    }

    override fun start() {
        currNode = null
        nodeLinkList.clear()
        //将起点加入检索队列
        startNode?.let {
            nodeLinkList.offer(NodeWithPathCost(it.x, it.y, null))
        }
        super.start()
    }

    override fun end() {
        super.end()
        //清除检索过程产生的标记
        for (x in 0 until count) {
            for (y in 0 until count) {
                if (map[x][y] == FLAG_WAITING_CHECK || map[x][y] > 0) {
                    map[x][y] = FLAG_DEFAULT
                }
            }
        }
        //清空检索队列与当前节点
        nodeLinkList.clear()
        currNode = null
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
                    FLAG_DEFAULT -> {
                        commonPaint.color = getHeightColor(height[x][y])
                        drawRect(x, y, canvas, commonPaint)
                    }
                }
            }
        }
        //绘制当前检索的路径
        var node = currNode
        while (node != null) {
            if (map[node.x][node.y] == FLAG_DEFAULT) {
                commonPaint.color = COLOR_PATH
                drawRect(node.x, node.y, canvas, commonPaint)
            }
            node = node.parent
        }
    }

    private fun getHeightColor(height: Int): Int {
        var rate = (height + HEIGHT_VALUE_RAGNE / 2F - BASE_HEIGHT) / HEIGHT_VALUE_RAGNE

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

}