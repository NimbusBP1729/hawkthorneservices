package com.projecthawkthorne.gamestate.elements;


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

}
