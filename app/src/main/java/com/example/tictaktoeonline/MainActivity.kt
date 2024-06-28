package com.example.tictaktoeonline

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictaktoeonline.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var musicPlaying = true // Initially music is playing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Start music service by default
        startService(Intent(this, MusicService::class.java))

        binding.playOfflineBtn.setOnClickListener {
            createOfflineGame()
        }

        binding.createOnlineGameBtn.setOnClickListener {
            createOnlineGame()
        }

        binding.joinOnlineBtn.setOnClickListener {
            joinOnlineGame()
        }

        // Set initial music toggle image
        updateMusicToggleImage()
    }

    private fun updateMusicToggleImage() {
        val musicToggle = findViewById<ImageView>(R.id.music_toggle)
        musicToggle.setImageResource(if (musicPlaying) R.drawable.ic_music_on else R.drawable.ic_music_off)
    }

    fun toggleMusicStatus(view: View) {
        musicPlaying = !musicPlaying // Toggle music status
        updateMusicToggleImage()

        val intent = Intent(this, MusicService::class.java)
        if (musicPlaying) {
            startService(intent) // Start music service if it's not running
        } else {
            stopService(intent) // Stop music service if it's running
        }
    }

    fun createOnlineGame() {
        GameData.myID = "X"
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.CREATED,
                gameId = Random.nextInt(1000..9999).toString()
            )
        )
        startGame()
    }

    fun joinOnlineGame() {
        val gameId = binding.gameIdInput.text.toString()
        if (gameId.isEmpty()) {
            binding.gameIdInput.error = "Please Enter Game ID"
            return
        }

        GameData.myID = "O"
        Firebase.firestore.collection("games")
            .document(gameId)
            .get()
            .addOnSuccessListener {
                val model = it?.toObject(GameModel::class.java)
                if (model == null) {
                    binding.gameIdInput.error = "Please Enter Valid Game ID"
                } else {
                    model.gameStatus = GameStatus.JOINED
                    GameData.saveGameModel(model)
                    startGame()
                }
            }
    }

    fun createOfflineGame() {
        GameData.saveGameModel(
            GameModel(
                gameStatus = GameStatus.JOINED
            )
        )
        startGame()
    }

    fun startGame() {
        startActivity(Intent(this, GameActivity::class.java))
    }
}
