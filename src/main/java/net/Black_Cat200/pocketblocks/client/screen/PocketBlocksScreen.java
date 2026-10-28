package net.Black_Cat200.pocketblocks.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import java.util.Random;

public class PocketBlocksScreen extends Screen {
    private static final int COLS = 10;
    private static final int ROWS = 20;
    private static final int TILE_SIZE = 12;

    private int[][] grid = new int[ROWS][COLS];
    private final Random random = new Random();

    // Все фазы вращения для каждой фигуры
    private static final int[][][][] POCKET_ROTATIONS = {
            // 0: I (Палка) - 2 положения
            {
                    {{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}},
                    {{0,0,1,0}, {0,0,1,0}, {0,0,1,0}, {0,0,1,0}}
            },
            // 1: O (Квадрат) - 1 положение
            {
                    {{1,1}, {1,1}}
            },
            // 2: Z - 2 положения
            {
                    {{1,1,0}, {0,1,1}, {0,0,0}},
                    {{0,1,0}, {1,1,0}, {1,0,0}}
            },
            // 3: S - 2 положения
            {
                    {{0,1,1}, {1,1,0}, {0,0,0}},
                    {{1,0,0}, {1,1,0}, {0,1,0}}
            },
            // 4: L - 4 положения
            {
                    {{1,1,1}, {1,0,0}, {0,0,0}},
                    {{1,1,0}, {0,1,0}, {0,1,0}},
                    {{0,0,1}, {1,1,1}, {0,0,0}},
                    {{1,0,0}, {1,0,0}, {1,1,0}}
            },
            // 5: J - 4 положения
            {
                    {{1,1,1}, {0,0,1}, {0,0,0}},
                    {{0,1,0}, {0,1,0}, {1,1,0}},
                    {{1,0,0}, {1,1,1}, {0,0,0}},
                    {{1,1,0}, {1,0,0}, {1,0,0}}
            },
            // 6: T - 4 положения
            {
                    {{1,1,1}, {0,1,0}, {0,0,0}},
                    {{0,1,0}, {1,1,0}, {0,1,0}},
                    {{0,1,0}, {1,1,1}, {0,0,0}},
                    {{0,1,0}, {0,1,1}, {0,1,0}}
            }
    };

    // Оригинальные цвета NES Тетриса
    private static final int[] COLORS = {
            0xFF4AAADE, 0xFFE79C21, 0xFFB51042, 0xFF5A9431, 0xFFDE6B10, 0xFF2142B5, 0xFFB521B5
    };

    private int currentPieceIndex;
    private int rotationIndex;
    private int pieceX, pieceY;

    private int nextPieceIndex;
    private int lastPieceIndex = -1;

    private int score = 0;
    private int lines = 0;
    private int level = 0;
    private int dropTimer = 0;

    private boolean isGameInitialized = false; // Флаг, чтобы знать, запущена ли уже игра

    // Переменные для реализации оригинального DAS (Зажатия клавиш)
    private boolean isLeftPressed = false;
    private boolean isRightPressed = false;
    private int dasCounter = 0;
    private static final int DAS_INITIAL_DELAY = 16; // Кадры до первого авто-сдвига
    private static final int DAS_REPEAT_DELAY = 6;    // Кадры между повторными сдвигами

    private static final int[] POCKET_SPEEDS = {48, 43, 38, 33, 28, 23, 18, 13, 8, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 2};

    public PocketBlocksScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        // Если игра ЕЩЕ НЕ запущенна — это первый вход, запускаем!
        if (!isGameInitialized) {
            resetGame();
            isGameInitialized = true; // Ставим флаг, что игра активна
        }
    }


    private void resetGame() {
        score = 0;
        lines = 0;
        level = 0;
        clearGrid();
        lastPieceIndex = -1;
        nextPieceIndex = random.nextInt(7);
        spawnNewPiece();
    }

    private int getPOCKETRandomPiece() {
        int res = random.nextInt(8);
        if (res == 7 || res == lastPieceIndex) {
            res = random.nextInt(7);
        }
        lastPieceIndex = res;
        return res;
    }

    private void clearGrid() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = 0;
            }
        }
    }

    private void spawnNewPiece() {
        currentPieceIndex = nextPieceIndex;
        rotationIndex = 0;
        nextPieceIndex = getPOCKETRandomPiece();

        pieceX = 3;
        // Корректный спавн NES: Палка прячется на -2 строку, чтобы зайти ровно верхней гранью блоков
        pieceY = currentPieceIndex == 0 ? -2 : 0;

        // БЕЗОПАСНЫЙ GAME OVER
        if (!canMove(getCurrentPieceMatrix(), pieceX, pieceY)) {
            clearGrid();
            score = 0;
            lines = 0;
            level = 0;
            // Не трогаем isGameInitialized, чтобы игра просто пошла по новому кругу без зависания интерфейса
        }

    }

    private int[][] getCurrentPieceMatrix() {
        return POCKET_ROTATIONS[currentPieceIndex][rotationIndex];
    }

    private boolean canMove(int[][] piece, int newX, int newY) {
        for (int r = 0; r < piece.length; r++) {
            for (int c = 0; c < piece[r].length; c++) {
                if (piece[r][c] != 0) {
                    int targetX = newX + c;
                    int targetY = newY + r;

                    if (targetX < 0 || targetX >= COLS || targetY >= ROWS) {
                        return false;
                    }
                    if (targetY >= 0 && grid[targetY][targetX] > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void rotatePiece() {
        int maxRotations = POCKET_ROTATIONS[currentPieceIndex].length;
        int nextRotation = (rotationIndex + 1) % maxRotations;
        int[][] nextMatrix = POCKET_ROTATIONS[currentPieceIndex][nextRotation];

        if (canMove(nextMatrix, pieceX, pieceY)) {
            rotationIndex = nextRotation;
        }
    }

    private void checkLines() {
        int linesClearedThisTick = 0;

        for (int r = ROWS - 1; r >= 0; r--) {
            boolean isFull = true;
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c] == 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                linesClearedThisTick++;
                for (int m = r; m > 0; m--) {
                    System.arraycopy(grid[m - 1], 0, grid[m], 0, COLS);
                }
                grid[0] = new int[COLS];
                r++;
            }
        }

        if (linesClearedThisTick > 0) {
            lines += linesClearedThisTick;

            int basePoints = 0;
            if (linesClearedThisTick == 1) basePoints = 40;
            else if (linesClearedThisTick == 2) basePoints = 100;
            else if (linesClearedThisTick == 3) basePoints = 300;
            else if (linesClearedThisTick == 4) basePoints = 1200;

            score += basePoints * (level + 1);
            level = lines / 10;
        }
    }

    private int getDropSpeed() {
        int index = Math.min(level, POCKET_SPEEDS.length - 1);
        return POCKET_SPEEDS[index];
    }

    @Override
    public void tick() {
        super.tick();

        // --- Логика обработки DAS (зажатия клавиш) ---
        if (isLeftPressed || isRightPressed) {
            dasCounter++;
            if (dasCounter == DAS_INITIAL_DELAY) {
                handleDasMove();
            } else if (dasCounter > DAS_INITIAL_DELAY && (dasCounter - DAS_INITIAL_DELAY) % DAS_REPEAT_DELAY == 0) {
                handleDasMove();
            }
        }

        // --- Логика падения фигуры по таймеру ---
        dropTimer++;
        if (dropTimer >= getDropSpeed()) {
            dropTimer = 0;

            if (canMove(getCurrentPieceMatrix(), pieceX, pieceY + 1)) {
                pieceY++;
            } else {
                int[][] piece = getCurrentPieceMatrix();
                for (int r = 0; r < piece.length; r++) {
                    for (int c = 0; c < piece[r].length; c++) {
                        if (piece[r][c] != 0 && (pieceY + r) >= 0) {
                            grid[pieceY + r][pieceX + c] = currentPieceIndex + 1;
                        }
                    }
                }
                checkLines();
                spawnNewPiece();
            }
        }
    }

    private void handleDasMove() {
        int[][] matrix = getCurrentPieceMatrix();
        if (isLeftPressed && canMove(matrix, pieceX - 1, pieceY)) {
            pieceX--;
        } else if (isRightPressed && canMove(matrix, pieceX + 1, pieceY)) {
            pieceX++;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int[][] matrix = getCurrentPieceMatrix();

        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            isLeftPressed = true;
            isRightPressed = false; // Приоритет последней нажатой
            dasCounter = 0;         // Сброс счетчика DAS
            if (canMove(matrix, pieceX - 1, pieceY)) pieceX--;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            isRightPressed = true;
            isLeftPressed = false;
            dasCounter = 0;
            if (canMove(matrix, pieceX + 1, pieceY)) pieceX++;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            if (canMove(matrix, pieceX, pieceY + 1)) {
                pieceY++;
                score += 1; // 1 очко за Soft Drop на NES
            }
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_UP) {
            rotatePiece();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            isLeftPressed = false;
            dasCounter = 0;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            isRightPressed = false;
            dasCounter = 0;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Сначала даем игре плавно затемнить мир на заднем плане
        this.renderTransparentBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Рисуем заголовок по центру
        guiGraphics.drawCenteredString(this.font, "Pocket Blocks", this.width / 2, 10, 0xFFFFFF);

        int playAreaWidth = COLS * TILE_SIZE;
        int playAreaHeight = ROWS * TILE_SIZE;
        int startX = (this.width - playAreaWidth) / 2;
        int startY = (this.height - playAreaHeight) / 2;

        // Рендерим стакан игры
        guiGraphics.fill(startX, startY, startX + playAreaWidth, startY + playAreaHeight, 0xFF000000);
        guiGraphics.renderOutline(startX - 1, startY - 1, playAreaWidth + 2, playAreaHeight + 2, 0xFFFFFFFF);

        int infoX = startX + playAreaWidth + 15;

        // ПРАВИЛЬНЫЙ РЕНДЕР ТЕКСТА (с флагом false в конце, чтобы убрать блюр):
        guiGraphics.drawString(this.font, "SCORE", infoX, startY, 0xFF4AAADE, false);
        guiGraphics.drawString(this.font, String.format("%06d", score), infoX, startY + 10, 0xFFFFFFFF, false);

        guiGraphics.drawString(this.font, "LINES", infoX, startY + 30, 0xFF4AAADE, false);
        guiGraphics.drawString(this.font, String.format("%03d", lines), infoX, startY + 40, 0xFFFFFFFF, false);

        guiGraphics.drawString(this.font, "LEVEL", infoX, startY + 60, 0xFF4AAADE, false);
        guiGraphics.drawString(this.font, String.format("%02d", level), infoX, startY + 70, 0xFFFFFFFF, false);

        guiGraphics.drawString(this.font, "NEXT", infoX, startY + 95, 0xFF4AAADE, false);

        int nextBoxSize = 4 * TILE_SIZE;
        guiGraphics.fill(infoX, startY + 107, infoX + nextBoxSize, startY + 107 + nextBoxSize, 0xFF000000);
        guiGraphics.renderOutline(infoX - 1, startY + 106, nextBoxSize + 2, nextBoxSize + 2, 0xFFFFFFFF);

        // Рендер следующей фигурып
        int[][] nextMatrix = POCKET_ROTATIONS[nextPieceIndex][0];
        int nextColor = COLORS[nextPieceIndex];
        for (int r = 0; r < nextMatrix.length; r++) {
            for (int c = 0; c < nextMatrix[r].length; c++) {
                if (nextMatrix[r][c] != 0) {
                    int xOffset = (nextPieceIndex == 0) ? 0 : 6;
                    int bX = infoX + c * TILE_SIZE + xOffset;
                    int bY = startY + 107 + r * TILE_SIZE + 6;
                    guiGraphics.fill(bX, bY, bX + TILE_SIZE, bY + TILE_SIZE, nextColor);
                }
            }
        }

        // Рендер зафиксированных блоков стакана
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int colorIndex = grid[r][c];
                if (colorIndex > 0) {
                    int blockX = startX + c * TILE_SIZE;
                    int blockY = startY + r * TILE_SIZE;
                    guiGraphics.fill(blockX, blockY, blockX + TILE_SIZE, blockY + TILE_SIZE, COLORS[colorIndex - 1]);
                }
            }
        }

        // Рендер активной летящей детали
        int[][] currentMatrix = getCurrentPieceMatrix();
        int color = COLORS[currentPieceIndex];
        for (int r = 0; r < currentMatrix.length; r++) {
            for (int c = 0; c < currentMatrix[r].length; c++) {
                if (currentMatrix[r][c] != 0) {
                    int blockX = startX + (pieceX + c) * TILE_SIZE;
                    int blockY = startY + (pieceY + r) * TILE_SIZE;

                    // Отрисовываем блок только если он опустился ниже верхней границы стакана!
                    if (blockY >= startY) {
                        guiGraphics.fill(blockX, blockY, blockX + TILE_SIZE, blockY + TILE_SIZE, color);
                    }
                }
            }
        }
    }
}