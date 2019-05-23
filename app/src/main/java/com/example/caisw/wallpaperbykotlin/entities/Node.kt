package com.example.caisw.wallpaperbykotlin.entities

class Node {
    val x: Int
    val y: Int
    val parent: Node?
    var step: Int = 0

    constructor(x: Int, y: Int, parent: Node?) {
        this.x = x
        this.y = y
        this.parent = parent
        if (parent != null) {
            step = parent.step + 1
        }
    }
}