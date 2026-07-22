package com.fadhil.taba.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import android.app.Activity
import com.fadhil.taba.ui.theme.GreenPrimary

@Composable
fun TabaHeader(
    title: String,
    onBack: (() -> Unit)? = null,
    subtitle: String? = null,
    trailingAction: (@Composable () -> Unit)? = null
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(GreenPrimary)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            val path1 = Path().apply {
                moveTo(w * 0.7f, 0f)
                lineTo(w, 0f)
                lineTo(w, h)
                lineTo(w * 0.55f, h)
                close()
            }
            drawPath(path = path1, color = Color.White.copy(alpha = 0.05f))

            val path2 = Path().apply {
                moveTo(w * 0.85f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.6f)
                lineTo(w * 0.75f, h * 0.6f)
                close()
            }
            drawPath(path = path2, color = Color.White.copy(alpha = 0.08f))
            
            val path3 = Path().apply {
                moveTo(w, h * 0.3f)
                lineTo(w, h)
                lineTo(w * 0.8f, h)
                close()
            }
            drawPath(path = path3, color = Color.White.copy(alpha = 0.03f))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 4.dp)
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.95f), // increased alpha
                            fontSize = 14.sp, // increased font size
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (trailingAction != null) {
                    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                        trailingAction()
                    }
                }
            }
        }
    }
}
