package me.Wundero.ProjectRay.animation;
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
import java.util.function.BiFunction;

import me.Wundero.ProjectRay.utils.Utils;

public class Effect<T> {

	private final T object;
	private final BiFunction<T, Integer, T> mod;
	private final int frames;
	private final int delay;

	private Animation<T> anim;

	public Effect(T obj, BiFunction<T, Integer, T> mod, int f, int d) {
		this.object = obj;
		this.mod = mod;
		this.frames = f;
		this.delay = d;
		setupAnimation();
	}

	public void loop(boolean l) {
		anim.loop(l);
	}

	public boolean loop() {
		return anim.isLoop();
	}

	public void stop() {
		anim.stop();
	}

	public void start() {
		anim.start();
	}

	public void onStop(Runnable r) {
		anim.onStop(r);
	}

	public Runnable getOnStop() {
		return anim.getOnStop();
	}

	public boolean async() {
		return anim.isAsync();
	}

	public void async(boolean a) {
		anim.async(a);
	}

	public void pause() {
		anim.pause();
	}

	public void play() {
		anim.play();
	}

	private void setupAnimation() {
		List<T> framez = Utils.sl();
		for (int i = 0; i < frames; i++) {
			try {
				T n = mod.apply(object, i);
				framez.add(n);
			} catch (Exception e) {
			}
		}
		anim = new Animation<T>(framez, (f) -> delay, (f) -> true);
	}

	public T getObject() {
		return object;
	}

	public int getFrames() {
		return frames;
	}

	public int getDelay() {
		return delay;
	}

	public BiFunction<T, Integer, T> getMod() {
		return mod;
	}
}
