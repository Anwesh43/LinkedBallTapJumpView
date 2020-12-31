package com.example.balltapjumpview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.app.Activity
import android.content.Context
import java.util.concurrent.ConcurrentLinkedQueue

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
val scGap : Float = 0.02f / parts


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

class BallTapJumpView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)
    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap(event.x, event.y)
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BTJNode(
        private var i : Int,
        private var x : Float,
        private var y : Float,
        private val state : State = State()
    ) {

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBTJNode(i, state.scale, x, y, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }
    }

    data class BallTapJumpList(var i : Int) {

        private var balls : ConcurrentLinkedQueue<BTJNode> = ConcurrentLinkedQueue()

        fun draw(canvas : Canvas, paint : Paint) {
            balls.forEach {
                it.draw(canvas, paint)
            }
        }

        fun update(cb : (Float) -> Unit) {
            balls.forEach {
                it.update {
                    balls.remove(0)
                    if (balls.size == 0) {
                        cb(it)
                    }
                }
            }
        }

        fun startUpdating(x : Float, y : Float, cb : () -> Unit) {
            balls.add(BTJNode(i++, x, y))
            if (balls.size == 1) {
                cb()
            }
        }
    }

    data class Renderer(var view : BallTapJumpView) {
        private val animator : Animator = Animator(view)
        private val ballTapJumpList :  BallTapJumpList = BallTapJumpList(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            animator.animate {
                ballTapJumpList.update {
                    animator.stop()
                }
            }
        }

        fun handleTap(x : Float, y : Float) {
            ballTapJumpList.startUpdating(x, y) {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : BallTapJumpView {
            val view : BallTapJumpView = BallTapJumpView(activity)
            activity.setContentView(view)
            return view
        }
    }
}