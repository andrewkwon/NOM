package nomentities;

import level.Level;
import level.tiles.Tile;
import entities.Mob;
import gfx.Screen;

public class NommableMob extends Mob {
	
	public NommableMob whenChewed;
	public int numWhenChewed;
	public int spitDelay;
	public int calories;
	protected int[][] animationCoords;
	protected int currentAnimationIndex;
	protected long lastTime;
	protected int animationSwitchDelay;
	public int mirrorDir;
	protected int scale;
	public int color;
	public int numInField;

	public NommableMob(Level level, String name, int x, int y, int speed, int numInField, int spitDelay, int[][] animationCoords, int color, int animationSwitchDelay, int mirrorDir, int scale, int direction, int xMin, int xMax, int yMin, int yMax, int numWhenChewed, int calories, NommableMob whenChewed) {
		super(level, name, x, y, speed);
		this.animationCoords = animationCoords;
		this.currentAnimationIndex = currentAnimationIndex;
		this.lastTime = lastTime;
		this.animationSwitchDelay = animationSwitchDelay;
		this.x = x;
		this.y = y;
		this.mirrorDir = mirrorDir;
		this.color = color;
		this.scale = scale;
		this.direction = direction;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.spitDelay = spitDelay;
		this.numWhenChewed = numWhenChewed;
		this.calories = calories;
		this.whenChewed = whenChewed;
		this.numInField = numInField;
	}
	
	public NommableMob(NommableMob n) {
		super(n.level, n.name, n.x, n.y, n.speed);
		this.animationCoords = n.animationCoords;
		this.currentAnimationIndex = n.currentAnimationIndex;
		this.lastTime = n.lastTime;
		this.animationSwitchDelay = n.animationSwitchDelay;
		this.x = n.x;
		this.y = n.y;
		this.mirrorDir = n.mirrorDir;
		this.color = n.color;
		this.scale = n.scale;
		this.direction = n.direction;
		this.xMin = n.xMin;
		this.xMax = n.xMax;
		this.yMin = n.yMin;
		this.yMax = n.yMax;
		this.spitDelay = n.spitDelay;
		this.numWhenChewed = n.numWhenChewed;
		this.calories = n.calories;
		this.whenChewed = n.whenChewed;
		this.numInField = n.numInField;
	}
	
	public NommableMob clone() {
		return new NommableMob(this);
	}

	@Override
	public void update() {
		if(direction == 0) move(0, -1);
		if(direction == 1) move(0, 1);
		if(direction == 2) move(-1, 0);
		if(direction == 3) move(1, 0);
		long now = System.currentTimeMillis();
		if(now - lastTime >= animationSwitchDelay) {
			lastTime = now;
			currentAnimationIndex = (currentAnimationIndex + 1) % animationCoords.length;
		}
	}

	@Override
	public void render(Screen screen) {
		screen.render(x, y, animationCoords[currentAnimationIndex][0] + animationCoords[currentAnimationIndex][1] * 32, color, mirrorDir, scale);
	}	
}
