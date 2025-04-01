package com.example.alertifyapp.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.alertifyapp.components.DrawingView

class DrawingScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrawingView(this)
        }
    }
}


class DrawingView(context: Context) : View(context) {
    private val paths = mutableListOf<Path>()
    private val currentPath = Path()
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath.reset()
                currentPath.moveTo(x, y)
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(x, y)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                paths.add(Path(currentPath))
                currentPath.reset()
                invalidate()
                return true
            }
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paths.forEach { canvas.drawPath(it, paint) }
        canvas.drawPath(currentPath, paint)
    }
}