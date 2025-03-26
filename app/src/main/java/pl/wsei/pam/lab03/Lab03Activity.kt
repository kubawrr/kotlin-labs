package pl.wsei.pam.lab03

import android.os.Bundle
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import java.util.Timer
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {
    private lateinit var mBoard: GridLayout

    private lateinit var mBoardModel: MemoryBoardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab03)

        mBoard = findViewById(R.id.grid_layout)

        val size = intent.getIntArrayExtra("size") ?: intArrayOf(3, 3)
        val rows = size[0]
        val columns = size[1]

        mBoard.rowCount = rows
        mBoard.columnCount = columns

        Toast.makeText(this, "$rows $columns", Toast.LENGTH_SHORT).show()

        if (savedInstanceState != null) {
            val savedState = savedInstanceState.getIntArray("gameState")
            mBoardModel = MemoryBoardView(mBoard, columns, rows)
            savedState?.let {
                mBoardModel.setState(it)
            }
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
                        }
                    }
                    GameStates.NoMatch -> {
                        e.tiles.forEach { tile ->
                            tile.button.setImageResource(tile.tileResource)
                            tile.revealed = true
                        }

                        Timer().schedule(2000) {
                            runOnUiThread {
                                e.tiles.forEach { tile ->
                                    tile.button.setImageResource(R.drawable.deck)
                                    tile.revealed = false
                                }
                            }
                        }
                    }
                    GameStates.Finished -> {
                        runOnUiThread {
                            Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val gameState = (mBoardModel as MemoryBoardView).getState()
        outState.putIntArray("gameState", gameState)
    }

}
