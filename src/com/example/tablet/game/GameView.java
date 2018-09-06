package com.example.tablet.game;



import java.util.concurrent.ArrayBlockingQueue;

import com.example.tablet.game.*;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private SpriteObject paddle;
	private SpriteObject[] block;
	private SpriteObject ball;
	
	private GameLogic mGameLogic;
	private ArrayBlockingQueue<InputObject> inputObjectPool;
	private int game_width;
	private int game_height;
	//odczytywanie danych z pliku xml
	private Resources res;
	private int[] x_coords;
	private int[] y_coords;
	private int block_count;
	
	
	private Context context;

	private MediaPlayer mpBlock;
	private MediaPlayer mpPaddle;
	private MediaPlayer mpGameOver;
	
	public GameView(Context con) {
		super(con);
		context = con;
		getHolder().addCallback(this);
		paddle = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.paddle), 600, 675);

		ball = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.ball), 600, 600);
		mGameLogic = new GameLogic(getHolder(), this);
		createInputObjectPool();
		
		
		res = getResources();
		block_count = res.getInteger(R.integer.blocknumber);
		x_coords = res.getIntArray(R.array.x);
		y_coords = res.getIntArray(R.array.y);
		block = new SpriteObject[block_count];
		for(int i = 0; i < block_count; i++){
			block[i] = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.block), x_coords[i], y_coords[i]);
		}
		
		
		
		mpBlock = MediaPlayer.create(context, R.raw.sound);
		mpGameOver = MediaPlayer.create(context, R.raw.gameover);
		mpPaddle = MediaPlayer.create(context, R.raw.jump);
		
		setFocusable(true);
	}
/**
 * inicjalizacja inputObjectPool obiekt przechowje zdarzenia wejsciowe dla 20 obiektow
 */
	private void createInputObjectPool() {
		inputObjectPool = new ArrayBlockingQueue<InputObject>(20);
		for (int i = 0; i < 20; i++) {
			inputObjectPool.add(new InputObject(inputObjectPool));
		}
	}

	/**
	 * metoda ta przypisuje zdarzenie do obiektu klasy InputObject() i zachowuje go do pozniejszej obslugi 
	 
	//mGameLogic.feedInput(input); metoda ta pozwala na dostep do szczagolow zdarzenia w chwili bezczynnosci watku 
	//nastepnie watek jest usypiany na 15 ms aby nie zostalo pobranych zbyt wiele danych wejsciowych
*/
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			try {
				int hist = event.getHistorySize();
				if (hist > 0) {
					for (int i = 0; i < hist; i++) {
						InputObject input = inputObjectPool.take();
						input.useEventHistory(event, i);
						mGameLogic.feedInput(input);
					}
				}
				InputObject input = inputObjectPool.take();
				input.useEvent(event);
				mGameLogic.feedInput(input);
			} catch (InterruptedException e) {
			}
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
			}
			return true;
		}
		
		
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
		
	}
	
	//

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mGameLogic.setGameState(GameLogic.RUNNING);
		mGameLogic.start();
		ball.setMoveY(-20);
		ball.setMoveX(35);
		
		


	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mpBlock.release();
		mpPaddle.release();
		mpGameOver.release();
	}
	
	/**
	 *  funkcja rysujaca wymusza na sprajcie jego przerysowanie
	 */
	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.background), 0, 0, null);
		 
		//canvas.drawColor(Color.WHITE);
		ball.draw(canvas);
		paddle.draw(canvas);
		for(int i = 0; i < block_count; i++){
			block[i].draw(canvas);
		}
		game_width = canvas.getWidth();
		game_height = canvas.getHeight();
		
	
	}

	/**
	 *  funkcja wykrywa kolizje, 
	 */
	public void update(int adj_mov) {
		
		int ball_bottom = (int)(ball.getY() + ball.getBitmap().getHeight());
		int ball_right = (int)(ball.getX() + ball.getBitmap().getWidth());
		int ball_y = (int) ball.getY();
		int ball_x = (int) ball.getX();
		
		/**
		 * Zderzenie od do³u 
		 */
		if(ball_bottom >= game_height){
			ball.setMoveY(-ball.getMoveY());
			mpGameOver.start();
			res = getResources();
			block_count = res.getInteger(R.integer.blocknumber);
			x_coords = res.getIntArray(R.array.x);
			y_coords = res.getIntArray(R.array.y);
			block = new SpriteObject[block_count];
			for(int i = 0; i < block_count; i++){
				block[i] = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.block), x_coords[i], y_coords[i]);}
			/**
			 * gracz przegrywa 
			 */
		}
		
		/**
		 * zderzenie od góry 
		 */
		if(ball_y <= ball.getBitmap().getHeight()/2){
			ball.setMoveY(-ball.getMoveY());
		}
		
		/**
		 * zderzenie z prawej strony 
		 */
		if(ball_right >= game_width){
			ball.setMoveX(-ball.getMoveX());
		}
		
		/**
		 * zderzenie z lewej strony 
		 */
		if(ball_x <= ball.getBitmap().getWidth()/2){
			ball.setMoveX(-ball.getMoveX());
		}
		
		
		/**
		 * zderzenie z rakietk¹ 
		 */
		if(paddle.collide(ball)){
			if(ball_bottom > paddle.getY() && ball_bottom < paddle.getY() + 20){
				ball.setMoveY(-ball.getMoveY());
				mpPaddle.start();
			}
		}
		
		
		/**
		 * sprawdzenie zderzenia z blokami
		 */
		for(int i = 0; i < block_count; i++){
			
			if(block[i].getState()==1){
				
			if(ball.collide(block[i])){
				block[i].setState(block[i].DEAD);
				mpBlock.start();
				int block_bottom = (int)(block[i].getY() + block[i].getBitmap().getHeight());
				int block_right =(int)(block[i].getX() + block[i].getBitmap().getWidth());

				
				/**
				 *  uderzenie w dó³ bloku
				 */
				if (ball_y > block_bottom ) {
					ball.setMoveY(-ball.getMoveY());
					
					
				}
				/**
				 * uderzenie w górê bloku
				 */
				else if (ball_bottom < block[i].getY() + (block[i].getBitmap().getHeight())) {
					ball.setMoveY(ball.getMoveY());
					
				}
				/**
				 * uderzenie z prawej strony
				 */
				else if (ball_x > block_right - (block[i].getBitmap().getWidth())) {
					ball.setMoveX(ball.getMoveX());
					
				}
				/**
				 * uderzenie z lewej strony
				 */
				else if (ball_right < block[i].getX()) {
					ball.setMoveX(-ball.getMoveX());
					
				}
			}
			}
		}
		

		/**
		 * wykonanie okreœlonych uaktualnieñ 
		 */
		for(int i = 0; i < block_count; i++){
			block[i].update(adj_mov);
		}		
		
		if(paddle.getX()>=getWidth()){
			paddle.setMoveX(0);
		}
		if(paddle.getX()>=0){
			paddle.setMoveX(0);
		}
		
		
		paddle.update(adj_mov);
		ball.update(adj_mov);

	}
	
	
	/**
	 *  dotykowe sterowanie rakietka
	 * @param input
	 */
	public void processMotionEvent(InputObject input){
		paddle.setX(input.x);
		//paddle.setY(input.y);

	}
	
	
	
	public void processKeyEvent(InputObject input){

		

	}
/**
 * sprawdzanie przechylenia 
 * @param orientation
 */
	public void processOrientationEvent(float orientation[]){
		
		float roll = orientation[2];
		if (roll < -40) {
			paddle.setMoveX(2);
		} else if (roll > 40) {
			paddle.setMoveX(-2);
		}
		
	}
	
	
		
}
