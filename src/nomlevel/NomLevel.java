package nomlevel;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import level.Level;
import nomentities.NomDoor;
import nomentities.NomHazard;
import nomentities.NomSign;
import nomentities.NomSpawner;
import nomentities.NommableMob;
import entities.Entity;

public class NomLevel extends Level {

	public File entityFile;
	public String entityFilePath;
	
	public NomLevel(String imagePath) {
		super(imagePath);
	}
	
	protected void loadLevelViaFile() {
		try {
			this.image = ImageIO.read(Level.class.getResource(this.imagePath));
			this.width = image.getWidth();
			this.height = image.getHeight();
			tiles = new byte[width * height];
			loadTiles();
			entityFilePath = imagePath.replaceFirst(".png", "Entities.txt");
			entityFilePath.trim();
			try {
				entityFile = new File((NomLevel.class.getResource(entityFilePath)).toURI());
				//entityFile = new File("C:/Users/Andy/workspace/NOM/res/levels/TestLevelEntities.txt");
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			loadEntities();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected void loadEntities() {
		Scanner entityLoader = null;
		try {
			entityLoader = new Scanner(entityFile);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		String type = "";
		while(entityLoader.hasNext()) {
			type = entityLoader.next();
			addEntity(loadEntity(type, entityLoader));
		}
		entityLoader.close();
	}
	
	protected Entity loadEntity(String type, Scanner entityLoader) {
		if(type.equalsIgnoreCase("NomHazard") || type.equalsIgnoreCase("NommableMob") || type.equalsIgnoreCase("NomDoor") || type.equalsIgnoreCase("NomSpawner")) {
			String name = entityLoader.next();
			int x = entityLoader.nextInt();
			int y = entityLoader.nextInt();
			int speed = entityLoader.nextInt();
			ArrayList animationCoords = new ArrayList<Integer>();
			String done = entityLoader.next();
			int i = 0;
			while(!done.equalsIgnoreCase("</animCo>")) {
				animationCoords.add(Integer.parseInt(done));
				animationCoords.add(entityLoader.nextInt());
				i++;
				done = entityLoader.next();
			}
			if(type.equalsIgnoreCase("NomHazard")) return new NomHazard(this, name, x, y, speed, listTo2DArray(animationCoords), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt());
			else if(type.equalsIgnoreCase("NommableMob")) return loadNommableMob(entityLoader, name, x, y, speed, listTo2DArray(animationCoords));
			else if(type.equalsIgnoreCase("NomDoor")) return new NomDoor(this, x, y, speed, entityLoader.nextInt(), entityLoader.nextInt(), name, listTo2DArray(animationCoords), entityLoader.nextInt());
			else if(type.equalsIgnoreCase("NomSpawner")) {
				ArrayList placementCoords = new ArrayList<Integer>();
				String Done = entityLoader.next();
				int I = 0;
				while(!Done.equalsIgnoreCase("</placeCo>")) {
					placementCoords.add(Integer.parseInt(Done));
					placementCoords.add(entityLoader.nextInt());
					I++;
					Done = entityLoader.next();
				}
				return new NomSpawner(this, name, x, y, speed, entityLoader.nextInt(), listTo2DArray(animationCoords), entityLoader.nextInt(), listTo2DArray(placementCoords), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), loadEntity(entityLoader.next(), entityLoader));
			}
		}
		else if(type.equalsIgnoreCase("NomSign")) return new NomSign(this, entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextLine());
		return null;
	}
	//public NomSpawner(Level level, int color, int area, int[][] animationCoords, int animationSwitchDelay, int[][] placementCoords, int spawnDelay, int spawnX, int spawnY, Entity spawned)
	
	protected NommableMob loadNommableMob(Scanner entityLoader, String name, int x, int y, int speed, int[][] animationCoords) {
		//Level level, String name, int x, int y, int speed, int spitDelay, int[][] animationCoords, int color, int animationSwitchDelay, int mirrorDir, int scale, int xDir, int yDir, int xMin, int xMax, int yMin, int yMax, int numWhenChewed, NommableMob whenChewed
		NommableMob n = new NommableMob(this, name, x, y, speed, entityLoader.nextInt(), entityLoader.nextInt(), animationCoords, entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), entityLoader.nextInt(), null);
		String re = entityLoader.next();
		if(re.equalsIgnoreCase("chewable")) {
			name = entityLoader.next();
			x = entityLoader.nextInt();
			y = entityLoader.nextInt();
			speed = entityLoader.nextInt();
			ArrayList<Integer> animationcoords = new ArrayList<Integer>();
			String done = entityLoader.next();
			int i = 0;
			while(!done.equalsIgnoreCase("</animCo>")) {
				animationcoords.add(Integer.parseInt(done));
				animationcoords.add(entityLoader.nextInt());
				i++;
			done = entityLoader.next();
			}
			n.whenChewed = loadNommableMob(entityLoader, name, x, y, speed, listTo2DArray(animationcoords));
		}
		return n;
	}
	
	protected int[][] listTo2DArray(List<Integer> list) {
		int x = 0;
		int y = 0;
		int[][] array = new int[list.size() / 2][2];
		for(int i = 0; i < list.size(); i++) {
			if((i & 1) == 0) x = list.get(i);
			if((i & 1) == 1) y = list.get(i);
			if((i & 1) == 1 && i != 0) {
				array[i >> 1][0] = x;
				array[i >> 1][1] = y;
			}
		}
		return array;
	}
	
	public void alterEntity(String oldData, String newData) {
		Scanner sc = null;
		PrintWriter writer = null;
		ArrayList<String> lines = new ArrayList<>();
		try {
			entityFile = entityFile.getAbsoluteFile();
			sc = new Scanner(entityFile);
			writer = new PrintWriter(entityFile);
			System.out.println(sc.hasNextLine());
			while(sc.hasNextLine()) {
				lines.add(sc.nextLine());
				System.out.println("scan: ");
			}
			System.out.println("scanned: ");
			lines.remove(oldData);
			lines.add(newData);
			System.out.println("done: ");
			for(String s : lines) writer.println(s);
			entities.clear();
			loadEntities();
			sc.close();
			writer.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
			/*
			NomHazard Fire 32 32 1 5 0 6 0 </animCo> -1144862209 100 2 00 1 0 0 0 0
NomHazard Electricity 192 176 1 7 0 8 0 </animCo> -704643073 100 1 00 1 0 0 0 0
NommableMob Peach 88 176 0 16 0 </animCo> 60 -708197633 1000 00 1 -1 0 0 0 0 1 chewable 
Peach_Pit 88 176 2 15 0 </animCo> 180 1093293311 1000 00 1 -1 0 0 0 0 0 null
			 */
	}
}
