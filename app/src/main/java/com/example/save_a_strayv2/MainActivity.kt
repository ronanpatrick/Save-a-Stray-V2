package com.example.save_a_strayv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.save_a_strayv2.ui.MainScreen
import com.example.save_a_strayv2.ui.theme.SaveaStrayV2Theme

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.save_a_strayv2.repository.AuthRepository
import com.example.save_a_strayv2.ui.navigation.RootNavGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaveaStrayV2Theme {
                RootNavGraph(authRepository = authRepository)
            }
        }
    }
}