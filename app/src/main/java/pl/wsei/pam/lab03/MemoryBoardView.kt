package pl.wsei.pam.lab03

import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import androidx.gridlayout.widget.GridLayout
import pl.wsei.pam.lab01.R
import java.util.Stack

class MemoryBoardView(
    private val gridLayout: android.widget.GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private val icons: List<Int> = listOf(
        R.drawable.baseline_rocket_24,
    )
    private var gameState: IntArray = IntArray(cols * rows) { -1 }

    fun getState(): IntArray {
        return gameState
    }

    fun setState(state: IntArray) {
        gameState = state
        for (i in gameState.indices) {
            val button = tiles.entries.elementAt(i).value.button
            if (gameState[i] == -1) {
                button.setImageResource(R.drawable.deck)
            } else {
                button.setImageResource(gameState[i])
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
        val tile = tiles[v.tag]
        matchedPair.push(tile)
        val matchResult = logic.process {
            tile?.tileResource ?: -1
        }
        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))
        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }
}
