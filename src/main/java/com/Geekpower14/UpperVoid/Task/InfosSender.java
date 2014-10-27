package com.Geekpower14.UpperVoid.Task;

import com.Geekpower14.UpperVoid.UpperVoid;

public class InfosSender implements Runnable {

	public UpperVoid plugin;

	public boolean run = true;

	public InfosSender(UpperVoid pl) {
		plugin = pl;
	}

	@Override
	public void run() {
		while (run) {
			plugin.cm.sendArenasInfos(true);

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void disable() {
		run = false;
	}

}
