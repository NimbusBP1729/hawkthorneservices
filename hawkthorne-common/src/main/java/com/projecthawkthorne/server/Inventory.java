/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.server;

import com.projecthawkthorne.server.nodes.Item;
import com.projecthawkthorne.server.nodes.Material;
import com.projecthawkthorne.server.nodes.Potion;
import com.projecthawkthorne.server.nodes.Weapon;

/**
 * 
 * @author Patrick
 */
public class Inventory {
	private final Player owner;
	private Weapon currentWeapon = null;
	private Weapon[] weapons;
	private Potion[] potions;
	private Material[] materials;
	private static final int LIST_MAX = 16;
	private int selection;
	private int cursor;
	private int page;

	public Inventory(Player plyr) {
		this.owner = plyr;
		this.weapons = new Weapon[LIST_MAX];
		this.potions = new Potion[LIST_MAX];
		this.materials = new Material[LIST_MAX];
	}

	public Weapon getCurrentWeapon() {
		return currentWeapon;
	}

	boolean isVisible() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	void keypressed(Keys button) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	void open() {
		new UnsupportedOperationException("Not yet implemented")
				.printStackTrace();
	}

	void update(int dt) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	// TODO: ensure the item is removed from the world if need be
	public void addItem(Item item) {
		if (item instanceof Weapon) {
			for (int i = 0; i < LIST_MAX; i++) {
				if (this.weapons[i] == null) {
					this.weapons[i] = (Weapon) item;
					break;
				}
			}
		} else if (item instanceof Material) {
			for (int i = 0; i < LIST_MAX; i++) {
				if (this.materials[i] == null) {
					this.materials[i] = (Material) item;
					break;
				}
			}
		} else if (item instanceof Potion) {
			for (int i = 0; i < LIST_MAX; i++) {
				if (this.potions[i] == null) {
					this.potions[i] = (Potion) item;
					break;
				}
			}
		} else {
			throw new UnsupportedOperationException("invalid item type");
		}
	}
}
