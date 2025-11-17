package org.example.app

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView


/**
 * PUBLIC_INTERFACE
 * MainActivity is the entry point of the Tic Tac Toe mobile app.
 * - Displays a modern, themed 3x3 board.
 * - Manages game state (turns, win/draw detection).
 * - Shows status text and provides a Reset/New Game control.
 *
 * UI Notes:
 * - Theme "Ocean Professional":
 *   primary #2563EB, secondary #F59E0B, error #EF4444,
 *   background #f9fafb, surface #ffffff, text #111827
 * - Rounded corners, subtle shadows, and smooth transitions are applied to buttons and containers.
 */
class MainActivity : Activity() {

    // Theme constants
    private val colorPrimary = Color.parseColor("#2563EB")
    private val colorSecondary = Color.parseColor("#F59E0B")
    private val colorError = Color.parseColor("#EF4444")
    private val colorBackground = Color.parseColor("#f9fafb")
    private val colorSurface = Color.parseColor("#ffffff")
    private val colorText = Color.parseColor("#111827")

    // Game state
    private var board: Array<String?> = Array(9) { null }
    private var currentPlayer: String = "X"
    private var winner: String? = null
    private var isDraw: Boolean = false

    // UI references
    private lateinit var statusText: TextView
    private lateinit var cells: List<Button>
    private lateinit var resetButton: Button
    private lateinit var titleText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Build the UI programmatically to ensure consistent theme and avoid extra resource complexity
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(colorBackground)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        titleText = TextView(this).apply {
            text = "Tic Tac Toe"
            setTextColor(colorText)
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setPadding(dp(8), dp(8), dp(8), dp(8))
            gravity = Gravity.CENTER
            // Subtle elevation via shadow layer simulation for text prominence
            setShadowLayer(2f, 0f, 2f, Color.argb(40, 0, 0, 0))
        }
        root.addView(titleText, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(16)
        })

        // Board container with surface color, rounded corners and subtle shadow (via background + elevation padding)
        val boardContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(colorSurface)
            // Simulate card-like surface with padding and rounded corners by background drawable replacement:
            background = RoundedRectDrawable(colorSurface, dp(12).toFloat())
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        val grid = GridLayout(this).apply {
            rowCount = 3
            columnCount = 3
            useDefaultMargins = false
            // Avoid alignment mode differences across API levels; rely on margins set per cell
            setPadding(dp(4), dp(4), dp(4), dp(4))
        }

        // Create 9 cell buttons
        val tempCells = mutableListOf<Button>()
        for (i in 0 until 9) {
            val cell = Button(this).apply {
                text = ""
                textSize = 28f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                setTextColor(colorText)
                isAllCaps = false
                minHeight = dp(64) // Accessibility: large enough touch target
                minWidth = dp(64)
                background = RoundedRectDrawable(Color.parseColor("#e5e7eb"), dp(10).toFloat()) // neutral cell background
                setPadding(dp(8), dp(8), dp(8), dp(8))
                stateListAnimator = null // flatter look, rely on subtle background/shadow
                setOnClickListener { onCellClicked(this, i) }
            }

            // Build explicit specs and pass them to the LayoutParams constructor to avoid overload ambiguity
            val row = i / 3
            val col = i % 3
            val params = GridLayout.LayoutParams(
                GridLayout.spec(row, 1),
                GridLayout.spec(col, 1)
            ).apply {
                // Explicit cell size; width 0 allows GridLayout to distribute evenly in container
                width = 0
                height = dp(90)

                // Optional center alignment within each grid cell
                setGravity(Gravity.CENTER)

                // Margins around each cell
                setMargins(dp(6), dp(6), dp(6), dp(6))
            }
            grid.addView(cell, params)
            tempCells.add(cell)
        }
        cells = tempCells

        boardContainer.addView(grid, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0
        ).apply {
            weight = 1f
        })

        root.addView(boardContainer, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0
        ).apply {
            weight = 1f
            bottomMargin = dp(16)
        })

        statusText = TextView(this).apply {
            textSize = 18f
            setTextColor(colorText)
            setPadding(dp(8), dp(8), dp(8), dp(8))
            gravity = Gravity.CENTER
        }
        root.addView(statusText, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(12)
        })

        resetButton = Button(this).apply {
            text = "New Game"
            textSize = 16f
            setTextColor(Color.WHITE)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAllCaps = false
            background = RoundedRectDrawable(colorPrimary, dp(14).toFloat())
            setPadding(dp(14), dp(14), dp(14), dp(14))
            setOnClickListener { resetGameWithAnimation() }
        }
        root.addView(resetButton, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        setContentView(root)

        // Initialize status
        updateStatus()
    }

    private fun onCellClicked(button: Button, index: Int) {
        if (winner != null || isDraw) return
        if (board[index] != null) return

        // Mark move
        board[index] = currentPlayer
        button.text = currentPlayer

        // Color accent for X / O to enhance readability and quick recognition
        val markColor = if (currentPlayer == "X") colorPrimary else colorSecondary
        button.setTextColor(markColor)
        // Smooth fade-in for feedback
        button.startAnimation(AlphaAnimation(0.3f, 1.0f).apply { duration = 150 })

        // Check state
        if (checkWin(currentPlayer)) {
            winner = currentPlayer
            updateStatus()
            disableAllCellsAfterGameOver()
            highlightWinningCells()
            return
        }

        if (board.all { it != null }) {
            isDraw = true
            updateStatus()
            disableAllCellsAfterGameOver()
            return
        }

        // Toggle player
        currentPlayer = if (currentPlayer == "X") "O" else "X"
        updateStatus()
    }

    private fun updateStatus() {
        when {
            winner != null -> {
                statusText.text = "Winner: $winner"
                statusText.setTextColor(colorPrimary)
            }
            isDraw -> {
                statusText.text = "Draw game"
                statusText.setTextColor(colorError)
            }
            else -> {
                statusText.text = "Current turn: $currentPlayer"
                statusText.setTextColor(colorText)
            }
        }
    }

    private fun disableAllCellsAfterGameOver() {
        cells.forEach { it.isEnabled = false }
    }

    private fun resetGameWithAnimation() {
        val anim = AlphaAnimation(0.0f, 1.0f).apply { duration = 180 }
        resetGame()
        (statusText.parent as? View)?.startAnimation(anim)
    }

    // PUBLIC_INTERFACE
    /**
     * Resets the game board, clears winner/draw, re-enables inputs, and sets current player to X.
     */
    private fun resetGame() {
        board = Array(9) { null }
        winner = null
        isDraw = false
        currentPlayer = "X"
        cells.forEach {
            it.text = ""
            it.isEnabled = true
            it.setTextColor(colorText)
            it.background = RoundedRectDrawable(Color.parseColor("#e5e7eb"), dp(10).toFloat())
        }
        updateStatus()
    }

    // PUBLIC_INTERFACE
    /**
     * Checks whether the given player ('X' or 'O') has a winning line.
     * @param player The current player symbol.
     * @return true if a win condition is met, false otherwise.
     */
    private fun checkWin(player: String): Boolean {
        val lines = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6),
        )
        for (line in lines) {
            if (board[line[0]] == player && board[line[1]] == player && board[line[2]] == player) {
                return true
            }
        }
        return false
    }

    private fun highlightWinningCells() {
        if (winner == null) return
        val player = winner!!
        val lines = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6),
        )
        val highlightColor = if (player == "X") colorPrimary else colorSecondary
        for (line in lines) {
            if (board[line[0]] == player && board[line[1]] == player && board[line[2]] == player) {
                line.forEach { idx ->
                    cells[idx].background = RoundedRectDrawable(applyAlpha(highlightColor, 0.15f), dp(10).toFloat())
                }
                // Emphasize status color
                statusText.setTextColor(highlightColor)
                return
            }
        }
    }

    private fun applyAlpha(color: Int, alpha: Float): Int {
        val a = (Color.alpha(color) * alpha).toInt()
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return Color.argb(a, r, g, b)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
