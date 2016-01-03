package nomgame;

import game.Game;
import gfx.ColorData;
import gfx.Font;
import gfx.Screen;
import gfx.SpriteSheet;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import level.tiles.AnimatedTile;
import level.tiles.BasicSolidTile;
import level.tiles.BasicTile;
import level.tiles.Tile;
import nomentities.Nom;
import nomentities.NomHazard;
import nomentities.NomSign;
import nomentities.NommableMob;
import nomlevel.NomLevel;

//This is a comment I added while experimenting with git branches

public class NomGame extends Game {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Nom nom;
	private static final Tile[] tiles = {
		/*1SKY*/ new BasicTile(1, 0, 0, ColorData.getColorData(125, -1, -1, -1), 0xFF00AAFF),
		/*2GRASS*/ new BasicSolidTile(2, 1, 0, ColorData.getColorData(110, 051, -1, -1), 0xFF00FF00),
		/*3WATERSHALLOW*/ new AnimatedTile(3, new int[][] {{2, 0}, {3, 0}, {4, 0}, {3, 0}}, ColorData.getColorData(005, 115, -1, 125), 0xFF1111FF, 500),
		/*4WATERDEEP*/ new BasicTile(4, 0, 0, ColorData.getColorData(005, -1, -1, -1), 0xFF0000FF),
		/*5POLLUTEDWATERSHALLOW*/ new AnimatedTile(5, new int[][] {{2, 0}, {3, 0}, {4, 0}, {3, 0}}, ColorData.getColorData(213, 323, -1, 125), 0xFF7744AA, 500),
		/*6POLLUTEDWATERDEEP*/ new BasicTile(6, 0, 0, ColorData.getColorData(213, -1, -1, -1), 0xFF663399),
		/*7PEPPERMINT*/ new BasicSolidTile(7, 21, 0, ColorData.getColorData(555, 500, -1, -1), 0xFFFFAAAA),
		/*8SHINYLEMONCANDY*/ new AnimatedTile(8, new int[][] {{21, 0}, {22, 0}}, ColorData.getColorData(550, 553, -1, -1), 0xFFFFFF00, 300),
		/*9MNM*/ new BasicSolidTile(9, 23, 0, ColorData.getColorData(125, 110, 221, 555), 0xFF222200)
	};
	
	public NomGame(String name, String iconPath) {
		super(name, iconPath);
		width = 200;
		height = width * 9 / 12;
		scale = 3;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		frame.setMinimumSize(new Dimension(width * scale, height * scale));
		frame.setMaximumSize(new Dimension(width * scale, height * scale));
		frame.setPreferredSize(new Dimension(width * scale, height * scale));
		frame.setResizable(false);
		input = new NomInputHandler();
		this.addKeyListener(input);
		frame.setVisible(true);
		init();
	}
	
	@Override
	public void init() {
		screen = new Screen(width, height, new SpriteSheet("/NomSheet.png"));
		int index = 0;
		for(int r = 0; r < 6; r++) {
			for(int g = 0; g < 6; g++) {
				for(int b = 0; b < 6; b++) {
					int rr = r * 255 / 5;
					int gg = g * 255 / 5;
					int bb = b * 255 / 5;
					colors[index++] = rr << 16 | gg << 8 | bb << 0;
				}
			}
		}
		//Tiles:
		Tile.tiles[8].setSolid(true);
		level = new NomLevel("/levels/Title.png");
		nom = new Nom(level, 8 * 8, 17 * 8, input, ColorData.getColorData(-1, 000, 555, 555), 0);
		level.addEntity(nom);
	}
	
	@Override
	public void run() {
		int updates = 0;
		int frames = 0;
		long lastTime = System.nanoTime();
		long lastCountReset = System.currentTimeMillis();
		double updatesGoneBy = 0;
		double updateRate = 60D/1000000000D;
		while(true) {
			long now = System.nanoTime();
			updatesGoneBy += (now - lastTime) * updateRate;
			lastTime = now;
			while(updatesGoneBy >= 1) {
				update();
				updates++;
				updatesGoneBy--;
			}
			try {
				Thread.yield();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			render();
			frames++;
			if(System.currentTimeMillis() - lastCountReset >= 1000) {
				lastCountReset = System.currentTimeMillis();
				System.out.println("updates: " + updates + " frames: " + frames);
				updates = 0;
				frames = 0;
			}
		}
	}
	
	@Override
	public void update() {
		if(!level.equals(nom.level)) level = nom.level;
		level.update();
	}
	
	private int x, y = 0;
	
	@Override
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		if(!nom.hurting) {
			x = nom.x;
			y = nom.y;
		}
		if(nom.lives > 0) {
			if(nom.reading == null) {
				level.renderTiles(screen, x - screen.width / 2, y - screen.height / 2);
				level.renderEntities(screen);
				renderNomStatus();
			}
			else nom.reading.renderMsg(screen);
		}
		for(int y = 0; y < screen.height; y++) {
			for(int x = 0; x < screen.width; x++) {
				int colcode = screen.pixels[x + y * screen.width];
				if(colcode < 255) pixels[x + y * width] = colors[colcode];
			}
		}
		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}
	
	public void renderNomStatus() {
		int color = ColorData.originalColorData((nom.color >> 16) & 255);
		if((nom.sugar / (nom.sugarMax / 100)) < 8) color = 000 + ((nom.updateCount >> 3) % 5) * 100;
		screen.render(screen.xOffset + 2, screen.yOffset + 2, 13 + 28 * 32, nom.color, 1);
		Font.render("X" + nom.lives, screen, screen.xOffset + 10, screen.yOffset + 10, ColorData.getColorData(-1, -1, -1, 000), 1);
		int barBroke = 0;
		if(nom.sugar < 0) barBroke = 1;
		screen.render(screen.xOffset + 19, screen.yOffset + 2, 12 + (28 + barBroke) * 32, ColorData.getColorData(-1, ((color + 255) >> 1) & 255, -1, -1), 1);
		int barRenderProgress = 8;
		while(barRenderProgress + 7 < nom.sugarMax / (nom.sugarMax / 100)) {
			screen.render(screen.xOffset + barRenderProgress + 19, screen.yOffset + 2, 12 + 29 * 32, ColorData.getColorData(-1, ((color + 255) >> 1) & 255, -1, -1), 1);
			barRenderProgress += 8;
		}
		screen.render(screen.xOffset + barRenderProgress + 20 - (nom.sugarMax / 1000 - barRenderProgress), screen.yOffset + 2, 12 + 28 * 32, ColorData.getColorData(-1, ((color + 255) >> 1) & 255, -1, -1), 01, 1);
		barRenderProgress = 0;
		while(barRenderProgress + 7 < nom.sugar / (nom.sugarMax / 100)) {
			screen.render(screen.xOffset + barRenderProgress + 20, screen.yOffset + 2, 13 + 29 * 32, ColorData.getColorData(-1, color, -1, -1), 1);
			barRenderProgress += 8;
		}
		screen.render(screen.xOffset + barRenderProgress + 20 + ((nom.sugar / (nom.sugarMax / 100) - 9) - barRenderProgress), screen.yOffset + 2, 13 + 29 * 32, ColorData.getColorData(-1, color, -1, -1), 1);
		screen.render(screen.xOffset + 122, screen.yOffset + 2, 11 + 29 * 32, ColorData.getColorData(-1, color, -1, -1), 2, 1);
		screen.render(screen.xOffset + 122 + 8, screen.yOffset + 2, 11 + 29 * 32, ColorData.getColorData(-1, color, -1, -1), 3, 1);
		screen.render(screen.xOffset + 122, screen.yOffset + 2 + 8, 11 + 29 * 32, ColorData.getColorData(-1, color, -1, -1), 00, 1);
		screen.render(screen.xOffset + 122 + 8, screen.yOffset + 2 + 8, 11 + 29 * 32, ColorData.getColorData(-1, color, -1, -1), 01, 1);
		if(nom.inMouth != null) {
			int inMouthX = nom.inMouth.x;
			int inMouthY = nom.inMouth.y;
			nom.inMouth.x = screen.xOffset + 122 + 4;
			nom.inMouth.y = screen.yOffset + 2 + 4;
			nom.inMouth.render(screen);
			nom.inMouth.x = inMouthX;
			nom.inMouth.y = inMouthY;
			Font.render(nom.inMouth.getName(), screen, screen.xOffset + 40, screen.yOffset + 10, ColorData.getColorData(-1, -1, -1, ColorData.originalColorData((nom.inMouth.color >> 16) & 255)), 1);
			Font.render("X" + nom.numInMouth, screen, screen.xOffset + 160, screen.yOffset + 2, ColorData.getColorData(-1, -1, -1, ColorData.originalColorData((nom.inMouth.color >> 16) & 255)), 1);
		}
		screen.render(screen.xOffset + 140, screen.yOffset + 2, 11 + 28 * 32, ColorData.getColorData(-1, color, -1, -1), 2, 1);
		screen.render(screen.xOffset + 140 + 8, screen.yOffset + 2, 11 + 28 * 32, ColorData.getColorData(-1, color, -1, -1), 3, 1);
		screen.render(screen.xOffset + 140, screen.yOffset + 2 + 8, 11 + 28 * 32, ColorData.getColorData(-1, color, -1, -1), 00, 1);
		screen.render(screen.xOffset + 140 + 8, screen.yOffset + 2 + 8, 11 + 28 * 32, ColorData.getColorData(-1, color, -1, -1), 01, 1);
		if(nom.inStomach != null) {
			nom.inStomach.x = screen.xOffset + 140 + 4;
			nom.inStomach.y = screen.yOffset + 2 + 4;
			nom.inStomach.render(screen);
		}
	}
	
	public static void main(String[] args) {
		new Thread(new NomGame("NOM: An adventure that doesn't need a subtitle", "res/NOMIcon.png")).run();
	}
}
