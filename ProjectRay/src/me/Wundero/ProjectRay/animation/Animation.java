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

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import me.Wundero.ProjectRay.Ray;

public class Animation {
	private final FrameSequence animation;
	private final Player player;

	private Task task;

	private boolean running;
	private boolean repeat;

	private int index = 0;
	private int ticksTillNext = 0;

	public Animation(FrameSequence animation, Player to) {
		this.animation = animation;
		this.player = to;
		ttn();
	}

	private void ttn() {
		ticksTillNext = animation.getFrames().get(index).getDelayTicks();
	}

	public void stop() {
		setRunning(false);
		task.cancel();
	}

	public void start() {
		setRunning(true);
		task = Task.builder().delayTicks(1).execute(() -> {
			if (ticksTillNext <= 0) {
				if (repeat && index >= animation.getFrames().size()) {
					index = 0;
				} else {
					stop();
					return;
				}
				animation.getFrames().get(index).send(player);
				index++;
				ttn();
			} else {
				ticksTillNext--;
			}
		}).submit(Ray.get().getPlugin());
	}

	public FrameSequence getAnimation() {
		return animation;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		if (running) {
			return;
		}
		this.repeat = repeat;
	}

	public boolean isRunning() {
		return running;
	}

	private void setRunning(boolean running) {
		this.running = running;
	}

}
