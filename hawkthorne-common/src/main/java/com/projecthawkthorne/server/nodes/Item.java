package com.projecthawkthorne.server.nodes;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.projecthawkthorne.gamestate.Level;

public abstract class Item extends Node{
	protected int quantity = 1;
	protected int maxItems = 1000;
	public Item(RectangleMapObject obj, Level level) {
		super(obj, level);
	}
	
	/**
	 * return true if these specific types of items
	 * can be merged
	 * @param other
	 * @return
	 */
	public boolean mergible(Item other){
		return(this.type==other.type &&
			   this.name==other.name );
	}
	
	/**
	 * attempts to merge other item into this item
	 * @param other
	 * @return true if completely merged
	 */
	public boolean merge(Item other){
		if(this.quantity + other.quantity <= this.maxItems){
			this.quantity = this.quantity + other.quantity;
			return true;
		}else{
			other.quantity = (other.quantity + this.quantity) - this.maxItems;
			this.quantity = this.maxItems;
			return false;
		}
	}
	public abstract void use();
	
}
