package nomentities;

import level.Level;
import nomgame.NomInputHandler;
import nomlevel.NomLevel;
import entities.Entity;
import game.InputHandler;
import gfx.ColorData;
import gfx.Screen;

public class Nom extends NommableMob {

	public boolean dying = false;
	public boolean electrocuted = false;
	public boolean burning = false;
	public boolean drowning = false;
	public boolean hurting = false;
	public boolean frozen = false;
	private int jumpProgress = 0;
	private boolean jumpPeaked = false;
	private boolean jumping = false;
	public int updateCount;
	public NommableMob inMouth;
	public int numInMouth;
	public NommableMob inStomach;
	public int numInStomach;
	public static int sugar = 100000;
	public static final int sugarMax = sugar;
	public int sugarConsumptionRate = 1;
	public int naturalSCR = sugarConsumptionRate;
	protected NomInputHandler input;
	public int lives = 3;
	public boolean gravitates = true;
	public boolean editMode = false;
	public Entity on;
	private long lastChew = -1L;
	private long lastGulp = -1L;
	public NomSign reading = null;
	private boolean spitting;
	private int yDir = 0;
	private int xDir = 1;
	public int spawnX;
	public int spawnY;
	
	public Nom(Level level, int x, int y, InputHandler input, int color, int index) {
		super(level, "NOM", x, y, 1, 1, 0, null, color, 0, 00, 1, -1, 2, 9, -7, 6, 1, sugarMax, null);
		//Level level, String name, int x, int y, int speed, int spitDelay, int[][] animationCoords, int color, int animationSwitchDelay, int mirrorDir, int scale, int direction, int xMin, int xMax, int yMin, int yMax, int numWhenChewed, NommableMob whenChewed
		this.input = (NomInputHandler) input;
		spitting = true;
		spawnX = x;
		spawnY = y;
	}

	@Override
	public void update() {
		if(!input.Read.pressed) reading = null;
		drowning = false;
		yDir = 0;
		int xa = 0;
		int ya = 0;
		if(jumpProgress >= 16 && gravitates) jumpPeaked = true;
		if(input.up.pressed && !jumpPeaked) {
			ya-=2;
			jumpProgress ++;
			jumping = true;
		}
		else if(!input.up.pressed && jumping && !jumpPeaked && gravitates) {
			ya -= 2;
			jumpProgress ++;
		}
		if(input.down.pressed) ya++;
		if(input.left.pressed) xa--;
		if(input.right.pressed) xa++;
		if(!hasCollided(0, ya + 1) && gravitates) ya++;
		if(!hasCollided(0, ya + 4) && drowning) ya += 4;
		if(frozen) {
			xa = 0;
			if(level.getTile(x >> 3, (y >> 3)).getID() == 4 || level.getTile(x >> 3, (y >> 3)).getID() == 6) ya--;
		}
		if(on != null && on instanceof NomHazard && ((NomHazard) on).hazardID == 3) {
			xa += ((NomHazard) on).xDir;
			ya += ((NomHazard) on).yDir;
		}
		if((xa != 0 || ya != 0) && reading == null) {
			move(xa, ya);
		}
		else moving = false;
		if(hasCollided(0, ya)) {
			jumpPeaked = false;
			jumping = false;
			jumpProgress = 0;
		}
		hazardCheck: for(Entity e : level.entities) {
			if((e.x >> 3) == (x >> 3) + 1 && ((e.y >> 3) == (y >> 3) || (e.y >> 3) == (y >> 3) - 1)) {
				on = e;
				if(on instanceof NomHazard && !editMode) {
					if(((NomHazard) on).hazardID == 0 && reading == null) {
						sugar -= sugarConsumptionRate * sugarMax / 100;
						move(-4 * xa, -4 * ya);
						hurting = true;
						break hazardCheck;
					}
					if(((NomHazard) on).hazardID == 1) {
						sugar -= sugarConsumptionRate * sugarMax / 100;
						electrocuted = true;
						yDir = 2;
						moving = true;
						break hazardCheck;
					}
					else electrocuted = false;
					if(!burning && ((NomHazard) on).hazardID == 2) {
						burning = true;
						frozen = false;
						sugarConsumptionRate = naturalSCR * 2;
						break hazardCheck;
					}
					if(!drowning && ((NomHazard) on).hazardID == 3) {
						drowning = true;
						burning = false;
						sugarConsumptionRate = naturalSCR * 3;
						break hazardCheck;
					}
					if(!burning && ((NomHazard) on).hazardID == 4) {
						sugar -= sugarConsumptionRate * sugarMax / 100;
						sugarConsumptionRate = -1;
						frozen = true;
						yDir = 2;
						moving = true;
						break hazardCheck;
					}
					else frozen = false;
					if(((NomHazard) on).hazardID == 5) {
						sugar = 0;
					}
				}
				else if(on instanceof NomSign) {
					if(input.Read.pressed) {
						reading = ((NomSign) on);
					}
				}
				if(on instanceof NommableMob) {
					if(input.Mouth.pressed && inMouth == null && updateCount > 60 && ((on.x >> 3) >= (x >> 3) - 1 && (on.x >> 3) <= (x >> 3) + 1) && ((on.y >> 3) >= (y >> 3) - 1 && (on.y >> 3) <= (y >> 3) + 1)) {
						inMouth = (NommableMob) on;
						numInMouth = ((NommableMob)on).numInField;
						level.removeEntities(on);
						updateCount = 0;
						spitting = false;
						break;
					}
				}
			}
			if(((e.x >> 3) <= (x >> 3)  + 2 && (e.x >> 3) >= (x >> 3)) || ((e.y >> 3) <= (y >> 3) + 1 && (e.y >> 3) >= (y >> 3))) {
				on = e;
				if(on instanceof NomDoor) {
					if(input.ToggleMode.pressed) ((NomDoor) on).opening = true;
				}
			}
			else on = null;
		}
		if(!drowning && (level.getTile(x >> 3, (y >> 3)).getID() == 4 || level.getTile(x >> 3, (y >> 3)).getID() == 6)) {
			if(frozen) {
				jumpPeaked = false;
				jumpProgress = 0;
				jumping = false;
			}
			else{
				drowning = true;
				sugarConsumptionRate = sugarMax / 10;
			}
		}
		if(input.Mouth.pressed && inMouth != null && updateCount > inMouth.spitDelay) {
			NommableMob m = new NommableMob(inMouth);
			m.x = x + xa * 8;
			m.y = y - 8;
			m.direction = xDir + 1;
			if(xDir == 2 && yDir != 1) m.direction --;
			if(xDir == 1 && yDir != 1) m.direction ++;
			m.mirrorDir = xDir - 1;
			if(yDir == 1) {
				m.y += 8;
				m.mirrorDir ++;
			}
			m.numInField = 1;
			level.addEntity(m);
			updateCount  = 0;
			numInMouth--;
			if(numInMouth <= 0) inMouth = null;
			spitting = true;
		}
		if(input.Digest.pressed && inMouth != null && !input.Alter.pressed) {
			if(inMouth.whenChewed != null) {
				if(lastChew > inMouth.spitDelay) {
					numInMouth *= inMouth.numWhenChewed;
					inMouth = inMouth.whenChewed;
					lastChew = -1;
				}
				else if(lastChew == -1) lastChew = 0;
				else lastChew++;
			}
		}
		if(input.Digest.pressed && input.Alter.pressed) {
			if((inStomach == null && lastGulp > 60) || (inStomach != null && lastGulp > inStomach.spitDelay)) {
				if(inStomach != null) {
					sugar += inStomach.calories * numInStomach;
					if(sugar > sugarMax) sugar = sugarMax;
				}
				inStomach = inMouth;
				numInStomach  = numInMouth;
				inMouth = null;
				numInMouth = 0;
				lastGulp = -1;
			}
			else if (lastGulp == -1) lastGulp = 0;
			else lastGulp++;
		}
		if(input.Restart.pressed) sugar -= sugarMax;
		if(sugar <= 0 && !editMode) dying = true;
		updateCount++;
	}
	
	@Override
	public void move(int xa, int ya) {
		if(xa != 0 && ya != 0) {
			move(xa, 0);
			move(0, ya);
			numSteps--;
			return;
		}
		numSteps++;
		if((!hasCollided(xa, ya) && !dying && !electrocuted) || editMode) {
			if(ya < 0) yDir = 2;
			if(ya > 0 && !jumping) yDir = 1;
			if(xa < 0) xDir = 2;
			if(xa > 0) xDir = 1;
			x += xa * speed;
			y += ya * speed;
		}
		sugar -= sugarConsumptionRate;
		moving = true;
	}

	@Override
	public void render(Screen screen) {
		int col = color;
		int xTile = 0;
		int yTile = 28;
		int modifier = 8 * scale;
		int xOffset = x - modifier / 4;
		int yOffset = y - modifier / 2 - 4;
		int walkingSpeed = 3;
		int flipTop = 0;
		int flipBottom = (numSteps >> walkingSpeed) & 1;
		if(moving && yDir == 0) {
			xTile += flipBottom * 2;
			flipBottom = (numSteps >> (walkingSpeed + 1)) & 1;
		}
		if(moving && yDir > 0) {
			flipBottom = xDir - 1;
			if(yDir == 2) xTile += 4;
			if(yDir == 1) xTile += 6;
		}
		flipTop = xDir - 1;
		if(dying) {
			if(((updateCount & 511) >> 7) <= 2) col = ColorData.getColorData(-1, ColorData.originalColorData((color >> 24) & 255), -1, -1);
			else if(((updateCount & 511) >> 7) == 3) 	col = ColorData.getColorData(-1, -1, -1, -1);
			if((updateCount & 511) >= 500) {	
				lives --;
				if(lives == 0) level.removeEntities(this);
				x = spawnX;
				y = spawnY;
				sugar = sugarMax;
				sugarConsumptionRate = naturalSCR;
				hurting = false;
				burning = false;
				drowning = false;
				electrocuted  = false;
				frozen = false;
				dying = false;
			}
		}
		else if(hurting) {
			if(((updateCount & 31) >> 3) == 1) col = ColorData.getColorData(-1, 500, 050, 005);
			else if(((updateCount & 31) >> 3) == 2) col = ColorData.getColorData(-1, 005, 050, 500);
			else if(((updateCount & 31) >> 3) == 3) col = ColorData.getColorData(-1, 500, 050, 005);
			else if(((updateCount & 31) >> 3) == 4) col = ColorData.getColorData(-1, 005, 050, 500);
			if((updateCount & 31) >= 25) hurting = false;
		}
		else if(electrocuted) {
			if(((updateCount & 63) >> 5) == 1) col = ColorData.getColorData(-1, 555, 000, 555);
			else col = ColorData.getColorData(-1, 000, 555, 000);
		}
		else if(burning) col = ColorData.getColorData(-1, ColorData.originalColorData(((color >> 16) & 255)), 000, 000);
		else if(frozen) {
			col = ColorData.getColorData(455, ColorData.originalColorData((color >> 16) & 255) + 112, 445, 445);
			xTile = 4;
			flipBottom = 00;
			if ((level.getTile(x >> 3, (y >> 3)).getID() == 4 || level.getTile(x >> 3, (y >> 3)).getID() == 6)) {
				if(((updateCount & 63) >> 4) == 1) yOffset ++;
				else if(((updateCount & 63) >> 4) >= 3) yOffset--;
			}
		}
		if(input.Mouth.pressed && !spitting && !frozen) {
			flipBottom = flipTop;
			xTile = 9;
			yTile = 28;
			screen.render(xOffset + modifier * flipTop, yOffset, xTile + yTile * 32, col, flipTop, scale);
			screen.render(xOffset + modifier - modifier * flipTop, yOffset, (xTile + 1) + yTile * 32, col, flipTop, scale);
			screen.render(xOffset + modifier * flipBottom, yOffset + modifier, (xTile) + (yTile + 1) * 32, col, flipBottom, scale);
			screen.render(xOffset + modifier - modifier * flipBottom, yOffset + modifier, (xTile + 1) + (yTile + 1) * 32, col, flipBottom, scale);
			if(updateCount > 30) spitting = true;
		}
		else {
			screen.render(xOffset + modifier * flipTop, yOffset, xTile + yTile * 32, col, flipTop, scale);
			screen.render(xOffset + modifier - modifier * flipTop, yOffset, (xTile + 1) + yTile * 32, col, flipTop, scale);
			screen.render(xOffset + modifier * flipBottom, yOffset + modifier, (xTile) + (yTile + 1) * 32, col, flipBottom, scale);
			screen.render(xOffset + modifier - modifier * flipBottom, yOffset + modifier, (xTile + 1) + (yTile + 1) * 32, col, flipBottom, scale);
		}
		if(input.Mouth.pressed && spitting && !frozen) {
			if(yDir == 1) {
				if(flipTop == 0) flipTop = 1;
				else if(flipTop == 1) flipTop = 0;
				screen.render(xOffset + modifier - modifier * flipTop, yOffset + 2, 8 + 28 * 32, col, flipTop, scale);
			}
			else {
				screen.render(xOffset + modifier - modifier * flipTop, yOffset, 8 + 28 * 32, col, flipTop, scale);
			}
		}
		if(input.Digest.pressed && inMouth != null && !input.Alter.pressed && !frozen) {
			yTile = 28 + ((updateCount % 32) >> 4);
			if(yDir == 1) {
				if(flipTop == 0) flipTop = 1;
				else if(flipTop == 1) flipTop = 0;
				screen.render(xOffset + modifier - modifier * flipTop, yOffset, 8 + 28 * 32, col, flipTop, scale);
			}
			screen.render(xOffset + modifier - modifier * flipTop, yOffset, 8 + yTile * 32, col, flipTop, scale);
		}
		if(input.Digest.pressed && input.Alter.pressed) {
			yOffset = yOffset + ((updateCount % 32) >> 3);
			if(yDir == 1) {
				if(flipTop == 0) flipTop = 1;
				else if(flipTop == 1) flipTop = 0;
				screen.render(xOffset + modifier - modifier * flipTop, yOffset, 8 + 29 * 32, col, flipTop, scale);
			}
			screen.render(xOffset + modifier - modifier * flipTop, yOffset, 8 + 29 * 32, col, flipTop, scale);
		}
	}
}