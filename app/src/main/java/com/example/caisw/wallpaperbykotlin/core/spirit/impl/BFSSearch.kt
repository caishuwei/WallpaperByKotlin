package com.example.caisw.wallpaperbykotlin.core.spirit.impl

import android.graphics.Canvas
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import com.example.caisw.wallpaperbykotlin.entities.Node
import java.util.*

/**
 * 广度优先搜索
 * <br/>
 * 从出发点开始向外检索，由近及远，一层一层搜索直到到达目标位置，
 * 会比较耗性能，但找到的路径一定是最优的
 */
class BFSSearch : GridMap(60) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val nodeQueue = LinkedList<Node>()
    private var currNode: Node? = null
    private val updateTask = object : Runnable {
        override fun run() {
            if (!nodeQueue.isEmpty()) {
                val node = nodeQueue.poll()
                currNode = node
                if (map[node.x][node.y] == FLAG_END) {
                    //已经找到终点
                    nodeQueue.clear()
                    return
                } else {
                    //不是终点
                    if (map[node.x][node.y] != FLAG_START) {
                        //标记这个点已经检索过
                        map[node.x][node.y] = FLAG_CHECKED
                    }
                    //将临近点加入队列(左上右下)
                    addNode(node.x - 1, node.y, node)
                    addNode(node.x, node.y - 1, node)
                    addNode(node.x + 1, node.y, node)
                    addNode(node.x, node.y + 1, node)
                }
                if (!nodeQueue.isEmpty()) {
                    mainHandler.postDelayed(this, 50)
                } else {
                    currNode = null
                }
            }
        }

        private fun addNode(x: Int, y: Int, node: Node) {
            if (x in 0 until count
                    && y in 0 until count
                    && map[x][y] == 0) {
                map[x][y] = FLAG_WAITING_CHECK
                nodeQueue.offer(Node(x, y, node))
            }
        }
    }

    private lateinit var startNode: Node
    init {
        randomStart()
        randomEnd()
    }

    fun destroySearch() {
        mainHandler.removeCallbacks(updateTask)
        for (x in 0 until count) {
            for (y in 0 until count) {
                if (map[x][y] == FLAG_WAITING_CHECK || map[x][y] == FLAG_CHECKED) {
                    map[x][y] = 0
                }
            }
        }
        nodeQueue.clear()
        currNode = null
    }

    fun startSearch() {
        //将检索点加入队列
        nodeQueue.offer(startNode)
        mainHandler.post(updateTask)
    }

    private fun randomEnd() {
        val x = (Math.random() * count).toInt()
        val y = (Math.random() * count).toInt()
        if (map[x][y] == 0) {
            map[x][y] = FLAG_END
        } else {
            randomEnd()
        }
    }


    private fun randomStart() {
        val x = (Math.random() * count).toInt()
        val y = (Math.random() * count).toInt()
        if (map[x][y] == 0) {
            map[x][y] = FLAG_START
            startNode = Node(x, y, null)
        } else {
            randomStart()
        }
    }

    private val checkedColor = Color.parseColor("#80FF0000")

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        for (x in 0 until count) {
            for (y in 0 until count) {
                when (map[x][y]) {
                    FLAG_OBSTACLE -> drawRect(x, y, canvas, obstaclePaint)
                    FLAG_START -> {
                        commonPaint.color = Color.GREEN
                        drawRect(x, y, canvas, commonPaint)
                    }
                    FLAG_END -> {
                        commonPaint.color = Color.YELLOW
                        drawRect(x, y, canvas, commonPaint)
                    }
                    FLAG_WAITING_CHECK -> {
                        commonPaint.color = Color.GRAY
                        drawRect(x, y, canvas, commonPaint)
                    }
                    FLAG_CHECKED -> {
                        commonPaint.color = checkedColor
                        drawRect(x, y, canvas, commonPaint)
                    }
                }
            }
        }
        //绘制当前检索的路径
        var node = currNode
        while (node != null) {
            commonPaint.color = Color.RED
            drawRect(node.x, node.y, canvas, commonPaint)
            node = node.parent
        }
    }


}