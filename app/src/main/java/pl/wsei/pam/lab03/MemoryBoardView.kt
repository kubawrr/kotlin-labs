package pl.wsei.pam.lab03

import android.animation.ObjectAnimator
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import pl.wsei.pam.lab01.R
import java.util.Stack

class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private val icons: List<Int> = listOf(
        R.drawable.baseline_rocket_24,
        R.drawable.baseline_star_24,
        R.drawable.baseline_heart_24,
        R.drawable.baseline_moon_24,
        R.drawable.baseline_plane_24,
        R.drawable.baseline_wifi_24,
        R.drawable.baseline_bluetooth_24,
        R.drawable.baseline_bicycle_24,
        R.drawable.baseline_music_note_24,
        R.drawable.baseline_sun_24,
        R.drawable.baseline_cloud_24,
        R.drawable.baseline_camera_24,
        R.drawable.baseline_gamepad_24,
        R.drawable.baseline_directions_24,
        R.drawable.baseline_trophy_24,
        R.drawable.baseline_shield_24,
        R.drawable.baseline_flash_24,
        R.drawable.baseline_gem_24
    )

    private var gameState: IntArray = IntArray(cols * rows) { -1 }

    fun getState(): IntArray {
        return gameState
    }

    fun setState(state: IntArray) {
        gameState = state
        for (i in gameState.indices) {
            val key = tiles.keys.elementAt(i)
            val tile = tiles[key]
            if (tile != null) {
                if (gameState[i] == -1) {
                    tile.button.setImageResource(deckResource)
                    tile.revealed = false
                } else {
                    tile.button.setImageResource(gameState[i])
                    tile.revealed = true
                    if (tile.revealed) {
                        val fadeOutAnimator = ObjectAnimator.ofFloat(tile.button, "alpha", 1f, 0f)
                        fadeOutAnimator.duration = 500
                        fadeOutAnimator.start()
                    }
                }
            }
        }
    }

    init {
        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().also {
            it.addAll(icons.subList(0, cols * rows / 2))
            it.addAll(icons.subList(0, cols * rows / 2))
            it.shuffle()
        }

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val button = ImageButton(gridLayout.context).apply {
                    tag = "${row}x${col}"
                    val layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        setGravity(Gravity.CENTER)
                        columnSpec = GridLayout.spec(col, 1, 1f)
                        rowSpec = GridLayout.spec(row, 1, 1f)
                    }
                    this.layoutParams = layoutParams
                    setImageResource(R.drawable.baseline_audiotrack_24)
                }

                val tileResource = shuffledIcons.removeAt(0)
                val tile = Tile(button, tileResource, R.drawable.deck)
                tiles[button.tag.toString()] = tile

                gridLayout.addView(button)

                button.setOnClickListener {
                    onClickTile(it)
                }
            }
        }
    }

    private val deckResource: Int = R.drawable.deck
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { (e) -> }
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    private fun onClickTile(v: View) {
        val tile = tiles[v.tag] ?: return

        val index = getTileIndex(v.tag.toString())
        if (index != -1 && gameState[index] == -1) {
            gameState[index] = tile.tileResource
            tile.button.setImageResource(tile.tileResource)
        }

        matchedPair.push(tile)
        val matchResult = logic.process {
            tile.tileResource ?: -1
        }

        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))
        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }
    }

    private fun getTileIndex(tag: String): Int {
        val rowCol = tag.split("x")
        val row = rowCol[0].toInt()
        val col = rowCol[1].toInt()
        return row * cols + col
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }
}
