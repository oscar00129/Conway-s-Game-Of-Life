package com.oscar00129;

import javax.swing.*;
import java.awt.*;

/**
 * This class initializes the program.
 */
public class Main {
    public static void main(String[] args) {
        new Program(1280, 720, 10000, 24, 4);
    }
}

/**
* This class contains the program initial values and creates the main window.
*/
class Program {

    /**
    * Constructs the window application.
    *
    * @param windowWidth   The main window width.
    * @param windowHeight  The main window height.
    * @param cellsNumber   The number of cells that will be at the program's start.
    * @param FPS           Frames per second the application displays.
    * @param boxPerPixel   The cell size by pixels.
    */
    public Program(int windowWidth, int windowHeight, int cellsNumber, int FPS, int boxPerPixel) {
        JFrame window = new JFrame("Conway's Game of Life");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(windowWidth, windowHeight);
        window.setResizable(false);

        CellWindow cw = new CellWindow(windowWidth / boxPerPixel, windowHeight / boxPerPixel, cellsNumber);

        MyPanel panel = new MyPanel(windowWidth, windowHeight, cw, boxPerPixel);
        window.add(panel);

        window.setVisible(true);

        MyThread thread = new MyThread(panel, FPS);
        thread.start();
    }
}

/**
* This class contains the thread instructions to repaint the data displayed in windows.
*/
class MyThread extends Thread {
    MyPanel canvas;
    int FPS;

    /**
    * Constructs the thread information.
    *
    * @param canvas    The canvas where data is displayed.
    * @param FPS       Frames per second the application displays.
    */
    MyThread(MyPanel canvas, int FPS) {
        this.canvas = canvas;
        this.FPS = FPS;
    }

    /**
    * Thread main method where calls to repaint canvas and calculate info to be displayed.
    */
    @Override
    public void run() {
        try {
            while (true) {
                canvas.cw.applyRules();
                canvas.repaint();
                Thread.sleep(1000 / FPS);
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}

/**
* This class contains the canvas where everything is displayed.
*/
class MyPanel extends JPanel {
    CellWindow cw;
    int boxPerPixel;

    /**
    * Constructs the panel (canvas) information.
    *
    * @param windowWidth   The main window width.
    * @param windowHeight  The main window height.
    * @param cw            The cell matrix information and methods.
    * @param boxPerPixel   The cell size by pixels.
    */
    public MyPanel(int windowWidth, int windowHeight, CellWindow cw, int boxPerPixel) {
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        this.cw = cw;
        this.boxPerPixel = boxPerPixel;
    }

    /**
    * Paints the component.
    *
    * @param g The Graphics context used for drawing.
    */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(214, 204, 178));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(new Color(114, 75, 57));
        for (int y = 0; y < cw.windowCells.length; y++) {
            for (int x = 0; x < cw.windowCells[0].length; x++) {
                if (cw.windowCells[y][x]) {
                    g.fillRect(x * boxPerPixel, y * boxPerPixel, boxPerPixel, boxPerPixel);
                }
            }
        }
    }
}

/**
* Represents the cell matrix and contains logic for Conway's Game of Life.
*/
class CellWindow {
    public boolean[][] windowCells;

    /**
    * Constructs the cell matrix.
    *
    * @param width         Matrix width divided by cells pixel size.
    * @param height        Matrix height divided by cells pixel size.
    * @param numberCells   The number of cells that will be at the program's start.
    */
    public CellWindow(int width, int height, int numberCells) {
        windowCells = new boolean[height][width];
        setRandomCells(numberCells);
    }

    /**
     *  Sets random cells position.
     *
     *  @param numberCells The number of cells that will be generated.
     */
    private void setRandomCells(int numberCells) {
        for (int i = 0; i < numberCells; i++) {
            windowCells[generateRandomPos()[0]][generateRandomPos()[1]] = true;
        }
    }

    /**
     * Generates a random position on matrix.
     */
    private int[] generateRandomPos() {
        int randomCellY = (int)(Math.random() * windowCells.length);
        int randomCellX = (int)(Math.random() * windowCells[0].length);

        int[] result = {randomCellY, randomCellX};
        return result;
    }

    /**
     * Apply the rules of Conway's Game of Life on cells.
     */
    public void applyRules() {
        boolean[][] newMatrix = new boolean[windowCells.length][];
        for (int i = 0; i < windowCells.length; i++) {
            newMatrix[i] = windowCells[i].clone();
        }

        for (int i = 0; i < windowCells.length; i++) {
            for (int j = 0; j < windowCells[0].length; j++) {
                int neighborsBorn = countLiveNeighbors(j, i);
                if (!windowCells[i][j] && neighborsBorn == 3) {
                    //System.out.println("Encontrado en: [" + i + "][" + j + "]");
                    newMatrix[i][j] = true;
                } else if(windowCells[i][j]) {
                    if (neighborsBorn > 3 || neighborsBorn <= 1) {
                        newMatrix[i][j] = false;
                    } else {
                        newMatrix[i][j] = true;
                    }
                }
            }
        }

        windowCells = newMatrix;
    }

    /**
     * Gets neighbors by position.
     *
     * @param posX Cell's X position.
     * @param posY Cell's Y position.
     */
    private int countLiveNeighbors(int posX, int posY) {
        int count = 0;
        int height = windowCells.length;
        int width = windowCells[0].length;

        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                if (x == 0 && y == 0) continue;
                int newY = posY + y;
                int newX = posX + x;

                if (newY >= 0 && newY < height && newX >= 0 && newX < width) {
                    if (windowCells[newY][newX]) count++;
                }

            }
        }
        return count;
    }
}

