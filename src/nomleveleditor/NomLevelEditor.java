package nomleveleditor;

import gfx.ColorData;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.util.List;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;

import level.tiles.Tile;
import nomgame.NomGame;
import nomgame.NomInputHandler;
import nomlevel.NomLevel;

public class NomLevelEditor extends NomGame {

	private int selectedX = 0;
	private int selectedY = 0;
	private int[][] entityAlterPos;
	private List<String> entityAlterHistory;
	private int[][] tileAlterPos;
	private List<Integer> tileAlterHistory;
	private int entityUndoIndex;
	private int tileUndoIndex;
	private Tile newtile;
	private String newentity;
	private boolean tilealtering;
	private JTextPane XY;
	private JRadioButton alterTile;
	private JRadioButton alterEntity;
	private JComboBox tiles;
	private JButton createtile;
	private JComboBox entities;
	private JButton createentity;
	private JButton undo;
	private JButton redo;
	private JButton save;
	
	public NomLevelEditor() {
		super("Nom Editor", "res/EditIcon.png");
		level.removeEntities(nom);
		level = new NomLevel("/levels/SecondLevel.png");
		level.addEntity(nom);
		nom.editMode = true;
		nom.gravitates = false;
		newtile = Tile.VOID;
		tilealtering = true;
		frame.setMinimumSize(new Dimension(width * scale, (height + 3) * scale));
		frame.setMaximumSize(new Dimension(width * scale, (height + 3) * scale));
		frame.setPreferredSize(new Dimension(width * scale, (height + 3) * scale));
		frame.setResizable(false);
		JFrame attributesFrame = new JFrame("Editor Pane");
		attributesFrame.setIconImage(new ImageIcon("res/EditIcon.png").getImage());
		attributesFrame.setMinimumSize(new Dimension(width * scale / 2, 768));
		attributesFrame.setMaximumSize(new Dimension(width * scale / 2, 768));
		attributesFrame.setPreferredSize(new Dimension(width * scale / 2, 768));
		attributesFrame.setResizable(false);
		attributesFrame.setLocation(100, 0);
		attributesFrame.setLayout(new FlowLayout());
		attributesFrame.add(XY = new JTextPane());
		XY.setEditable(false);
		XY.setMinimumSize(new Dimension(100, 20));
		XY.setPreferredSize(new Dimension(100, 20));
		XY.setMaximumSize(new Dimension(100, 20));
		ButtonGroup J = new ButtonGroup();
		J.add(alterTile = new JRadioButton("Tiles"));
		J.add(alterEntity = new JRadioButton("Entities"));
		JPanel A = new JPanel();
		A.add(alterTile);
		A.add(alterEntity);
		attributesFrame.add(A);
		attributesFrame.add(tiles = new JComboBox(getTileNames()));
		attributesFrame.add(createtile = new JButton("create"));
		attributesFrame.add(entities = new JComboBox<String>());
		attributesFrame.add(createentity = new JButton("create"));
		attributesFrame.add(undo = new JButton(new ImageIcon("res/levels/LevelEditorRes/Undo.png")));
		attributesFrame.add(redo = new JButton(new ImageIcon("res/levels/LevelEditorRes/Redo.png")));
		attributesFrame.add(save = new JButton(new ImageIcon("res/levels/LevelEditorRes/Save.png")));
		save.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						level.saveLevelToFile();
					}
				});
		tiles.addItemListener(
				new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent arg0) {
						if(arg0.getStateChange() == ItemEvent.SELECTED) {
							newtile = Tile.tiles[tiles.getSelectedIndex()];
						}
					}
				});
		attributesFrame.pack();
		attributesFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		attributesFrame.setVisible(true);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void run() {
		//((NomLevel) level).alterEntity("NomHazard Fire 32 32 1 5 0 6 0 </animCo> -1144862209 100 2 00 1 0 0 0 0", "");
		//level.addEntity(nom);
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
				Thread.sleep(2);
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
		level.update();
		selectedX = (nom.x >> 3) << 3;
		if(selectedX < 0) selectedX = 0;
		if(selectedX > ((level.width - 1) << 3)) selectedX = ((level.width - 1) << 3);
		selectedY = (nom.y >> 3) << 3;
		if(selectedY < 0) selectedY = 0;
		if(selectedY > ((level.height - 1) << 3)) selectedY = ((level.height - 1) << 3);
		XY.setText("X: " + (selectedX >> 3) + " Y: " + (selectedY >> 3));
		if(((NomInputHandler) input).Alter.pressed) level.alterTile(selectedX >> 3, selectedY >> 3, newtile);
		if(((NomInputHandler) input).ToggleMode.pressed) {
			nom.gravitates = !nom.gravitates;
			nom.editMode = !nom.editMode;
		}
		if(!level.equals(nom.level)) level = nom.level;
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
		level.renderTiles(screen, x - screen.width / 2, y - screen.height / 2);
		level.renderEntities(screen);
		if(!nom.editMode) renderNomStatus();
		screen.render(selectedX, selectedY, 17, ColorData.getColorData(-1, 000, -1, -1), 1);
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
	
	public String[] getTileNames() {
		String[] names = new String[256];
		Scanner sc = null;
		try {
			sc = new Scanner(new File("res/levels/LevelEditorRes/TileNames.txt"));
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		for(int i = 0; i < names.length && sc.hasNext(); i++) {
			names[i] = sc.next();
		}
		return names;
	}

	public static void main(String[] args) {
		new Thread(new NomLevelEditor()).run();
	}
}
