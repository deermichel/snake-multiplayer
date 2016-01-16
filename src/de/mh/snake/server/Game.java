package de.mh.snake.server;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.Timer;

public class Game {

	public static final int WIDTH = 64;
	public static final int HEIGHT = 36;
	public static final int FRUITS = 3;
	public static final int TICK = 100;
	
	public int field[][] = new int[WIDTH][HEIGHT];
	public ArrayList<Player> players = new ArrayList<>();
	private Random rnd = new Random();
	public ArrayList<Integer> fruitsX = new ArrayList<>();
	public ArrayList<Integer> fruitsY = new ArrayList<>();
	public int highscore = 0;
	
	
	public Game() {
		
		for (int i = 0; i < FRUITS; i++) {
			fruitsX.add(rnd.nextInt(WIDTH));
			fruitsY.add(rnd.nextInt(HEIGHT));
		}
		
	}

	public void update() {

		move();
		checkCollisions();
		checkFruit();
		
		updateField();
		
	}
	
	private void move() {
		
		for (Player p : players) {
			if (p == null) continue;
			p.move();
		}
		
	}
	
	private void checkCollisions() {
		
		outerLoop:
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (p == null) continue;
			
			// borders
			if (	Collections.max(p.segmentsX) >= WIDTH ||
					Collections.max(p.segmentsY) >= HEIGHT ||
					Collections.min(p.segmentsX) < 0 ||
					Collections.min(p.segmentsY) < 0) {
				
				// dead
				players.set(i, null);
				continue;
				
			}
			
			// other players
			int pX = p.segmentsX.get(0);
			int pY = p.segmentsY.get(0);
			for (Player q : players) {
				if (q == null) continue;
				
				if (p == q &&
						q.segmentsX.subList(1, q.segmentsX.size()).contains(pX) &&
						q.segmentsY.subList(1, q.segmentsY.size()).contains(pY)) {
					
					// dead
					players.set(i, null);
					continue outerLoop;
					
				} else if (p != q && q.segmentsX.contains(pX) && q.segmentsY.contains(pY)) {

					// dead
					players.set(i, null);
					continue outerLoop;
					
				}
				
			}
			
		}
		
	}
	
	private void checkFruit() {
		
		for (Player p : players) {
			if (p == null) continue;
			
			for (int i = 0; i < FRUITS; i++) {
				
				if (p.segmentsX.get(0) == fruitsX.get(i) && p.segmentsY.get(0) == fruitsY.get(i)) {
					
					fruitsX.set(i, rnd.nextInt(WIDTH));
					fruitsY.set(i, rnd.nextInt(HEIGHT));
					
					p.score++;
					p.updateScore = true;
					
				}
				
			}
			
		}
		
	}
	
	private void updateField() {
		
		field = new int[WIDTH][HEIGHT];
		
		for (int i = 0; i < FRUITS; i++) {
			field[fruitsX.get(i)][fruitsY.get(i)] = 1;
		}
		
		for (Player p : players) {
			if (p == null) continue;
			
			// segments
			for (int i = 0; i < p.segmentsX.size(); i++) {
				field[p.segmentsX.get(i)][p.segmentsY.get(i)] = p.id;
			}
			
			// head
			field[p.segmentsX.get(0)][p.segmentsY.get(0)] = -p.id;
			
		}
		
	}
	
}
