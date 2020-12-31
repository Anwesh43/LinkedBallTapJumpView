package com.example.balltapjumpview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.app.Activity
import android.content.Context

val backColor : Int = Color.parseColor("#BDBDBD")
val colors : Array<Int> = arrayOf(
    "#F44336",
    "#009688",
    "#FF9800",
    "#795548",
    "#03A9F4"
).map {
   Color.parseColor(it)
}.toTypedArray()
val delay : Long = 20
val sizeFactor : Float = 5.9f
val strokeFactor : Float = 90f
val parts : Int = 3

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float  = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawBallTapJumper(scale : Float, w : Float, h : Float, x : Float, y : Float, paint : Paint) {
    val sc1 : Float = scale.divideScale(0, parts)
    val sc2 : Float = scale.divideScale(1, parts)
    val sc3 : Float = scale.divideScale(2, parts)
    val r : Float = Math.min(w, h) / sizeFactor
    save()
    drawCircle(r + (x - r) * sc2, y + (h - y + r) * sc3, r * sc1, paint)
    drawLine(0f, y, x * (sc2 - sc3), y, paint)
    restore()
}

fun Canvas.drawBTJNode(i : Int, scale : Float, x : Float, y : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i % colors.size]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawBallTapJumper(scale, w, h, x, y, paint)
}
