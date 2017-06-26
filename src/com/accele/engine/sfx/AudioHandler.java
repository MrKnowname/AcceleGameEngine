package com.accele.engine.sfx;

import com.accele.engine.core.Engine;
import com.accele.engine.core.Tickable;

public final class AudioHandler implements Tickable {

	private Engine engine;
	private Music currentMusic;
	
	public AudioHandler(Engine engine) {
		this.engine = engine;
	}
	
	@Override
	public void onUpdate() {
		if ((boolean) engine.getRegistry().getProperty("internal:musicMute").get())
			for (Music m : engine.getRegistry().getAllMusic())
				m.setVolume(0f);
		else
			for (Music m : engine.getRegistry().getAllMusic())
				m.setVolume((float) engine.getRegistry().getProperty("internal:musicVolume").get());
	}
	
	public Music getCurrentMusic() {
		return currentMusic;
	}
	
	public void setCurrentMusic(Music currentMusic) {
		System.out.println("setting currentMusic to " + currentMusic.registryID);
		this.currentMusic = currentMusic;
	}
	
}
