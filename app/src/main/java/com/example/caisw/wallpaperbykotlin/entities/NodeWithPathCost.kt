package com.example.caisw.wallpaperbykotlin.entities

class NodeWithPathCost {

    var cost = 0f
    val x: Int
    val y: Int
    val parent: NodeWithPathCost?
    var step: Int = 0

    constructor(x: Int, y: Int, parent: NodeWithPathCost?) {
        this.x = x
        this.y = y
        this.parent = parent
        if (parent != null) {
            step = parent.step + 1
        }
    }

}