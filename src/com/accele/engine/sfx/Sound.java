package com.accele.engine.sfx;

import org.newdawn.slick.openal.Audio;

import com.accele.engine.core.Engine;
import com.accele.engine.sfx.internal.SoundStore;
import com.accele.engine.util.Resource;

public final class Sound extends SFX {

	private Audio sound;

	public Sound(Engine engine, String registryID, String localizedID, Resource sound) {
		super(engine, registryID, localizedID);
		sound.load();
		this.sound = (Audio) sound.getValue();
	}

	@Override
	public void play() {
		if (!(boolean) engine.getRegistry().getProperty("internal:soundMute").get())
			play(1.0F, (float) engine.getRegistry().getProperty("internal:soundVolume").get() / 100f);
	}

	@Override
	public void play(float pitch, float volume) {
		if (!(boolean) engine.getRegistry().getProperty("internal:soundMute").get())
			this.sound.playAsSoundEffect(pitch, volume * SoundStore.get().getSoundVolume(), false);
	}

	@Override
	public void loop() {
		if (!(boolean) engine.getRegistry().getProperty("internal:soundMute").get())
			loop(1.0F, 1.0F);
	}

	@Override
	public void loop(float pitch, float volume) {
		if (!(boolean) engine.getRegistry().getProperty("internal:soundMute").get())
			this.sound.playAsSoundEffect(pitch, volume * SoundStore.get().getSoundVolume(), true);
	}

	@Override
	public void stop() {
		this.sound.stop();
	}

	@Override
	public boolean isPlaying() {
		return this.sound.isPlaying();
	}

}
