## snake-multiplayer

This game originated from a school project. Finally, it became a great multiplayer snake that could even be played globally. Apart from that, it features solids, server commands :), an alternative gamemode and a hip retro design. ***Give it a try!***

### Instructions

Import or clone the source into Eclipse (or your fav IDE) and make sure you have at least Java 7 installed. Alternatively, hurried people can just download the two JAR's under [releases](https://github.com/DeerMichel/snake-multiplayer/releases). First start the server, then head over to the client. Press SPACE to start playing and control your snake with WASD or the arrow keys. Share the client JAR with anyone in your LAN and it will detect and join your server automatically. If you for any reasons need a specific server IP input, uncomment the line ```InetAddress address = InetAddress.getByName(JOptionPane.showInputDialog("Enter server IP"));``` and comment out ```InetAddress address = client.discoverHost(54001, 5000);``` in [this](https://github.com/DeerMichel/snake-multiplayer/blob/master/src/de/mh/snake/client/ClientHandler.java) file. Recompile the client and enjoy! If you prefer being a 'global player' *(insert laugh here)*, make sure to open the ports 54000 (TCP) and 54001 (UDP) at your router.

### Server commands

	freeze;[id]				// freezes player [id]
	slowdown;[id];[steps]	// slows player [id] down by [steps] (default: 1)
	kamikaze;[id]			// ignores direction changes of player [id]
	ban;[ip]				// bans [ip]
	unban;[ip]				// unbans [ip]
	score;[id];[score]		// sets score of player [id] to [score]
	speed;[tick]			// changes game update speed to [tick] ms (default: 100)
	gamemode;[mode]			// changes game mode to [mode] (default: 0)
	addsolid;[x];[y]		// adds solid at [x], [y]
	remsolid;[x];[y]		// removes solid at [x], [y]
	clearsolids				// removes all solids
	
#### Gamemodes

	0	classic snake game
	1	dead players become solids (recommended :D)

### Third-party

* [kryonet](https://github.com/EsotericSoftware/kryonet) ([License](https://github.com/EsotericSoftware/kryonet/blob/master/license.txt))