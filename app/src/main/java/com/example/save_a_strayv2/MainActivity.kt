package com.example.save_a_strayv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.save_a_strayv2.ui.RegistrationScreen
import com.example.save_a_strayv2.ui.theme.SaveaStrayV2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaveaStrayV2Theme {
                // Entry point — swap for a NavHost when navigation is set up
                RegistrationScreen(
                    onNavigateToLogin = {
                        // TODO: navigate to LoginScreen
                    }
                )
            }
        }
    }
}