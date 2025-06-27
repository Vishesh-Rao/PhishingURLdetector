package com.vr13.phishingurldetector.mainscreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vr13.phishingurldetector.R
import com.vr13.phishingurldetector.viewmodel.PhishingViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: PhishingViewModel = viewModel()
) {
    var url by remember { mutableStateOf("") }
    val result = viewModel.result
    val isLoading = viewModel.isLoading
    val engineResults = viewModel.engineResults

    val rawProgress = if (isLoading) 0.3f else 1.0f
    val progress by animateFloatAsState(targetValue = rawProgress, label = "")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A0F1C),
                        Color(0xFF121B2E)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PhishGuard",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(60.dp))

            TextField(
                value = url,
                onValueChange = { url = it },
                placeholder = {
                    Text(
                        "Enter URL to check",
                        color = colorResource(com.vr13.phishingurldetector.R.color.Light_Grey),
                        fontSize = 16.sp
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(com.vr13.phishingurldetector.R.drawable.link),
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(25.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .background(Color(0x221F2A3F), RoundedCornerShape(10.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorResource(com.vr13.phishingurldetector.R.color.Dark_Grey),
                    unfocusedContainerColor = colorResource(com.vr13.phishingurldetector.R.color.Dark_Grey),
                    focusedTextColor = colorResource(com.vr13.phishingurldetector.R.color.Light_Grey),
                    unfocusedTextColor = colorResource(com.vr13.phishingurldetector.R.color.Light_Grey)
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (url.isNotBlank()) viewModel.checkUrl(url)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(com.vr13.phishingurldetector.R.color.blue)
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(55.dp)
            ) {
                Text(
                    text = if (isLoading) "Checking..." else "Check URL",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .background(Color(0x331F2A3F), RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = result,
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            color = colorResource(com.vr13.phishingurldetector.R.color.blue),
                            trackColor = colorResource(com.vr13.phishingurldetector.R.color.Dark_Grey)
                        )
                    }

                    if (engineResults.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Engine Results:",
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        engineResults.forEach { (engine, category) ->
                            Text(
                                "$engine: $category",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

