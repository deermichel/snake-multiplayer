package de.mh.snake.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.net.InetAddress;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import de.mh.snake.Request;
import de.mh.snake.Response;
import de.mh.snake.server.Game;
import de.mh.snake.server.Player;

public class Board extends JPanel {

	public static final int DOTS = 20;
	public static final Color BGCOLOR = Color.decode("#424242");
	public static final Color MYCOLOR = Color.decode("#F44336");
	public static final Color FRUITCOLOR = Color.decode("#FFEB3B");
	public static final Color OTHERCOLOR = Color.decode("#2196F3");
	public static final Color BORDERCOLOR = Color.decode("#FFFFFF");

	public int[][] field = new int[Game.WIDTH][Game.HEIGHT];
	public int id = -9999;
	public String text = "";
	
	
	public Board() {
		
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(Game.WIDTH*DOTS, Game.HEIGHT*DOTS));
		
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for (int x = 0; x < Game.WIDTH; x++) {
			for (int y = 0; y < Game.HEIGHT; y++) {
				
				if (field[x][y] == 1) {				// fruit (1)
					g.setColor(FRUITCOLOR);
					
				} else if (field[x][y] == -1) {		// border (-1)
					g.setColor(BORDERCOLOR);
					
				} else if (field[x][y] == id) {		// my segments (id)
					g.setColor(MYCOLOR);
					
				} else if (field[x][y] == -id) {	// my head (-id)
					g.setColor(MYCOLOR.brighter());
					
				} else if (field[x][y] > 1) {		// other segments (>1)
					g.setColor(OTHERCOLOR);
					
				} else if (field[x][y] < -1) {		// other heads (<-1)
					g.setColor(OTHERCOLOR.brighter());
					
				} else {							// bg (0)
					g.setColor(BGCOLOR);
				}
				
				g.fillRect(x * DOTS, y * DOTS, DOTS, DOTS);
				
			}
		}
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 16));
		g.drawString(text, 10, getHeight() - 10);
		
	}
	
}
