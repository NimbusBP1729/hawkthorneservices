package com.projecthawkthorne.gamestate;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.KeyMapping;

public abstract class GenericGamestate extends Gamestate {
	
	private Map<GameKeys, Boolean> keyMap = new HashMap<GameKeys, Boolean>();
	private float lifeTime = 0;

	public final void setIsKeyDown(GameKeys button, boolean b) {
		this.keyMap.put(button, Boolean.valueOf(b));
	}

	public final boolean getIsKeyDown(GameKeys button) {
		Boolean b = keyMap.get(button);
		return b == null ? false : b;
	}
	
	public void update(float dt){
		lifeTime += dt;
		for (GameKeys gk : GameKeys.values()) {
			boolean wasDown = this.getIsKeyDown(gk);
			boolean isPcKeyDown = Gdx.input.isKeyPressed(KeyMapping
						.gameKeyToInt(gk));
			boolean isAndroidKeyDown = context.getUserInterface().getIsAndroidKeyDown(gk);
			boolean isDown = isPcKeyDown || isAndroidKeyDown;
			this.setIsKeyDown(gk, isDown);
			if(lifeTime > 0.5f){
				if (!wasDown && isDown) {
					Gdx.app.log("keypress(g)", gk.toString());
					this.keypressed(gk);
				} else if (wasDown && !isDown) {
					Gdx.app.log("keyrelease(g)", gk.toString());
					this.keyreleased(gk);
				}
			}
		}
	}

	public abstract void keypressed(GameKeys gk);

	public abstract void keyreleased(GameKeys gk);

	public String getSoundtrack() {
		return "opening";
	}
	
	@Override
	public void hide() {
	}
	
	@Override
	public void show() {
		lifeTime = 0;
	}
}
