package pl.wsei.pam.lab02

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab03.Lab03Activity

class Lab02Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab02)
        setupButtonListeners()

    }
    fun setupButtonListeners() {
        val buttons = listOf(
            findViewById<Button>(R.id.main_6_6_board),
            findViewById<Button>(R.id.main_4_4_board),
            findViewById<Button>(R.id.main_4_3_board),
            findViewById<Button>(R.id.main_3_2_board)
        )

        buttons.forEach { button ->
            button.setOnClickListener { view ->
                val tag: String? = view.tag as String?
                val tokens: List<String>? = tag?.split("x")

                val rows = tokens?.get(0)?.toInt() ?: 0
                val columns = tokens?.get(1)?.toInt() ?: 0
                Toast.makeText(this, "$rows x $columns", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Lab03Activity::class.java)

                val size: IntArray = intArrayOf(rows, columns)
                intent.putExtra("size", size)

                startActivity(intent)
            }
        }
    }

}