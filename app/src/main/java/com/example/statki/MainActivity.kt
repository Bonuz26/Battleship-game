package com.example.statki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.statki.ui.theme.StatkiTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StatkiTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        BattleshipGame()
                    }
                }
            }
        }
    }
}

@Composable
fun BattleshipGame() {
    val boardSize = 5
    val maxShips = 5
    val orangeTheme = Color(0xFFFF8C00)

    var playerShips by remember { mutableStateOf(listOf<Int>()) }
    var gameStarted by remember { mutableStateOf(false) }
    var enemyShips by remember { mutableStateOf(listOf<Int>()) }
    var playerShots by remember { mutableStateOf(listOf<Int>()) }
    var enemyShots by remember { mutableStateOf(listOf<Int>()) }
    var message by remember { mutableStateOf("Ustaw 5 statków") }
    var gameOver by remember { mutableStateOf(false) }

    val playerHits = playerShots.count { enemyShips.contains(it) }
    val enemyHits = enemyShots.count { playerShips.contains(it) }

    fun generateShips(size: Int, count: Int): List<Int> {
        return (0 until size * size).shuffled().take(count)
    }

    fun generateEnemyShot(size: Int, currentShots: List<Int>): Int {
        val available = (0 until size * size).filter { !currentShots.contains(it) }
        return if (available.isNotEmpty()) available.random() else -1
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 1.0f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "GRA W STATKI", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = orangeTheme)

            Spacer(modifier = Modifier.height(8.dp))

            if (message.isNotEmpty() && !gameOver) {
                Text(text = message, fontSize = 18.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Twoja plansza",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            PlayerBoard(
                boardSize = boardSize,
                playerShips = playerShips,
                enemyShots = enemyShots,
                orangeColor = orangeTheme,
                onCellClick = { index ->
                    if (!gameStarted) {
                        if (playerShips.contains(index)) {
                            playerShips = playerShips - index
                        } else if (playerShips.size < maxShips) {
                            playerShips = playerShips + index
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (!gameStarted) {
                Button(
                    onClick = {
                        enemyShips = generateShips(boardSize, maxShips)
                        playerShots = emptyList()
                        enemyShots = emptyList()
                        message = ""
                        gameStarted = true
                        gameOver = false
                    },
                    enabled = playerShips.size == maxShips,
                    colors = ButtonDefaults.buttonColors(containerColor = orangeTheme)
                ) {
                    Text("Start gry")
                }
            } else {
                Text(
                    text = "Plansza przeciwnika",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                EnemyBoard(
                    boardSize = boardSize,
                    enemyShips = enemyShips,
                    playerShots = playerShots,
                    orangeColor = orangeTheme,
                    onCellClick = { index ->
                        if (!gameOver && !playerShots.contains(index)) {
                            val newPlayerShots = playerShots + index
                            playerShots = newPlayerShots

                            if (enemyShips.all { newPlayerShots.contains(it) }) {
                                message = "WYGRAŁEŚ!"
                                gameOver = true
                            } else {
                                val shot = generateEnemyShot(boardSize, enemyShots)
                                if (shot != -1) {
                                    val newEnemyShots = enemyShots + shot
                                    enemyShots = newEnemyShots
                                    if (playerShips.all { newEnemyShots.contains(it) }) {
                                        message = "PRZEGRAŁEŚ!"
                                        gameOver = true
                                    }
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Trafiłeś:", fontSize = 14.sp, color = Color.White)
                        Text(text = "$playerHits", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = orangeTheme)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Przeciwnik trafił:", fontSize = 14.sp, color = Color.White)
                        Text(text = "$enemyHits", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = orangeTheme)
                    }
                }

                if (gameOver) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = message,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (message == "WYGRAŁEŚ!") Color(0xFF2E7D32) else Color.Red
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        gameStarted = false
                        playerShips = emptyList()
                        playerShots = emptyList()
                        enemyShots = emptyList()
                        gameOver = false
                        message = "Ustaw 5 statków"
                    }, colors = ButtonDefaults.buttonColors(containerColor = orangeTheme)) {
                        Text("Zagraj ponownie")
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerBoard(boardSize: Int, playerShips: List<Int>, enemyShots: List<Int>, orangeColor: Color, onCellClick: (Int) -> Unit) {
    Column {
        for (row in 0 until boardSize) {
            Row {
                for (col in 0 until boardSize) {
                    val index = row * boardSize + col
                    val hasShip = playerShips.contains(index)
                    val wasShot = enemyShots.contains(index)
                    val isHit = wasShot && hasShip

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(2.dp)
                            .background(
                                when {
                                    isHit -> Color.Red
                                    wasShot -> orangeColor
                                    hasShip -> Color.Gray
                                    else -> Color.LightGray
                                }
                            )
                            .clickable { onCellClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasShip && !isHit) Text("o", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        if (isHit) Text("X", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        else if (wasShot) Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun EnemyBoard(boardSize: Int, enemyShips: List<Int>, playerShots: List<Int>, orangeColor: Color, onCellClick: (Int) -> Unit) {
    Column {
        for (row in 0 until boardSize) {
            Row {
                for (col in 0 until boardSize) {
                    val index = row * boardSize + col
                    val wasShot = playerShots.contains(index)
                    val isHit = wasShot && enemyShips.contains(index)

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(2.dp)
                            .background(
                                when {
                                    isHit -> Color.Red
                                    wasShot -> orangeColor
                                    else -> Color.LightGray
                                }
                            )
                            .clickable { onCellClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isHit) Text("X", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        else if (wasShot) Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StatkiTheme { BattleshipGame() }
}