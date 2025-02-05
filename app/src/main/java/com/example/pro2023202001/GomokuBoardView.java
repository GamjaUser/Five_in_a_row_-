package com.example.pro2023202001;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GomokuBoardView extends View {

    private Paint gridPaint, blackPaint, whitePaint;
    private int boardSize = 15;
    private float cellSize;
    private int[][] gameBoard = new int[boardSize][boardSize]; // 오목판 상태 저장
    private Bitmap boardBackground; // 배경 이미지 저장할 변수

    public GomokuBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gridPaint = new Paint();
        gridPaint.setColor(android.graphics.Color.BLACK);
        gridPaint.setStrokeWidth(5);

        blackPaint = new Paint();
        blackPaint.setColor(android.graphics.Color.BLACK);

        whitePaint = new Paint();
        whitePaint.setColor(android.graphics.Color.WHITE);

        // 배경 이미지를 Bitmap으로 불러옴
        boardBackground = BitmapFactory.decodeResource(getResources(), R.drawable.woodimg);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 가로, 세로 중 작은 값을 기준으로 cellSize 설정
        cellSize = Math.min(getWidth(), getHeight()) / (float) boardSize;

        // 배경 이미지 크기를 오목판 크기에 맞게 조정
        Bitmap scaledBackground = Bitmap.createScaledBitmap(boardBackground, (int) (cellSize * boardSize), (int) (cellSize * boardSize), true);

        // 배경 이미지와 그리드를 중앙에 배치하기 위한 오프셋 계산
        float offsetX = (getWidth() - scaledBackground.getWidth()) / 2;
        float offsetY = (getHeight() - scaledBackground.getHeight()) / 2;

        // 배경 이미지를 중앙에 그리기
        canvas.drawBitmap(scaledBackground, offsetX, offsetY, null);

        // 그리드 그리기 (중앙에 맞추어 오프셋 추가)
        for (int i = 0; i < boardSize; i++) {
            // 세로선
            canvas.drawLine(i * cellSize + offsetX, offsetY, i * cellSize + offsetX, cellSize * boardSize + offsetY, gridPaint);
            // 가로선
            canvas.drawLine(offsetX, i * cellSize + offsetY, cellSize * boardSize + offsetX, i * cellSize + offsetY, gridPaint);
        }

        // 돌 그리기 (중앙에 맞추어 오프셋 추가)
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (gameBoard[row][col] == 1) { // 검정 돌
                    canvas.drawCircle(col * cellSize + cellSize / 2 + offsetX, row * cellSize + cellSize / 2 + offsetY, cellSize / 2 - 10, blackPaint);
                } else if (gameBoard[row][col] == 2) { // 흰 돌
                    canvas.drawCircle(col * cellSize + cellSize / 2 + offsetX, row * cellSize + cellSize / 2 + offsetY, cellSize / 2 - 10, whitePaint);
                }
            }
        }
    }

    // 게임판 업데이트
    public void setGameBoard(int[][] gameBoard) {
        this.gameBoard = gameBoard;
        invalidate(); // 다시 그리기
    }

    // cellSize 반환
    public float getCellSize() {
        return cellSize;
    }
}
