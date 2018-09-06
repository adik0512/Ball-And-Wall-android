package com.example.tablet.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;




public class SpriteObject {

	private Bitmap bitmap;
	private double x;		
	private double y;	
	private double x_move = 0;
	private double y_move = 0;
	
	public int DEAD = 0;
	public int ALIVE = 1;

	private int state;
	

	public SpriteObject(Bitmap bitmap, int x, int y) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		state = ALIVE;
	}
	
	public int getState(){
		return state;
	}
	public void setState(int s){
		state = s;
	}

	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	/**
	 * pionoa oraz pozioma predkosc sprajta
	 * @return
	 */
	public double getMoveY(){
		return y_move;
	}
	public double getMoveX(){
		return x_move;
	}

	
	
	public void setMoveX(double speedx){
		x_move = speedx;
	}
	
	public void setMoveY(double speedy){
		y_move = speedy;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	
/**
 * 	przygotowuje obiekty rakietke,blokie pilke to rysowania 
 * @param canvas
 */
	public void draw(Canvas canvas) {
		if(state == ALIVE){
			canvas.drawBitmap(bitmap, (int)x - (bitmap.getWidth() / 2), (int)y - (bitmap.getHeight() / 2), null);
		}
	}

	
	/**
	 * adj_mov - wspolczynnik szybkosci gry
	 * gra dziala tak szybko jakto jest mozliwe
	 */

	public void update(int adj_mov) {
		if(state == ALIVE){
			x += x_move;
			y += y_move;
		}
	}
	
	public boolean collide(SpriteObject entity){
		if(state != DEAD){
			double left, entity_left;
			double right, entity_right;
			double top, entity_top;
			double bottom, entity_bottom;
	
			left = x;
			entity_left = entity.getX();
			right = x + bitmap.getWidth();
			entity_right = entity.getX() + entity.getBitmap().getWidth();
			top = y;
			entity_top = entity.getY();
			bottom = y + bitmap.getHeight();
			entity_bottom = entity.getY() + entity.getBitmap().getHeight();
	
			if (bottom < entity_top) {
				return false;
			}
			else if (top > entity_bottom){
				return false;
			}
			else if (right < entity_left) {
				return false;
			}
			else if (left > entity_right){
				return false;
			}
			else{
				return true;
			}
		}
		
		else{
			return false;
		}
		
	}
	

}
