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

public class Animation<T> {
	private final List<T> frames;
	private final Function<T, Integer> update;
	private final Function<T, Boolean> frameCheck;

	private boolean loop, running;
	private Runnable stop;
	private Iterator<T> iter;

	public Animation(List<T> frames, Function<T, Integer> update, Function<T, Boolean> frameCheck) {
		this.frames = frames;
		this.update = update;
		this.frameCheck = frameCheck;
		iter = frames.iterator();
	}

	public void update(final T frame) {
		if (!frameCheck.apply(frame)) {
			stop();
		}
		if (!running) {
			return;
		}
		int delayTicks = update.apply(frame);
		if (delayTicks < 0) {
			stop();
		}
		if (!running) {
			return;
		}
		if (!iter.hasNext() && loop) {
			iter = frames.iterator();
		}
		if (iter.hasNext()) {
			Utils.schedule(Task.builder().execute(() -> update(iter.next())).delayTicks(delayTicks));
		} else {
			stop();
		}
	}

	public void start() {
		if (!running) {
			running = true;
			update(iter.next());
		}
	}

	public void stop() {
		if (running) {
			running = false;
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

	public void onStop(Runnable s) {
		this.stop = s;
	}

}
