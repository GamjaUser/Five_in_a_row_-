package com.example.pro2023202001;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private Button startButton, restartButton, quitButton;
    private TextView winnerText, turnAndTimerText; // 시간, 턴 택스트 뷰
    private GomokuBoardView gomokuBoardView;
    private int[][] gameBoard = new int[15][15]; // 오목 게임판
    private boolean isGameStarted = false;
    private boolean blackTurn = true;
    private int previousWinner = 0; // 0: 없음, 1: 흑 승, 2: 백 승
    private CountDownTimer timer; // 카운트다운 타이머
    private static final int TURN_TIME_LIMIT = 30000; // 30초

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        restartButton = findViewById(R.id.restartButton);
        quitButton = findViewById(R.id.quitButton);
        winnerText = findViewById(R.id.winnerText);
        turnAndTimerText = findViewById(R.id.turnAndTimerText); // 시간, 차례 택스트 초기화
        gomokuBoardView = findViewById(R.id.gomokuBoard);

        // 시작 버튼 누르면 게임 시작
        startButton.setOnClickListener(v -> startGame());
        restartButton.setOnClickListener(v -> restartGame());
        quitButton.setOnClickListener(v -> quitGame());

        // 터치 입력으로 돌 놓기
        gomokuBoardView.setOnTouchListener((v, event) -> {
            if (isGameStarted && event.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(event.getX(), event.getY());
            }
            return true;
        });
    }

    // 게임 시작 시 오목판 보이기 및 초기화
    private void startGame() {
        resetBoard(); // 게임판 초기화
        isGameStarted = true;
        startButton.setVisibility(View.GONE); // 시작 버튼 숨기기
        gomokuBoardView.setVisibility(View.VISIBLE); // 오목판 보이기
        winnerText.setVisibility(View.GONE); // 승자 텍스트 숨기기
        turnAndTimerText.setVisibility(View.VISIBLE); // 턴 및 타이머 텍스트 보이기
        blackTurn = true; // 흑부터 시작

        // 오목판 배경을 woodimg.jpg로 설정
        Drawable woodBackground = ContextCompat.getDrawable(this, R.drawable.woodimg);
        gomokuBoardView.setBackground(woodBackground);

        gomokuBoardView.setGameBoard(gameBoard); // 게임판 그리기
        startTurnTimer(); // 턴 타이머 시작
    }

    // 게임 재시작 로직
    private void restartGame() {
        resetBoard();
        isGameStarted = true;
        winnerText.setVisibility(View.GONE);
        restartButton.setVisibility(View.GONE);
        quitButton.setVisibility(View.GONE);
        turnAndTimerText.setVisibility(View.VISIBLE); // 턴 및 타이머 텍스트 보이기

        // 전판 패배자부터 게임 재시작
        if (previousWinner == 1) {
            blackTurn = false; // 흰색이 먼저 시작
        } else if (previousWinner == 2) {
            blackTurn = true; // 검정이 먼저 시작
        }

        gomokuBoardView.setGameBoard(gameBoard);
        startTurnTimer(); // 턴 타이머 다시 시작
    }

    // 게임 종료
    private void quitGame() {
        isGameStarted = false;
        gomokuBoardView.setVisibility(View.GONE);
        turnAndTimerText.setVisibility(View.GONE); // 타이머 및 차례 텍스트 숨기기
        gomokuBoardView.setBackgroundColor(Color.WHITE);
        winnerText.setText("Game Over");
        winnerText.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.GONE);
        quitButton.setVisibility(View.GONE);
        if (timer != null) {
            timer.cancel(); // 타이머 중지
        }
    }

    // 게임판 초기화
    private void resetBoard() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                gameBoard[i][j] = 0;
            }
        }
        gomokuBoardView.setGameBoard(gameBoard);
    }

    // 터치 입력 처리
    private void handleTouch(float x, float y) {
        float offsetX = (gomokuBoardView.getWidth() - gomokuBoardView.getCellSize() * 15) / 2;
        float offsetY = (gomokuBoardView.getHeight() - gomokuBoardView.getCellSize() * 15) / 2;

        float adjustedX = x - offsetX;
        float adjustedY = y - offsetY;

        int row = (int) (adjustedY / gomokuBoardView.getCellSize());
        int col = (int) (adjustedX / gomokuBoardView.getCellSize());

        if (row >= 0 && row < 15 && col >= 0 && col < 15 && gameBoard[row][col] == 0) {
            gameBoard[row][col] = blackTurn ? 1 : 2;
            blackTurn = !blackTurn;
            gomokuBoardView.setGameBoard(gameBoard);

            if (timer != null) {
                timer.cancel(); // 이전 타이머 취소
            }

            // 승리 조건 확인
            if (checkWin(row, col)) {
                isGameStarted = false;
                int winner = gameBoard[row][col];
                winnerText.setText((winner == 1 ? "Black" : "White") + " Win!");
                winnerText.setVisibility(View.VISIBLE);
                restartButton.setVisibility(View.VISIBLE);
                quitButton.setVisibility(View.VISIBLE);
                previousWinner = winner; // 승리자 기록
                turnAndTimerText.setVisibility(View.GONE); // 타이머 숨김
            } else {
                startTurnTimer(); // 새 턴 타이머 시작
            }
        }
    }

    // 30초 타이머 시작
    private void startTurnTimer() {
        String turnText = blackTurn ? "Now, Black turn" : "Now, White turn";
        turnAndTimerText.setText(turnText); // 차례 텍스트 설정

        timer = new CountDownTimer(TURN_TIME_LIMIT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                turnAndTimerText.setText(turnText + "\nTime left: " + secondsLeft + "s");
            }

            @Override
            public void onFinish() {
                // 시간이 다 지나면 상대방 자동 승리
                isGameStarted = false;
                int winner = blackTurn ? 2 : 1; // 검정 차례에서 시간이 끝나면 흰색 승리, 흰색 차례에서 끝나면 검정 승리
                winnerText.setText((winner == 1 ? "Black" : "White") + " Win by Timeout!");
                winnerText.setVisibility(View.VISIBLE);
                restartButton.setVisibility(View.VISIBLE);
                quitButton.setVisibility(View.VISIBLE);
                turnAndTimerText.setVisibility(View.GONE); // 타이머 숨김
                previousWinner = winner; // 승리자 기록
            }
        }.start();
    }

    // 승리 체크 (임시 false 반환)
    private boolean checkWin(int row, int col) {
        int color = gameBoard[row][col]; // 현재 놓인 돌의 색 (1: 흑, 2: 백)

        // 가로, 세로, 대각선 방향을 확인하여 5개가 연속으로 놓였는지 검사
        return checkDirection(row, col, 1, 0, color)   // 가로 확인
                || checkDirection(row, col, 0, 1, color)   // 세로 확인
                || checkDirection(row, col, 1, 1, color)   // 오른쪽 아래 대각선 확인
                || checkDirection(row, col, 1, -1, color); // 왼쪽 아래 대각선 확인
    }

    private boolean checkDirection(int row, int col, int dRow, int dCol, int color) {
        int count = 1; // 현재 돌을 포함하여 몇 개 연속인지 카운트

        // 현재 돌의 좌우(혹은 상하, 대각선) 양쪽을 탐색하면서 같은 색 돌을 찾음
        count += countStones(row, col, dRow, dCol, color);   // 한쪽 방향
        count += countStones(row, col, -dRow, -dCol, color); // 반대 방향

        return count >= 5; // 5개 이상이면 승리
    }

    private int countStones(int row, int col, int dRow, int dCol, int color) {
        int count = 0;
        int newRow = row + dRow;
        int newCol = col + dCol;

        // 범위 안에서 같은 색 돌이 있는 한 계속 카운트
        while (newRow >= 0 && newRow < 15 && newCol >= 0 && newCol < 15 && gameBoard[newRow][newCol] == color) {
            count++;
            newRow += dRow;
            newCol += dCol;
        }

        return count;
    }
}