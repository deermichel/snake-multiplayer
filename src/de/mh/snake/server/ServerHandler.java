package de.mh.snake.server;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.Timer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import de.mh.snake.Request;
import de.mh.snake.Response;

public class ServerHandler implements ActionListener {
	
	private SnakeServer snakeServer;
	private Server server;
	private Game game;
	private ArrayList<Integer> deadIds = new ArrayList<>();
	public Timer timer;
	

	public ServerHandler(SnakeServer snakeServer) {
		this.snakeServer = snakeServer;
	}
	
	public void start() {
		
		server = new Server(8192, 8192);
		server.start();
		try {
			
			server.bind(54000, 54001);
			snakeServer.buttonStart.setEnabled(false);
			snakeServer.buttonStop.setEnabled(true);
			log("Server started");
			
			Kryo kryo = server.getKryo();
			kryo.register(Request.class);
			kryo.register(Response.class);
			
			server.addListener(new Listener() {
				public void received(Connection connection, Object object) {
					if (object instanceof Request) {
						Request request = (Request)object;
						handleRequest(request.content, connection);
					}
				}
			});
			
			game = new Game();
			timer = new Timer(Game.TICK, this);
			timer.start();
			log("Game created");
			
		} catch (Exception e) {
			log(e.toString());
		}
		
	}
	
	public void stop() {
		
		server.stop();
		timer.stop();
		snakeServer.buttonStart.setEnabled(true);
		snakeServer.buttonStop.setEnabled(false);
		log("Server stopped");

	}
	
	private void log(String message) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        snakeServer.textLog.setText(snakeServer.textLog.getText() + sdf.format(new Date()) + " " + message + "\n");
        
	}
	
	public void handleCommand(String command) {
		
		/** --- COMMANDS ---
		 * 
		 * freeze;[id]
		 * slowdown;[id];[steps]
		 * kamikaze;[id]
		 * 
		 */
		
		log("Command: " + command);
		
		if (command.startsWith("freeze")) {
			
			Player player = game.players.get(Integer.valueOf(command.substring(7)) - 2);
			if (player == null) return;
			player.freeze = !player.freeze;
			
		} else if (command.startsWith("slowdown")) {
			
			String temp[] = command.split(";");
			Player player = game.players.get(Integer.valueOf(temp[1]) - 2);
			if (player == null) return;
			player.steps = Integer.valueOf(temp[2]);
			
		} else if (command.startsWith("kamikaze")) {
			
			Player player = game.players.get(Integer.valueOf(command.substring(9)) - 2);
			if (player == null) return;
			player.kamikaze = player.direction;
			
		}
		
	}
	
	private void handleRequest(String content, Connection connection) {
		
		log("Request: " + content);
		
		/** --- COMMANDS ---
		 * 
		 * getID;[color]
		 * setID;[id]
		 * 
		 * direction;[id];[direction]
		 * 
		 * update;[field]
		 * score;[id];[score]
		 * dead;[id]
		 * highscore;[highscore]
		 * getHighscore
		 * 
		 * dead;[id]
		 * 
		 */
		
		if (content.startsWith("getID")) {
			
			// begin with 2 ( -> 0 nothing 1 fruit -1 border)
			
			Player newPlayer = new Player(game.players.size() + 2, Color.BLACK);
			game.players.add(newPlayer);
			respond("setID;" + newPlayer.id, connection);
			
		} else if (content.startsWith("direction")) {
			
			String temp[] = content.split(";");
			Player player = game.players.get(Integer.valueOf(temp[1]) - 2);
			if (player == null) return;
			player.direction = Integer.valueOf(temp[2]);
			
		} else if (content.startsWith("getHighscore")) {
			
			respond("highscore;" + game.highscore, connection);
			
		}
		
	}
	
	private void respond(String content, Connection connection) {
		Response response = new Response();
		response.content = content;
		connection.sendTCP(response);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		// update
		game.update();
		
		// send to clients
		String raw = "";
		for (int y = 0; y < Game.HEIGHT; y++) {
			for (int x = 0; x < Game.WIDTH; x++) {
				raw += String.valueOf(game.field[x][y]) + ":";
			}
			raw = raw.substring(0, raw.length() - 1);
			raw += ";";
		}
		raw = raw.substring(0, raw.length() - 1);
		
		
		Response response = new Response();
		response.content = "update;" + raw;
		server.sendToAllTCP(response);
		
		
		// scores and dead updates
		for (int i = 0; i < game.players.size(); i++) {
			Player p = game.players.get(i);
			
			if (p != null && p.updateScore) {
				response = new Response();
				response.content = "score;" + p.id + ";" + p.score;
				server.sendToAllTCP(response);
				p.updateScore = false;
				
				if (p.score > game.highscore) {
					game.highscore = p.score;
					response = new Response();
					response.content = "highscore;" + game.highscore;
					server.sendToAllTCP(response);
				}
				
			} else if (p == null && !deadIds.contains(i + 2)) {
				deadIds.add(i + 2);
				response = new Response();
				response.content = "dead;" + (i + 2);
				server.sendToAllTCP(response);
			}
			
		}
		
	}

}
