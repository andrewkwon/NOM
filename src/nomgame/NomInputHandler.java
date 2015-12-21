package nomgame;

import entities.Entity;
import game.InputHandler;

import java.awt.event.KeyEvent;

import nomentities.Nom;

public class NomInputHandler extends InputHandler {
	
	public Key Mouth = new Key();
	public Key Digest = new Key();
	public Key Restart = new Key();
	public Key Read = new Key();
	public Key ToggleMode = new Key();
	public Key Alter = new Key();
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		toggleKeys(arg0.getKeyCode(), true);
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) {
		toggleKeys(arg0.getKeyCode(), false);
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	
	@Override
	public void toggleKeys(int arg0, boolean pressed) {
		if(arg0 == KeyEvent.VK_LEFT || arg0 == KeyEvent.VK_A) left.pressed = pressed;
		if(arg0 == KeyEvent.VK_RIGHT || arg0 == KeyEvent.VK_D) right.pressed = pressed;
		if(arg0 == KeyEvent.VK_UP || arg0 == KeyEvent.VK_W) up.pressed = pressed;
		if(arg0 == KeyEvent.VK_DOWN || arg0 == KeyEvent.VK_S) down.pressed = pressed;
		if(arg0 == KeyEvent.VK_SPACE) Mouth.pressed = pressed;
		if(arg0 == KeyEvent.VK_SHIFT || arg0 == KeyEvent.VK_INSERT) Digest.pressed = pressed;
		if(arg0 == KeyEvent.VK_BACK_SPACE) Read.pressed = pressed;
		if(arg0 == KeyEvent.VK_ESCAPE) Restart.pressed = pressed;
		if(arg0 == KeyEvent.VK_ENTER) ToggleMode.pressed = pressed;
		if(arg0 == KeyEvent.VK_CONTROL) Alter.pressed = pressed;
	}
}
