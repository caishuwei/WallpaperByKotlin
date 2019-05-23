package com.example.caisw.wallpaperbykotlin.core.spirit.search

import android.graphics.Canvas
import com.example.caisw.wallpaperbykotlin.entities.Node
import java.util.*

/**
 * 深度优先搜索
 * <br/>
 * 深度优先与广度优先的区别在于，他不是先遍历最近的节点然后再遍历下一层的节点
 * 而是最近的节点选一个，查找他的下一层节点，若下一层有节点则选一个继续下一层
 * ，若没有则看上一次的其它节点。
 * 非常符合先进后出，所以我们用栈实现
 * 特点：先找最深的节点，找出来的路径通常不是最短的，而且只能在有限区域里面使用
 *
 * 深度搜索一开始被应用于走迷宫，往一条分支走到底，发现没路了，就切换别的分支继续走
 */
class DepthFirstSearch : SearchBase(60) {
    private val nodeStack = Stack<Node>()
    private var currNode: Node? = null
    //检索深度限制，无障碍的情况下最远的两个点距离是count+count，这里乘以2作为查询路径的最大值
//    private val depthLimit = (count + count) * 2
    private val depthLimit = count * count

    override fun nextStep(): Boolean {
        super.nextStep()
        if (!nodeStack.isEmpty()) {
            val node = nodeStack.pop()
            currNode = node
            if (map[node.x][node.y] == FLAG_END) {
                //已经找到终点
                nodeStack.clear()
                return false
            } else {
                //不是终点
                if (map[node.x][node.y] != FLAG_START) {
                    //标记这个点已经检索过
                    map[node.x][node.y] = node.step
                }
                //左
                var nearNode = createNode(node.x - 1, node.y, node)
                if (nearNode != null) {
                    when (map[nearNode.x][nearNode.y]) {
                        FLAG_END -> {
                            currNode = nearNode
                            nodeStack.clear()
                            return false
                        }
                        FLAG_WAITING_CHECK -> {
                            nodeStack.push(nearNode)
                        }
                    }
                }
                //上
                nearNode = createNode(node.x, node.y - 1, node)
                if (nearNode != null) {
                    when (map[nearNode.x][nearNode.y]) {
                        FLAG_END -> {
                            currNode = nearNode
                            nodeStack.clear()
                            return false
                        }
                        FLAG_WAITING_CHECK -> {
                            nodeStack.push(nearNode)
                        }
                    }
                }
                //右
                nearNode = createNode(node.x + 1, node.y, node)
                if (nearNode != null) {
                    when (map[nearNode.x][nearNode.y]) {
                        FLAG_END -> {
                            currNode = nearNode
                            nodeStack.clear()
                            return false
                        }
                        FLAG_WAITING_CHECK -> {
                            nodeStack.push(nearNode)
                        }
                    }
                }
                //下
                nearNode = createNode(node.x, node.y + 1, node)
                if (nearNode != null) {
                    when (map[nearNode.x][nearNode.y]) {
                        FLAG_END -> {
                            currNode = nearNode
                            nodeStack.clear()
                            return false
                        }
                        FLAG_WAITING_CHECK -> {
                            nodeStack.push(nearNode)
                        }
                    }
                }
            }
            if (nodeStack.isEmpty()) {
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
        //深度已经达到最大值，则不继续往下探索
        if (node.step >= depthLimit) {
            return null
        }
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
        nodeStack.clear()
        //将起点加入检索队列
        nodeStack.push(startNode)
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
        nodeStack.clear()
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
        return alpha shl 24 or (COLOR_PATH and 0x00ffffff)
    }

}