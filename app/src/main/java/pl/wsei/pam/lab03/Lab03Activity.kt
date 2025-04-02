package pl.wsei.pam.lab03

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab02.Lab02Activity
import java.util.Timer
import kotlin.concurrent.schedule

class Lab03Activity() : AppCompatActivity() {
    private lateinit var mBoard: GridLayout
    private lateinit var mBoardModel: MemoryBoardView

    lateinit var completionPlayer: MediaPlayer
    lateinit var negativePLayer: MediaPlayer

    var isSound = true;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab03)

        val size = intent.getIntArrayExtra("size") ?: intArrayOf(3, 3)

        val rows = size[0]
        val columns = size[1]

        onResume()

        mBoard = findViewById(R.id.grid_layout)

        mBoard.rowCount = rows
        mBoard.columnCount = columns

        val boardKey = "board_${rows}_${columns}"

        val savedState = loadGameState(boardKey)

        if (savedState != null) {
            mBoardModel = MemoryBoardView(mBoard, columns, rows)
            mBoardModel.setState(savedState)
        } else {
            mBoardModel = MemoryBoardView(mBoard, columns, rows)
        }

        mBoardModel.setOnGameChangeListener { e ->
            run {
                when (e.state) {
                    GameStates.Matching -> {
                        e.tiles.forEach { tile ->
                            tile.button.setImageResource(tile.tileResource)
                            tile.revealed = true
                        }
                    }

                    GameStates.Match -> {
                        e.tiles.forEach { tile ->
                            tile.button.setImageResource(tile.tileResource)
                            tile.revealed = true

                            val rotateAnimator = ObjectAnimator.ofFloat(tile.button, "rotation", 0f, 360f) // Вращение на 360 градусов
                            val scaleAnimator = ObjectAnimator.ofFloat(tile.button, "scaleX", 1f, 1.5f) // Увеличение по оси X
                            val scaleYAnimator = ObjectAnimator.ofFloat(tile.button, "scaleY", 1f, 1.5f) // Увеличение по оси Y
                            val fadeOutAnimator = ObjectAnimator.ofFloat(tile.button, "alpha", 1f, 0f) // Исчезновение

                            val animatorSet = AnimatorSet()
                            animatorSet.playTogether(rotateAnimator, scaleAnimator, scaleYAnimator, fadeOutAnimator)
                            animatorSet.duration = 500

                            animatorSet.start()

                        }

                        saveGameState(boardKey)
                    }

                    GameStates.NoMatch -> {
                        e.tiles.forEach { tile ->
                            tile.button.isClickable = false
                            tile.button.setImageResource(tile.tileResource)
                            tile.revealed = true

                            val rotateLeft = ObjectAnimator.ofFloat(tile.button, "rotation", 0f, -15f)
                            val rotateLeft2 = ObjectAnimator.ofFloat(tile.button, "rotation", 0f, -15f)
                            val rotateRight = ObjectAnimator.ofFloat(tile.button, "rotation", -15f, 15f)
                            val rotateRight2 = ObjectAnimator.ofFloat(tile.button, "rotation", -15f, 15f)
                            val rotateBack = ObjectAnimator.ofFloat(tile.button, "rotation", 15f, 0f)
                            val rotateBack2 = ObjectAnimator.ofFloat(tile.button, "rotation", 15f, 0f)

                            val animatorSet = AnimatorSet()
                            animatorSet.playSequentially(rotateLeft, rotateRight, rotateBack, rotateLeft2, rotateRight2, rotateBack2)
                            animatorSet.duration = 300

                            animatorSet.start()
                        }

                        Timer().schedule(2000) {
                            runOnUiThread {
                                e.tiles.forEach { tile ->
                                    tile.button.setImageResource(R.drawable.deck)
                                    tile.revealed = false
                                    tile.button.isClickable = true
                                }
                            }
                        }
                    }

                    GameStates.Finished -> {
                        e.tiles.forEach { tile ->
                            tile.button.setImageResource(tile.tileResource)
                            tile.revealed = true
                            tile.button.setBackgroundColor(Color.GREEN)
                            runOnUiThread {
                                Toast.makeText(this, "Game finished you win", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this, Lab02Activity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean  {
        val inflater: MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.board_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.board_activity_sound -> {
                val currentIcon = item.icon?.constantState
                val soundOnIconState = getResources().getDrawable(R.drawable.baseline_volume_up_24, theme).constantState

                if (currentIcon == soundOnIconState) {
                    Toast.makeText(this, "Sound turned off", Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.baseline_volume_off_24)
                    isSound = false
                } else {
                    Toast.makeText(this, "Sound turned on", Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.baseline_volume_up_24)
                    isSound = true
                }
            }
        }
        return true
    }

    private fun loadGameState(boardKey: String): IntArray? {
        val sharedPreferences = getSharedPreferences("GameStates", MODE_PRIVATE)
        val gameStateString = sharedPreferences.getString(boardKey, null)

        if (gameStateString != null) {
            Log.d("GameState", "Loaded state for key $boardKey: $gameStateString")
            return gameStateString.split(",").map { it.toInt() }.toIntArray()
        } else {
            Log.d("GameState", "No state found for key $boardKey")
            return null
        }
    }

    private fun saveGameState(boardKey: String) {
        val gameState = mBoardModel.getState()

        val sharedPreferences = getSharedPreferences("GameStates", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gameStateString = gameState.joinToString(",") { it.toString() }

        Log.d("GameState", "Saving state for key $boardKey: $gameStateString")

        editor.putString(boardKey, gameStateString)
        editor.apply()

        Log.d("GameState", "State saved for key $boardKey")
    }


    private fun saveGameState() {
        val gameState = mBoardModel.getState()
        val outState = Bundle()
        outState.putIntArray("gameState", gameState)
        onSaveInstanceState(outState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val gameState = (mBoardModel as MemoryBoardView).getState()
        outState.putIntArray("gameState", gameState)
    }

    override fun onResume() {
        super.onResume()

        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        negativePLayer = MediaPlayer.create(applicationContext, R.raw.negative_guitar)

        if (isSound) {
            completionPlayer.start() // This is safe now since the player is initialized
        }
    }

    override fun onPause() {
        super.onPause()

        completionPlayer.release()
        negativePLayer.release()
    }
}
