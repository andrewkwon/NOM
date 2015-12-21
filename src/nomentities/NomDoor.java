package nomentities;

import level.Level;
import nomlevel.NomLevel;
import entities.Entity;
import gfx.Screen;

public class NomDoor extends Entity {
	
	public static int SENDID;
	public static int RECEIVEID;
	public String receiveLevelPath;
	public int[][] animationCoords;
	public int currentAnimationIndex = 0;
	public int animationSwitchDelay;
	public boolean opening = false;
	public long lastTime;
	public int[] tiles = new int[4];
	public int color;

	public NomDoor(Level level, int x, int y, int color, int SENDID, int RECEIVEID, String receiveLevelPath, int[][] animationCoords, int animationSwitchDelay) {
		super(level);
		this.x = x;
		this.y = y;
		this.RECEIVEID = RECEIVEID;
		this.SENDID = SENDID;
		this.receiveLevelPath = receiveLevelPath;
		this.animationCoords = animationCoords;
		this.animationSwitchDelay = animationSwitchDelay;
		this.color = color;
		lastTime = System.currentTimeMillis();
		for(int i = 0; i < tiles.length; i++) {
			tiles[i] = animationCoords[i][0] + animationCoords[i][1] * 32;
		}
		System.out.println(this.x);
		System.out.println(this.RECEIVEID);
		System.out.println(this.SENDID);
	}

	@Override
	public void update() {
		if(opening) {
			long now = System.currentTimeMillis();
			if(now - lastTime >= animationSwitchDelay) {
				lastTime = now;
				currentAnimationIndex = (currentAnimationIndex + 1) % (animationCoords.length / tiles.length);
				for(int i = 0; i < tiles.length; i++) {
					tiles[i] = animationCoords[currentAnimationIndex * tiles.length + i][0] + animationCoords[currentAnimationIndex * tiles.length + i][1] * 32;
				}
			}
			if(currentAnimationIndex == 0) {
				opening = false;
				try {
					Thread.sleep(animationSwitchDelay);
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
				send();
			}
		}
	}

	@Override
	public void render(Screen screen) {
		screen.render(x, y, tiles[0], color, 1);
		screen.render(x + 8, y, tiles[1], color, 1);
		screen.render(x, y + 8, tiles[2], color, 1);
		screen.render(x + 8, y + 8, tiles[3], color, 1);
	}
	
	public void send() {
		//TODO: add in save functionality
		NomLevel receiveLevel = new NomLevel(receiveLevelPath);
		for(Entity on : level.entities) {
			if(on instanceof Nom && !on.equals(this) && (((on.x >> 3) <= (x >> 3) && (on.x >> 3) >= (x >> 3) - 2) || ((on.y >> 3) <= (y >> 3) && (on.y >> 3) >= (y >> 3) - 1))) {
				receiverSearch : for(Entity receiver : receiveLevel.entities) {
					if(receiver instanceof NomDoor && ((NomDoor) receiver).RECEIVEID == this.SENDID) {
						on.level = receiveLevel;
						level.removeEntities(on);
						receiveLevel.addEntities(on);
						on.x = receiver.x;
						on.y = receiver.y;
						((Nom) on).spawnX = receiver.x;
						((Nom) on).spawnY = receiver.y;
						break receiverSearch;
					}
				}
			}
		}
	}
}