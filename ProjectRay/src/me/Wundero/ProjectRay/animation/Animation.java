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

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import com.google.common.collect.Lists;

import me.Wundero.ProjectRay.Ray;

public class Animation {
	private final FrameSequence animation;
	private final Player player;

	private List<Task.Builder> tasks = Lists.newArrayList();
	private List<String> taskids = Lists.newArrayList();
	private Task cancellableTask;

	private boolean running;
	private boolean repeat;

	public Animation(FrameSequence animation, Player to) {
		this.animation = animation;
		this.player = to;
		for (Frame f : animation.getFrames()) {
			Task.Builder tb = Task.builder().delayTicks(f.getDelayTicks())
					.name("RayFrameTo" + to.getName() + "-S-" + animation.getFrames().indexOf(f)).execute((task) -> {
						if (!f.send(player)) {
							stop();
							return;
						}
						int index = taskids.indexOf(task.getName()) + 1;
						if (tasks.size() == index) {
							if (!repeat) {
								return;
							}
							index = 0;
						}
						Task.Builder t2 = tasks.get(index);
						cancellableTask = t2.submit(Ray.get().getPlugin());
					});
			tasks.add(tb);
			taskids.add("RayFrameTo" + to.getName() + "-S-" + animation.getFrames().indexOf(f));
		}
	}

	public void stop() {
		setRunning(false);
		cancellableTask.cancel();
	}

	public void start() {
		setRunning(true);
		cancellableTask = tasks.get(0).submit(Ray.get().getPlugin());
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
