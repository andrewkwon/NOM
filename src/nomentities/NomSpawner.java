package nomentities;

import level.Level;
import entities.Entity;
import gfx.Screen;

public class NomSpawner extends Entity {

	protected NommableMob spawned;
	protected String name;
	protected int spawnX;
	protected int spawnY;
	protected int spawnDelay;
	protected int area;
	protected int[][] animationCoords;
	protected int animationSwitchDelay;
	protected int currentAnimationIndex = 0;
	protected long lastTime;
	protected long lastSpawn;
	protected int[][] placementCoords;
	protected int[] tiles;
	protected int color;
	
	public NomSpawner(Level level, String name, int x, int y, int color, int area, int[][] animationCoords, int animationSwitchDelay, int[][] placementCoords, int spawnDelay, int spawnX, int spawnY, Entity spawned) {
		super(level);
		this.name = name;
		this.x = x;
		this.y = y;
		this.color = color;
		this.area = area;
		this.animationCoords = animationCoords;
		this.animationSwitchDelay = animationSwitchDelay;
		lastTime = System.currentTimeMillis();
		lastSpawn = System.currentTimeMillis();
		this.placementCoords = placementCoords;
		this.spawnDelay = spawnDelay;
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.spawned = (NommableMob)spawned;
		this.tiles = new int[area];
	}

	@Override
	public void update() {
		long now = System.currentTimeMillis();
		if(now - lastTime >= animationSwitchDelay) {
			lastTime = now;
			currentAnimationIndex = (currentAnimationIndex + 1) % (animationCoords.length / area);
			for(int i = 0; i < area; i++) {
				tiles[i] = animationCoords[currentAnimationIndex * tiles.length + i][0] + animationCoords[currentAnimationIndex * tiles.length + i][1] * 32;
			}
		}
		boolean blocked = false;
		for(Entity e : level.entities) if(e.x == spawned.x && e.y == spawned.y) blocked = true;
		if(!blocked && now - lastSpawn >= spawnDelay) {
			lastSpawn = now;
			NommableMob Spawned = spawned.clone();
			Spawned.x = spawnX;
			Spawned.y = spawnY;
			level.addEntity(Spawned);
		}
		if(blocked) lastSpawn = now;
	}

	@Override
	public void render(Screen screen) {
		for(int i = 0; i < area; i++) {
			screen.render(x + placementCoords[i][0], y + placementCoords[i][1], tiles[i], color, 1);
		}
	}

}
