package com.sy.feature.timer.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.sy.odokcompose.core.designsystem.OdokColors

@Composable
fun LpPlayerView(
    lpRotationAngle: Animatable<Float, AnimationVector1D>,
    armAnimationProgress: Animatable<Float, AnimationVector1D>,
    volumeAnimationProgress: Animatable<Float, AnimationVector1D>,
    powerButtonRotation: Animatable<Float, AnimationVector1D>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val center = Offset(canvasWidth / 2, canvasHeight / 2)

            rotate(lpRotationAngle.value, center) {
                drawCircle(
                    color = Color(0xFF353935),
                    radius = canvasHeight * 0.38f,
                    center = center
                )
                drawCircle(
                    color = Color.Black,
                    radius = canvasHeight * 0.18f,
                    center = center
                )
                drawCircle(
                    color = Color(0xFFE53935),
                    radius = canvasHeight * 0.11f,
                    center = center
                )
                drawCircle(
                    color = Color.Black,
                    radius = canvasHeight * 0.02f,
                    center = center
                )
                drawArc(
                    color = Color(0xFF90A4AE),
                    startAngle = 200f,
                    sweepAngle = 40f,
                    useCenter = false,
                    topLeft = Offset(
                        center.x - canvasHeight * 0.32f,
                        center.y - canvasHeight * 0.32f
                    ),
                    size = Size(canvasHeight * 0.64f, canvasHeight * 0.64f),
                    style = Stroke(width = 6f)
                )
                drawArc(
                    color = Color(0xFF90A4AE),
                    startAngle = 30f,
                    sweepAngle = 30f,
                    useCenter = false,
                    topLeft = Offset(
                        center.x - canvasHeight * 0.22f,
                        center.y - canvasHeight * 0.22f
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        canvasHeight * 0.44f,
                        canvasHeight * 0.44f
                    ),
                    style = Stroke(width = 4f)
                )
            }
            val armStartOff = Offset(canvasWidth * 0.85f, canvasHeight * 0.85f)
            val armStartOn = Offset(center.x + canvasHeight * 0.23f, center.y + canvasHeight * 0.1f)
            val armEnd = Offset(canvasWidth * 0.85f, canvasHeight * 0.18f)

            val interpolatedArmStart = androidx.compose.ui.geometry.lerp(
                armStartOff,
                armStartOn,
                armAnimationProgress.value
            )

            drawCircle(
                color = OdokColors.Orange,
                radius = 40f,
                center = armEnd
            )
            drawLine(
                color = Color.Black,
                start = interpolatedArmStart,
                end = armEnd,
                strokeWidth = 12f
            )

            drawRect(
                color = OdokColors.Orange,
                topLeft = Offset(interpolatedArmStart.x - 12f, interpolatedArmStart.y - 12f),
                size = androidx.compose.ui.geometry.Size(24f, 24f)
            )

            val ctrlX = canvasWidth * 0.92f
            val volumeOnTopLeft = Offset(ctrlX - 18f, canvasHeight * 0.45f)
            val volumeOffTopLeft = Offset(ctrlX - 18f, canvasHeight * 0.65f)

            val interpolatedVolumeTopLeft = androidx.compose.ui.geometry.lerp(
                volumeOffTopLeft,
                volumeOnTopLeft,
                volumeAnimationProgress.value
            )

            drawLine(
                color = Color.Black,
                start = Offset(ctrlX, canvasHeight * 0.4f),
                end = Offset(ctrlX, canvasHeight * 0.7f),
                strokeWidth = 12f
            )
            drawRect(
                color = OdokColors.Orange,
                topLeft = interpolatedVolumeTopLeft,
                size = androidx.compose.ui.geometry.Size(36f, 18f)
            )

            val powerX = canvasWidth * 0.80f
            val powerButtonCenter = Offset(powerX, canvasHeight * 0.8f)

            drawCircle(
                color = OdokColors.Orange,
                radius = 20f,
                center = powerButtonCenter
            )
            rotate(degrees = powerButtonRotation.value, pivot = powerButtonCenter) {
                drawLine(
                    color = Color.Black,
                    start = Offset(powerButtonCenter.x - 14f, powerButtonCenter.y),
                    end = Offset(powerButtonCenter.x + 14f, powerButtonCenter.y),
                    strokeWidth = 4f
                )
            }
        }
    }
}