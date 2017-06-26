package com.accele.engine.sfx;

import org.newdawn.slick.openal.Audio;

import com.accele.engine.core.Engine;
import com.accele.engine.sfx.internal.AudioImpl;
import com.accele.engine.sfx.internal.SoundStore;
import com.accele.engine.util.Resource;

public final class Music extends SFX {
	
	private Audio sound;
	private boolean paused;
	private static Music currentMusic;
	private boolean playing;
	//private ArrayList<MusicListener> listeners = new ArrayList<>();

	private float volume = 1.0F;
	private float fadeStartGain;
	private float fadeEndGain;
	private int fadeTime;
	private int fadeDuration;
	private boolean stopAfterFade;
	
	@SuppressWarnings("unused")
	private boolean positioning;
	private float requiredPosition = -1.0F;

	public Music(Engine engine, String registryID, String localizedID, Resource music) {
		super(engine, registryID, localizedID);
		music.load();
		this.sound = (Audio) music.getValue();
	}

	/*public static void poll(int delta) {
		if (currentMusic != null) {
			SoundStore.get().poll(delta);
			if (!SoundStore.get().isMusicPlaying()) {
				if (!currentMusic.positioning) {
					Music oldMusic = currentMusic;
					currentMusic = null;
					oldMusic.fireMusicEnded();
				}
			} else
				currentMusic.update(delta);
		}
	}

	public void addListener(MusicListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(MusicListener listener) {
		this.listeners.remove(listener);
	}

	/*private void fireMusicEnded() {
		this.playing = false;
		for (int i = 0; i < this.listeners.size(); i++)
			((MusicListener) this.listeners.get(i)).musicEnded(this);
	}

	private void fireMusicSwapped(Music newMusic) {
		this.playing = false;
		for (int i = 0; i < this.listeners.size(); i++)
			((MusicListener) this.listeners.get(i)).musicSwapped(this, newMusic);
	}*/

	@Override
	public void loop() {
		loop(1.0F, (float) engine.getRegistry().getProperty("internal:musicVolume").get() / 100f);
	}

	@Override
	public void play() {
		play(1.0F, (float) engine.getRegistry().getProperty("internal:musicVolume").get() / 100f);
	}

	@Override
	public void play(float pitch, float volume) {
		startMusic(pitch, volume, false);
	}

	@Override
	public void loop(float pitch, float volume) {
		if ((boolean) engine.getRegistry().getProperty("internal:musicMute").get())
			startMusic(pitch, 0f, true);
		else
			startMusic(pitch, volume, true);
	}

	private void startMusic(float pitch, float volume, boolean loop) {
		engine.getAudioHandler().setCurrentMusic(this);
		this.paused = false;
		if (currentMusic != null) {
			currentMusic.stop();
			//currentMusic.fireMusicSwapped(this);
		}

		currentMusic = this;
		if (volume < 0.0F)
			volume = 0.0F;
		if (volume > 1.0F) {
			volume = 1.0F;
		}
		this.sound.playAsMusic(pitch, volume, loop);
		this.playing = true;
		setVolume(volume);
		if (this.requiredPosition != -1.0F)
			setPosition(this.requiredPosition);
	}

	public void pause() {
		this.playing = false;
		this.paused = true;
		AudioImpl.pauseMusic();
	}

	@Override
	public void stop() {
		this.sound.stop();
	}

	public void resume() {
		this.playing = true;
		this.paused = false;
		AudioImpl.restartMusic();
	}

	@Override
	public boolean isPlaying() {
		return (currentMusic == this) && (this.playing);
	}

	public void setVolume(float volume) {
		if (volume > 1.0F)
			volume = 1.0F;
		else if (volume < 0.0F) {
			volume = 0.0F;
		}

		this.volume = volume;

		if (currentMusic == this)
			SoundStore.get().setCurrentMusicVolume(volume);
	}

	public float getVolume() {
		return this.volume;
	}

	public void fade(int duration, float endVolume, boolean stopAfterFade) {
		this.stopAfterFade = stopAfterFade;
		this.fadeStartGain = this.volume;
		this.fadeEndGain = endVolume;
		this.fadeDuration = duration;
		this.fadeTime = duration;
	}

	public void update(int delta) {
		if (!this.playing) {
			return;
		}

		if (this.fadeTime > 0) {
			this.fadeTime -= delta;
			if (this.fadeTime < 0) {
				this.fadeTime = 0;
				if (this.stopAfterFade) {
					stop();
					return;
				}
			}

			float offset = (this.fadeEndGain - this.fadeStartGain) * (1.0F - this.fadeTime / this.fadeDuration);
			setVolume(this.fadeStartGain + offset);
		}
	}

	public boolean setPosition(float position) {
		if (this.playing) {
			this.requiredPosition = -1.0F;

			this.positioning = true;
			this.playing = false;
			boolean result = this.sound.setPosition(position);
			this.playing = true;
			this.positioning = false;

			return result;
		}
		this.requiredPosition = position;
		return false;
	}

	public float getPosition() {
		return this.sound.getPosition();
	}

	public void resume(float volume) {
		if ((boolean) engine.getRegistry().getProperty("internal:musicMute").get())
			this.setVolume(0f);
		else
			this.setVolume(volume);
		resume();
	}

	public boolean isPaused() {
		return this.paused;
	}

}
