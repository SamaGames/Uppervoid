package com.Geekpower14.UpperVoid.Task;

import net.zyuiop.coinsManager.CoinsManager;

import com.Geekpower14.UpperVoid.Arena.APlayer;
import com.Geekpower14.UpperVoid.Arena.APlayer.Role;
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

	@SuppressWarnings("static-access")
	public void run() {
		while (cont) {
			try {
				this.sleep(20000);
				if (ar.getActualPlayers() > 1) {
					for (APlayer ap : ar.getAPlayers()) {
						if (ap.getRole() == Role.Player) {
							int total = CoinsManager.syncCreditJoueur(ap.getP()
									.getUniqueId(), ar.coinsGiven, true, true);
							ap.setCoins(ap.getCoins() + total);
							ap.updateScoreboard();
						}
					}
				} else {
					end();
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
