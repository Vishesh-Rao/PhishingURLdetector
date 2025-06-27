package com.vr13.phishingurldetector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vr13.phishingurldetector.mainscreen.HomeScreen
import com.vr13.phishingurldetector.navigation.AppNavHost
import com.vr13.phishingurldetector.ui.theme.PhishingURLDetectorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhishingURLDetectorTheme {
                AppNavHost()
            }
        }
    }
}
