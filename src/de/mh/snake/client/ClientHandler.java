package de.mh.snake.client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
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

public class ClientHandler implements KeyListener {
	
	private SnakeClient snakeClient;
	Client client = new Client(8192, 8192);
	public int id = 0;
	public int score = 0;
	public int highscore = 0;
	public boolean ingame = false;
	

	public ClientHandler(SnakeClient snakeClient) {
		this.snakeClient = snakeClient;
	}
	
	public void start() {
		
		client.start();
		try {

			Kryo kryo = client.getKryo();
			kryo.register(Request.class);
			kryo.register(Response.class);
			
			InetAddress address = client.discoverHost(54001, 5000);
			//InetAddress address = InetAddress.getByName(JOptionPane.showInputDialog("Enter server IP"));	// yeah, worldwide is possible ;)
			client.connect(5000, address, 54000, 54001);
			
			client.addListener(new Listener() {
				public void received(Connection connection, Object object) {
					if (object instanceof Response) {
						Response response = (Response)object;
						handleResponse(response.content, connection);
					}
				}
			});
			
			snakeClient.addKeyListener(this);
			
			request("getHighscore");
			request("getColors");
			
		} catch (Exception e) {
			System.out.println(e.toString());
			
			if (e.toString().contains("host cannot be null.")) {
				
				int result = JOptionPane.showOptionDialog(null, "No server found! Start server first, then click 'Retry'.", "Error", 
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, 
						new String[]{"Retry", "Cancel"}, "Retry");
				
				if (result == 0) {
					start();
				} else {
					System.exit(1);
				}
				
			}
			
		}
		
	}
	
	public void stop() {
		
		client.close();
		client.stop();

	}
	
	private void handleResponse(String content, Connection connection) {

		if (!content.startsWith("update")) System.out.println("Response: " + content);
		
		
		if (content.startsWith("setID")) {
			
			id = Integer.valueOf(content.substring(6));
			snakeClient.board.id = id;
			score = 0;
			snakeClient.board.text = "Score: " + score + " | Highscore: " + highscore;
			ingame = true;
			
		} else if (content.startsWith("update")) {
			
			String raw = content.substring(7);
			String tempY[] = raw.split(";");
			int field[][] = new int[Game.WIDTH][Game.HEIGHT];
			
			for (int y = 0; y < Game.HEIGHT; y++) {
				String tempX[] = tempY[y].split(":");
				for (int x = 0; x < Game.WIDTH; x++) {
					field[x][y] = Integer.valueOf(tempX[x]);
				}
			}
			
			// display start point
			if (!ingame) {
				field[0][0] = -9999;
			}
			
			snakeClient.board.field = field;
			snakeClient.board.repaint();
			
		} else if (content.startsWith("score")) {
			
			String temp[] = content.split(";");
			if (Integer.valueOf(temp[1]) == id) {
				score = Integer.valueOf(temp[2]);
				snakeClient.board.text = "Score: " + score + " | Highscore: " + highscore;
				snakeClient.board.repaint();
			}
			
		} else if (content.startsWith("highscore")) {
			
			highscore = Integer.valueOf(content.substring(10));
			if (ingame) {
				snakeClient.board.text = "Score: " + score + " | Highscore: " + highscore;
			} else {
				snakeClient.board.text = "Press SPACE to play. | Highscore: " + highscore;
			}
			snakeClient.board.repaint();
			
		} else if (content.startsWith("dead")) {
			
			String temp[] = content.split(";");
			if (Integer.valueOf(temp[1]) == id) {
				snakeClient.board.text = "GAME OVER! Press SPACE to replay. | Highscore: " + highscore;
				snakeClient.board.id = 9999;
				snakeClient.board.repaint();
				ingame = false;
			}
			
		} else if (content.startsWith("ban")) {
			
			snakeClient.board.text = content.substring(4);
			snakeClient.board.repaint();
			ingame = false;
			
		} else if (content.startsWith("colors")) {
		
			for (String s : content.substring(7).split(";")) {
				String temp[] = s.split(":");
				snakeClient.board.otherColor.put(Integer.valueOf(temp[0]), Color.decode(temp[1]));
			}
				
		}
		
	}
	
	private void request(String content) {
		Request request = new Request();
		request.content = content;
		client.sendTCP(request);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		int key = e.getKeyCode();
		int direction = -1;
		
		if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
			direction = 0;
			
		} else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
			direction = 1;
			
		} else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
			direction = 2;
			
		} else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
			direction = 3;
			
		} else if (key == KeyEvent.VK_SPACE && !ingame) {
			
			// wanna play
			request("getID;#" + Integer.toHexString(snakeClient.board.myColor.getRGB()).substring(2));
			
		}
		
		if (direction != -1 && ingame) {
			request("direction;" + String.valueOf(id) + ";" + String.valueOf(direction));
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

}
