package com.alyfer.jogodavelha;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class Game extends Canvas implements Runnable, MouseListener {

    public static final int width = 300, height = 300;
    public int player = 1, enemy = -1, current = enemy;

    public BufferedImage playerSpr, enemySpr;
    public int[][] board = new int[3][3];

    public static int mX, mY;

    public boolean pressed = false;

    public Game() {
        this.setPreferredSize(new Dimension(width, height));
        this.addMouseListener(this);
        try {
            playerSpr = ImageIO.read(getClass().getResource("/player.png"));
            enemySpr = ImageIO.read(getClass().getResource("/enemy.png"));
        } catch (IOException err) {
            err.printStackTrace();
        }
        resetBoard();
    }

    public static void main(String[] args) {
        //Janela
        Game game = new Game();
        JFrame frame = new JFrame("Jogo da Velha");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        new Thread(game).start();
    }

    public void resetBoard() {
        for(int xx = 0; xx < board.length; xx++) {
            for(int yy = 0; yy < board.length; yy++) {
                board[xx][yy] = 0;
            }
        }
    }

    public int checkVictory() {
        //Verificar se o player ganhou

        //Horizontal
        if(board[0][0] == player && board[1][0] == player && board[2][0] == player) {
            return player;
        }
        if(board[0][1] == player && board[1][1] == player && board[2][1] == player) {
            return player;
        }
        if(board[0][2] == player && board[1][2] == player && board[2][2] == player) {
            return player;
        }

        //Vertical
        if(board[0][0] == player && board[0][1] == player && board[0][2] == player) {
            return player;
        }
        if(board[1][0] == player && board[1][1] == player && board[1][2] == player) {
            return player;
        }
        if(board[2][0] == player && board[2][1] == player && board[2][2] == player) {
            return player;
        }

        //Diagonal
        if(board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return player;
        }
        if(board[2][0] == player && board[1][1] == player && board[0][2] == player) {
            return player;
        }

        //Verificar se o inimigo ganhou

        //Horizontal
        if(board[0][0] == enemy && board[1][0] == enemy && board[2][0] == enemy) {
            return enemy;
        }
        if(board[0][1] == enemy && board[1][1] == enemy && board[2][1] == enemy) {
            return enemy;
        }
        if(board[0][2] == enemy && board[1][2] == enemy && board[2][2] == enemy) {
            return enemy;
        }

        //Vertical
        if(board[0][0] == enemy && board[0][1] == enemy && board[0][2] == enemy) {
            return enemy;
        }
        if(board[1][0] == enemy && board[1][1] == enemy && board[1][2] == enemy) {
            return enemy;
        }
        if(board[2][0] == enemy && board[2][1] == enemy && board[2][2] == enemy) {
            return enemy;
        }

        //Diagonal
        if(board[0][0] == enemy && board[1][1] == enemy && board[2][2] == enemy) {
            return enemy;
        }
        if(board[2][0] == enemy && board[1][1] == enemy && board[0][2] == enemy) {
            return enemy;
        }

        int curScore = 0;
        //Empate
        for(int xx = 0; xx < board.length; xx++) {
            for(int yy = 0; yy < board.length; yy++) {
                if(board[xx][yy] != 0) {
                    curScore++;
                }
            }
        }

        if(curScore == board.length * board[0].length) {
            return 0;
        }

        //Ninguem ganhou
        return -10;
    }

    public void tick() {
        if(current == player) {
            if(pressed) {
                pressed = false;
                mX /= 100;
                mY /= 100;
                if(board[mX][mY] == 0) {
                    board[mX][mY] = player;
                    current = enemy;
                }
            }
        } else if (current == enemy) {
            /*if(pressed) {
                pressed = false;
                mX /= 100;
                mY /= 100;
                if(board[mX][mY] == 0) {
                    board[mX][mY] = enemy;
                    current = player;
                }
            }*/
            for(int xx = 0; xx < board.length; xx++) {
                for(int yy = 0; yy < board.length; yy++) {
                    if(board[xx][yy] == 0) {
                        Node bestMove = getBestMove(xx, yy, 0, enemy);
                        board[bestMove.x][bestMove.y] = enemy;
                        current = player;
                        return;
                    }
                }
            }
        }

        if(checkVictory() == player) {
            System.out.println("Player ganhou");
            resetBoard();
        } else if (checkVictory() == enemy) {
            System.out.println("Inimigo ganhou");
            resetBoard();
        } else if (checkVictory() == 0) {
            System.out.println("Empate");
            resetBoard();
        }
    }

    public Node getBestMove(int x, int y, int depth, int shift) {
        if(checkVictory() == player) {
            return new Node(x,y,depth-10,depth);
        } else if (checkVictory() == enemy) {
            return new Node(x,y,10-depth,depth);
        } else if (checkVictory() == 0) {
            return new Node(x,y,0,depth);
        }
        List<Node> nodes = new ArrayList<Node>();
        for(int xx = 0; xx < board.length; xx++) {
            for(int yy = 0; yy < board.length; yy++) {

                if(board[xx][yy] == 0) {
                    Node node;
                    if(shift == player) {
                        board[xx][yy] = player;
                        node = getBestMove(xx,yy, depth+1,enemy);
                        board[xx][yy] = 0;
                    } else {
                        board[xx][yy] = enemy;
                        node = getBestMove(xx,yy, depth+1,player);
                        board[xx][yy] = 0;
                    }
                    nodes.add(node);
                }
            }
        }
        Node finalNode = nodes.get(0);
        for(int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            if(shift == player) {
                if(n.score > finalNode.score) {
                    finalNode = n;
                }
            } else {
                if(n.score < finalNode.score) {
                    finalNode = n;
                }
            }
        }
        return finalNode;
    }


    //Renderização
    public void render () {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);

        for(int xx = 0; xx < board.length; xx++) {
            for(int yy = 0; yy < board.length; yy++) {
                g.setColor(Color.black);
                g.drawRect(xx * 100, yy * 100, 100,100);
                if(board[xx][yy] == player) {
                    g.drawImage(playerSpr, xx * 100 + 25, yy * 100 + 25, 50, 50, null);
                } else if (board[xx][yy] == enemy) {
                    g.drawImage(enemySpr, xx * 100 + 25, yy * 100 + 25, 50, 50, null);
                }
            }
        }

        g.dispose();
        bs.show();
    }

    //Game Loop
    @Override
    public void run() {
        while(true) {
            tick();
            render();
            try {
                Thread.sleep(1000/60);
            } catch (InterruptedException err) {
                err.printStackTrace();
            }
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;
        mX = e.getX();
        mY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
