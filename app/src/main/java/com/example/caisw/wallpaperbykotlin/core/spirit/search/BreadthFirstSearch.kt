package com.example.caisw.wallpaperbykotlin.core.spirit.search

import android.graphics.Canvas
import com.example.caisw.wallpaperbykotlin.entities.Node
import java.util.*

/**
 * 广度优先搜索
 * <br/>
 * 从出发点开始向外检索，由近及远，一层一层搜索直到到达目标位置，
 * 会比较耗性能，但找到的路径一定是最优的
 *
 */
class BreadthFirstSearch : SearchBase(60) {
    //用队列实现即可，先近先出（进入的节点由近及远）
    private val nodeQueue = LinkedList<Node>()
    private var currNode: Node? = null

    override fun nextStep(): Boolean {
        super.nextStep()
        if (!nodeQueue.isEmpty()) {
            val node = nodeQueue.poll()
            currNode = node
            if (map[node.x][node.y] == FLAG_END) {
                //已经找到终点
                nodeQueue.clear()
                return false
            } else {
                //不是终点
                if (map[node.x][node.y] != FLAG_START) {
                    //标记这个点已经检索过
                    map[node.x][node.y] = node.step
                }
                //将临近点加入队列(左上右下)
                //左
                var nearNode = createNode(node.x - 1, node.y, node)
                if (nearNode != null) {
                    when (map[nearNode.x][nearNode.y]) {
                        FLAG_END -> {
                            currNode = nearNode
                            nodeQueue.clear()
                            return false
                        }
                        FLAG_WAITING_CHECK -> {
                            nodeQueue.offer(nearNode)
                        }
                    }
                }
                //上
                nearNode = createNode(node.x, node.y - 1, node)
                if (nearNode != null) {
                    when (map[nearNode.x][nearNode.y]) {
                        FLAG_END -> {
                            currNode = nearNode
                            nodeQueue.clear()
                            return false
                        }
                        FLAG_WAITING_CHECK -> {
                            nodeQueue.offer(nearNode)
                        }
                    }
                }
                //右
                nearNode = createNode(node.x + 1, node.y, node)
                if (nearNode != null) {
                    when (map[nearNode.x][nearNode.y]) {
                        FLAG_END -> {
                            currNode = nearNode
                            nodeQueue.clear()
                            return false
                        }
                        FLAG_WAITING_CHECK -> {
                            nodeQueue.offer(nearNode)
                        }
                    }
                }
                //下
                nearNode = createNode(node.x, node.y + 1, node)
                if (nearNode != null) {
                    when (map[nearNode.x][nearNode.y]) {
                        FLAG_END -> {
                            currNode = nearNode
                            nodeQueue.clear()
                            return false
                        }
                        FLAG_WAITING_CHECK -> {
                            nodeQueue.offer(nearNode)
                        }
                    }
                }
            }
            if (nodeQueue.isEmpty()) {
                //已经检索完所有点都没找到
                currNode = null
            } else {
                //还需要检索
                return true
            }
        }
        return false
    }

    private fun createNode(x: Int, y: Int, node: Node): Node? {
        if (x in 0 until count
                && y in 0 until count
        ) {
            var result: Node? = null
            if (map[x][y] == FLAG_DEFAULT) {
                map[x][y] = FLAG_WAITING_CHECK
                result = Node(x, y, node)
            } else if (map[x][y] == FLAG_END) {
                result = Node(x, y, node)
            }
            return result
        }
        return null
    }

    override fun start() {
        currNode = null
        nodeQueue.clear()
        //将起点加入检索队列
        nodeQueue.offer(startNode)
        super.start()
    }

    override fun end() {
        super.end()
        //清除检索过程产生的标记
        for (x in 0 until count) {
            for (y in 0 until count) {
                if (map[x][y] == FLAG_WAITING_CHECK || map[x][y] > 0) {
                    map[x][y] = 0
                }
            }
        }
        //清空检索队列与当前节点
        nodeQueue.clear()
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

    private fun getCheckedColor(step: Int): Int {
        var rate = 0F
        currNode?.let {
            if (it.step > 0) {
                rate = step * 1F / it.step
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


}