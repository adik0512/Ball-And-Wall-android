package com.example.tablet.game;


import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import java.lang.Thread;
import java.util.concurrent.ArrayBlockingQueue;


public class GameLogic extends Thread {
	
	/**
	 * metoda ta umozliwia modyfikacje obszaru p³ótna wewnatrz metody run()blokuje i zwalnia blokade na uzywanym obiekcie klasy Canvas
	 * zablokowanie tego obiektu oznacza ze tylko jeden watek moze z niego korzystac
	 */
	
	private SurfaceHolder surfaceHolder;
	/**
	 *  tworzy instancje obiektu klasy GameView a nastepnie uzywa go do wywolywania metody update i onDraw
	 */
	private GameView mGameView;
	private int game_state;
    public static final int PAUSE = 0;
    public static final int READY = 1;
    public static final int RUNNING = 2;
    private ArrayBlockingQueue<InputObject> inputQueue = new ArrayBlockingQueue<InputObject>(20);
    private Object inputQueueMutex = new Object();
    
	public GameLogic(SurfaceHolder surfaceHolder, GameView mGameView) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.mGameView = mGameView;
	}
	
    /**
     * metoda zachowuje stan gry w jej dowolnym momencie
     * @param gamestate
     */
	public void setGameState(int gamestate) {
		this.game_state = gamestate;
	}
	public int getGameState(){
		return game_state;
	}


/**
 *  w chwili gdy gra jest w stanie dzia³ania metoda run () próbuje za³o¿yæ blokade na obszarze p³ótna wykonaæ wszystkie konieczne 
 *  operacje zwolnic blokadeoraz przygotowac proces do ponowengo uruchomienia 
 */
	@Override
	public void run() {
		long time_orig = System.currentTimeMillis();
		long time_interim;
		Canvas canvas;
		
		
		while (game_state == RUNNING) {
			canvas = null;
			try {
				
				canvas = this.surfaceHolder.lockCanvas();
				
				synchronized (surfaceHolder) {
					/**
					 * wyjatek ktory dostosowuje ruch animacji na podstawie czasu ktory uplynal np. jezeli jedencykl zajmie duzo czasu a inny
					 * //mniej wtedy sprajt zastanie przesuniety w zaleznosci od tej wartosci o odpowiednia odleglosc
					 */
					
	               try {
	                    Thread.sleep(30);
	                } catch (InterruptedException e1) {
	                }

	                time_interim = System.currentTimeMillis(); 
	                int adj_mov = (int)(time_interim - time_orig);
	                mGameView.update(adj_mov); 
	                /** 
	                 * tu nastapi przetwarzanie danych wejsciowych
	                 */
	                processInput();
	                time_orig = time_interim; 
					this.mGameView.onDraw(canvas);				
				}
			} 
			finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
	
	/**
	 * nadzoruje dzialanie kolejki utworzonej z klasy ArrayBlockingQueue
	 * @param input
	 */
	public void feedInput(InputObject input) {
		synchronized(inputQueueMutex) {
			try {
				inputQueue.put(input);
			} catch (InterruptedException e) {
			}
		}
	}
/**
 * metoda przetwarza dane wejsciowe
 */
	private void processInput() {
		synchronized(inputQueueMutex) {
			ArrayBlockingQueue<InputObject> inputQueue = this.inputQueue;
			while (!inputQueue.isEmpty()) {
				try {
					InputObject input = inputQueue.take();
					if (input.eventType == InputObject.EVENT_TYPE_KEY) {
						mGameView.processKeyEvent(input);
					} else if (input.eventType == InputObject.EVENT_TYPE_TOUCH) {
						mGameView.processMotionEvent(input);
					}
					input.returnToPool();
				} catch (InterruptedException e) {
				}
			}
		}
	}
		


	
}
