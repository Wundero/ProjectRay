package me.Wundero.Ray.animation.effect;
/*
 The MIT License (MIT)

 Copyright (c) 2016 Wundero

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;

import me.Wundero.Ray.animation.Animation;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;

/*
 * Class for a specific animation (called effect)
 * example: scrolling text
 * UNTESTED 
 */
public abstract class Effect<T> {

	private final T object;// object to modify for each frame: ex substring
	private final BiFunction<T, Integer, T> mod;// modifier
	private BiFunction<T, ParsableData, T> parser;
	private final int frames;// number of frames
	private final int delay;// time delay between each frame
	private List<T> objects = Utils.al();// final list of frames

	private Animation<T> anim;// animation for sending

	/**
	 * Create a new effect
	 */
	public Effect(T obj, BiFunction<T, Integer, T> mod, BiFunction<T, ParsableData, T> parser, int f, int d) {
		this.object = obj;
		this.mod = mod;
		this.frames = f;
		this.delay = d;
		this.parser = parser;
	}

	/**
	 * Send the effect frame to a player.
	 */
	public abstract boolean send(BiFunction<Text, Player, Boolean> sender, T obj, Player p);

	// access methods for anim - this cannot impl anim so instead has this

	/**
	 * Set whether the effect loops
	 */
	public void loop(boolean l) {
		anim.loop(l);
	}

	/**
	 * @return whether the effect loops
	 */
	public boolean loop() {
		return anim.isLoop();
	}

	/**
	 * Stop playing the effect
	 */
	public void stop() {
		anim.stop();
	}

	/**
	 * Start playing the effect
	 */
	public void start() {
		anim.start();
	}

	/**
	 * Set the stop function of the effect
	 */
	public void onStop(Runnable r) {
		anim.onStop(r);
	}

	/**
	 * Get the stop function of the effect
	 */
	public Runnable getOnStop() {
		return anim.getOnStop();
	}

	/**
	 * Get whether the effect is asynchronous
	 */
	public boolean async() {
		return anim.isAsync();
	}

	/**
	 * Set whether the effect is asynchronous
	 */
	public void async(boolean a) {
		anim.async(a);
	}

	/**
	 * Pause the effect
	 */
	public void pause() {
		anim.pause();
	}

	/**
	 * Resume the effect
	 */
	public void play() {
		anim.play();
	}

	// TODO animation effect that supports T as vars - gen vars and then setup?

	/**
	 * Create the animation underlying this effect
	 */
	public void setupAnimation(Player p, Map<String, Object> d, BiFunction<Text, Player, Boolean> sender) {
		setupAnimation(p, new ParsableData().setKnown(d), sender);
	}

	/**
	 * Create the animation underlying this effect
	 */
	public void setupAnimation(Player p, ParsableData d, BiFunction<Text, Player, Boolean> sender) {
		List<T> framez = Utils.sl();
		for (int i = 0; i < frames; i++) {
			try {
				T n = mod.apply(object, i);// mod can throw exception if it
											// wants to skip a frame
				n = parser.apply(n, d);
				framez.add(n);
			} catch (Exception e) {
			}
		}
		setObjects(framez);
		anim = new Animation<T>(framez, (f) -> {
			if (send(sender, f, p)) {
				return delay;
			} else {
				return -1;
			}
		}, (f) -> Tristate.TRUE);
	}

	/**
	 * Get the object upon which modifications are made to generate frames
	 */
	public T getObject() {
		return object;
	}

	/**
	 * Get the number of frames
	 */
	public int getFrames() {
		return frames;
	}

	/**
	 * Get the delay between each frame
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Get the modifying function for this effect
	 */
	public BiFunction<T, Integer, T> getMod() {
		return mod;
	}

	/**
	 * Set the variable parser. [parses T for variables then returns a modified
	 * T]
	 */
	public void setParser(BiFunction<T, ParsableData, T> parser) {
		if (anim != null) {
			return;
		}
		this.parser = parser;
	}

	/**
	 * Get the modified objects as a list.
	 */
	public List<T> getObjects() {
		return objects;
	}

	/**
	 * Set the list of modified objects.
	 */
	public void setObjects(List<T> objects) {
		this.objects = objects;
	}
}
