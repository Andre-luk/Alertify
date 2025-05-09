package com.example.alertify.screens

import android.content.Context
import android.graphics.Canvas
import androidx.compose.ui.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight

class DrawingView(context: Context) : View(context) {
    private val paths = mutableListOf<Path>()
    private val currentPath = Path()
    private val paint = Paint().apply {
//        color = Color.BLACK
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

@Composable
fun DrawingScreen() {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }

    // Animation d’échelle
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "scaleAnim"
    )

    // Élévation dynamique
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 8f,
        animationSpec = tween(150),
        label = "elevationAnim"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { DrawingView(it) })

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFB71C1C),
                shadowElevation = elevation.dp,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                isPressed = true
                                Toast.makeText(context, "Clic normal détecté", Toast.LENGTH_SHORT).show()
                                isPressed = false
                            },
                            onDoubleTap = {
                                isPressed = true
                                Toast.makeText(context, "Double clic détecté", Toast.LENGTH_SHORT).show()
                                isPressed = false
                            },
                            onLongPress = {
                                isPressed = true
                                Toast.makeText(context, "Clic long détecté", Toast.LENGTH_SHORT).show()
                                isPressed = false
                            }
                        )
                    }
            ) {
                Text(
                    text = "Interaction",
                    modifier = Modifier
                        .padding(horizontal = 32.dp, vertical = 14.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
