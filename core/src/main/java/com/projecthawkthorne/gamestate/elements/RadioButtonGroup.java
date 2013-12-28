package com.projecthawkthorne.gamestate.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.projecthawkthorne.client.display.Assets;

public class RadioButtonGroup extends GamestateElement {

	private String[] options;
	private int selection;
	private int cursor;

	public RadioButtonGroup(float x, float y, float width, float height,
			String... options) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.options = options;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public int getSelection() {
		return selection;
	}

	public void setSelection(int selection) {
		this.selection = selection;
	}

	public int getCursor() {
		return cursor;
	}

	public void setCursor(int cursor) {
		this.cursor = cursor;
	}

	@Override
	public void draw(SpriteBatch batch) {
		Texture rbgTexture = Assets.loadTexture(Assets.SRC_IMAGES
				+ "defaultTexture.png");

		batch.draw(rbgTexture, this.getX(), this.getY(), this.getWidth(),
				this.getHeight());
		float x = this.getX();
		float y = this.getY();

		for (String ro : this.getOptions()) {
			if (ro.equals(this.getOptions()[this.getSelection()])) {
				batch.setColor(0, 1, 0, 1);
			} else if (ro.equals(this.getOptions()[this.getCursor()])) {
				batch.setColor(1, 0, 0, 1);
			} else {
				batch.setColor(1, 1, 1, 1);
			}
			Assets.font.drawMultiLine(batch, ro, x, y);
			y += 10;
		}

	}
}
