package com.Geekpower14.UpperVoid.Task;

import com.Geekpower14.UpperVoid.Arena.APlayer;
import com.Geekpower14.UpperVoid.Arena.Arena;

public class CoinsGiver extends Thread {

	private Arena ar;
	private boolean cont = true;

	public CoinsGiver(Arena ar) {
		this.ar = ar;
	}

	public void end() {
		cont = false;
	}

	public void run() {
		while (cont) {
			try {
				sleep(20000);
				if (ar.getConnectedPlayers() > 1) {
					for (APlayer ap : ar.getAPlayers()) {
						if (!ap.isSpectator()) {
							ar.addCoins(ap.getP() , ar.coinsGiven, "Mort d'un joueur");
							ap.updateScoreboard();
						}
					}
				} else {
					end();
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
