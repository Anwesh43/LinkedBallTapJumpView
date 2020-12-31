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
val parts : Int = 4

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float  = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()
