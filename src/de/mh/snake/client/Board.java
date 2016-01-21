package de.mh.snake.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JPanel;
import de.mh.snake.server.Game;

public class Board extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	public static final int DOTS = 20;
	public static final Color BGCOLOR = Color.decode("#424242");
	public static final Color FRUITCOLOR = Color.decode("#FFEB3B");
	public static final Color SOLIDCOLOR = Color.decode("#FFFFFF");

	public int[][] field = new int[Game.WIDTH][Game.HEIGHT];
	public int id = 9999;
	public String text = "";
	public Color myColor = Color.BLACK;
	public HashMap<Integer, Color> otherColor = new HashMap<>();
	
	
	public Board() {
		
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(Game.WIDTH*DOTS, Game.HEIGHT*DOTS));
		
		// generate my color
		Random rnd = new Random();
		float hue = rnd.nextFloat();
		float sat = (rnd.nextInt(4000) + 3000) / 10000f;
		float lum = .7f;
		myColor = Color.getHSBColor(hue, sat, lum);
		
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for (int x = 0; x < Game.WIDTH; x++) {
			for (int y = 0; y < Game.HEIGHT; y++) {
				
				if (field[x][y] == 1) {				// fruit (1)
					g.setColor(FRUITCOLOR);
					
				} else if (field[x][y] == -1) {		// border (-1)
					g.setColor(SOLIDCOLOR);
					
				} else if (field[x][y] == id) {		// my segments (id)
					g.setColor(myColor);
					
				} else if (field[x][y] == -id) {	// my head (-id)
					g.setColor(myColor.brighter());
					
				} else if (field[x][y] > 1) {		// other segments (>1)
					g.setColor(otherColor.get(field[x][y]));
					
				} else if (field[x][y] < -1) {		// other heads (<-1)
					g.setColor(otherColor.get(-field[x][y]).brighter());
					
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
