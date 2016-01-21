package de.mh.snake.server;

import java.util.ArrayList;

public class Player {

	public int id;
	public int direction = 0;
	public int score = 0;
	public boolean updateScore = false;
	public boolean freeze = false;
	public int kamikaze = -1;
	public int steps = 1;
	public int step = 0;
	public String color;
	public ArrayList<Integer> segmentsX = new ArrayList<>();
	public ArrayList<Integer> segmentsY = new ArrayList<>();
	
	
	public Player(int id, String color) {
		
		this.id = id;
		this.color = color;
		
		segmentsX.add(0);
		segmentsY.add(0);
		
	}
	
	public void move() {
		
		// server commands
		if (freeze) return;
		
		step++;
		if (step >= steps) {
			step = 0;
		} else {
			return;
		}
		
		if (kamikaze > -1) direction = kamikaze;
		
		
		// save last segment (to add a new one)
		int lastX = segmentsX.get(segmentsX.size()-1);
		int lastY = segmentsY.get(segmentsY.size()-1);
		
		// move segments
		for (int i = segmentsX.size() - 1; i > 0; i--) {
			
			segmentsX.set(i, segmentsX.get(i-1));
			segmentsY.set(i, segmentsY.get(i-1));
			
		}

		// move head in direction
		switch (direction) {
		
		// RIGHT
		case 0:
			segmentsX.set(0, segmentsX.get(0) + 1);
			break;
			
		// UP
		case 1:
			segmentsY.set(0, segmentsY.get(0) - 1);
			break;
			
		// LEFT
		case 2:
			segmentsX.set(0, segmentsX.get(0) - 1);
			break;
			
		// DOWN
		case 3:
			segmentsY.set(0, segmentsY.get(0) + 1);
			break;
		
		}
		
		// add segment
		if (score >= segmentsX.size()) {
			segmentsX.add(lastX);
			segmentsY.add(lastY);
		}

		// System.out.println(segmentsX + " " + segmentsY);
		
	}
	
}
