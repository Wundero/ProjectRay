package me.Wundero.ProjectRay.animation;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.spongepowered.api.scheduler.Task;

import me.Wundero.ProjectRay.utils.Utils;

/*
 The MIT License (MIT)

 Copyright (c) 2016 Wundero

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the zfollowing conditions:

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

/**
 * generic animation class
 */
public class Animation<T> {

	private final List<T> frames; // list of objects
	private final Function<T, Integer> update; // apply frame + return delay to
												// next frame
	private final Function<T, Boolean> frameCheck; // check to see if frame can
													// be displayed

	// loop is if the anim is to repeat, running is state detection
	private boolean loop = true, running = false, async = false;
	private Runnable stop;// what happens on stop
	private Iterator<T> iter; // frames iter - removes need for index var

	public Animation(List<T> frames, Function<T, Integer> update, Function<T, Boolean> frameCheck) {
		this.frames = frames;
		this.update = update;
		this.frameCheck = frameCheck;
		iter = frames.iterator();
	}

	public boolean isRunning() {
		return running;
	}

	public void update(final T frame) {// display frame call
		if (!frameCheck.apply(frame)) {
			// if frame cannot be displayed
			stop();
		}
		if (!running) {// if not running
			return;
		}
		// apply frame
		int delayTicks = update.apply(frame);
		if (delayTicks < 0) {
			// if no more frames or frame display went wrong
			stop();
		}
		if (!running) {
			return;
		}
		// if we've run out of frames and we need to loop
		if (!iter.hasNext() && loop) {
			iter = frames.iterator();
		}
		// if we can proceed
		if (iter.hasNext()) {
			// execute next frame update with a delay set by the current frame
			cancellable = Utils.schedule(
					Task.builder().execute(() -> update(curFrame = iter.next())).delayTicks(delayTicks), async);
		} else {
			stop();
		}
	}

	private T curFrame;
	private Task cancellable;

	public void start() {// execute first frame update if possible
		if (!running && iter.hasNext()) {
			running = true;
			update(curFrame = iter.next());
		}
	}

	public void play() {
		if (!running) {
			running = true;
			update(curFrame);
		}
	}

	public void pause() {
		if (running) {
			running = false;
			cancellable.cancel();
		}
	}

	public void stop() {
		if (running) {
			running = false;
			cancellable.cancel();
			if (stop != null) {
				stop.run();
			}
		}
	}

	public boolean isLoop() {
		return loop;
	}

	public void loop(boolean b) {
		this.loop = b;
	}

	public void async(boolean b) {
		this.async = b;
	}

	public boolean isAsync() {
		return async;
	}

	public Runnable getOnStop() {
		return stop;
	}

	public void onStop(Runnable s) {
		this.stop = s;
	}

}
