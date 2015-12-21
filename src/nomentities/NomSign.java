package nomentities;

import level.Level;
import entities.Entity;
import gfx.ColorData;
import gfx.Font;
import gfx.Screen;

public class NomSign extends Entity {
	
	private String msg;
	private int tileIndex;
	private int fieldColor;
	private int textColor;
	private int bgIndex;
	private int bgColor;
	
	public NomSign(Level level, int x, int y, int tileIndex, int fieldColor, int textColor, int bgIndex, int bgColor, String msg) {
		super(level);
		this.x = x;
		this.y = y;
		this.tileIndex = tileIndex;
		this.msg = msg;
		this.fieldColor = fieldColor;
		this.textColor = textColor;
		this.bgIndex = bgIndex;
		this.bgColor = bgColor;
	}

	@Override
	public void update() {
	}

	@Override
	public void render(Screen screen) {
		screen.render(x, y, tileIndex, fieldColor, 1);
	}
	
	public void renderMsg(Screen screen) {
		for(int m = 0; m < screen.width; m++) {
			for(int n = 0; n < screen.height; n++) {
				screen.render(m * 8, n * 8, bgIndex, bgColor, 1);
			}
		}
		Font.renderLines(msg, screen, screen.xOffset, screen.yOffset, ColorData.getColorData(-1, -1, -1, textColor), 1);
	}
}
