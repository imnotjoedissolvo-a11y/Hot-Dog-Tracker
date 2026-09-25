package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import kotlinx.coroutines.launch

@Composable
fun HotDogScreen(
    viewModel: HotDogViewModel,
    modifier: Modifier = Modifier
) {
    val count by viewModel.count.collectAsStateWithLifecycle()
    val confettiTrigger by viewModel.confettiTrigger.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var showResetDialog by remember { mutableStateOf(false) }
    var showWidgetInfoDialog by remember { mutableStateOf(false) }
    var milestoneCelebrationText by remember { mutableStateOf<String?>(null) }

    // Hot dog bounce scale animation
    val hotDogScale = remember { Animatable(1f) }

    // Bounce helper
    val triggerBounce: (Float) -> Unit = { targetScale ->
        coroutineScope.launch {
            hotDogScale.snapTo(targetScale)
            hotDogScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    // Trigger celebration when milestone event happens
    LaunchedEffect(confettiTrigger) {
        if (confettiTrigger > 0) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            triggerBounce(1.22f)
            milestoneCelebrationText = "Milestone Reached! $confettiTrigger Hot Dogs!"
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top action bar: Widget Info button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showWidgetInfoDialog = true },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("widget_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Widget Info",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Main center content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 480.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Hot dog container with confetti explosion
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(240.dp)
                        .scale(hotDogScale.value)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            triggerBounce(1.12f)
                            viewModel.increment()
                        }
                        .testTag("hotdog_graphic")
                ) {
                    // Confetti explosion centered on hot dog
                    ConfettiExplosion(
                        triggerKey = confettiTrigger,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("confetti_canvas")
                    )

                    // Black & white hot dog emoji graphic
                    BlackWhiteHotDogEmoji(
                        sizeDp = 150.dp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Milestone badge banner
                AnimatedVisibility(
                    visible = milestoneCelebrationText != null,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = milestoneCelebrationText.orEmpty(),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                // Text: "You have eaten X hot dogs."
                val hotDogsWord = if (count == 1) "hot dog" else "hot dogs"
                Text(
                    text = "You have eaten $count $hotDogsWord.",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .testTag("count_text")
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Action Controls: Plus button and Reset button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Plus Button
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            triggerBounce(1.12f)
                            viewModel.increment()
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .size(72.dp)
                            .testTag("increment_button"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.increment),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // Reset Button
                    OutlinedButton(
                        onClick = {
                            if (count > 0) {
                                showResetDialog = true
                            } else {
                                viewModel.reset()
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.reset),
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.reset),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // Reset confirmation dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.reset_confirmation_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = stringResource(R.string.reset_confirmation_message))
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reset()
                        milestoneCelebrationText = null
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text(text = stringResource(R.string.reset))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetDialog = false },
                    modifier = Modifier.testTag("cancel_reset_button")
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    // Widget Information Dialog
    if (showWidgetInfoDialog) {
        AlertDialog(
            onDismissRequest = { showWidgetInfoDialog = false },
            title = {
                Text(
                    text = "Home Screen Widget",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "You can add the Hot Dog Counter widget to your home screen!\n\n" +
                            "1. Go to your phone's home screen.\n" +
                            "2. Touch and hold an empty space.\n" +
                            "3. Tap 'Widgets' and select 'Hot Dog Counter'.\n" +
                            "4. Drag it to your desired spot to track your count and add hot dogs instantly!"
                )
            },
            confirmButton = {
                Button(
                    onClick = { showWidgetInfoDialog = false },
                    modifier = Modifier.testTag("close_widget_info_button")
                ) {
                    Text(text = "Got it")
                }
            }
        )
    }
}
