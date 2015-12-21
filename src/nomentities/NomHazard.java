package nomentities;

import entities.Mob;
import gfx.Screen;
import level.Level;

public class NomHazard extends Mob {

	private int[][] animationCoords;
	private int currentAnimationIndex;
	private long lastTime;
	private int animationSwitchDelay;
	private int mirrorDir;
	private int scale;
	private int color;
	public int hazardID;
	private int min;
	private int max;
	public int yDir;
	public int xDir;
	//0normal, 1electrocutes, 2burns, 3drowns, 4freezes, 5instant death
	
	public NomHazard() {
		super(null, "", 0, 0, 1);
	}
			
	public NomHazard(Level level, String name, int x, int y, int speed, int[][] animationCoords, int color, int animationSwitchDelay, int hazardID, int mirrorDir, int scale, int xDir, int yDir, int min, int max) {
		super(level, name, x, y, speed);
		this.animationCoords = animationCoords;
		this.currentAnimationIndex = currentAnimationIndex;
		this.lastTime = lastTime;
		this.animationSwitchDelay = animationSwitchDelay;
		this.x = x;
		this.y = y;
		this.hazardID = hazardID;
		this.mirrorDir = mirrorDir;
		this.color = color;
		this.scale = scale;
		this.xDir = xDir;
		this.yDir = yDir;
		this.min = min;
		this.max = max;
	}

	@Override
	public void update() {
		if(min != max) {
			if(x <= min || x >= max) xDir *= -1;
			if(y <= min || y >= max) yDir *= -1;
			move(xDir, yDir);
		}
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
