package me.Wundero.Ray.animation;
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

/**
 * Queue of animations - if one is queued, the currently running one is
 * interrupted; if the queued animation is not looping the current animation
 * resumes on termination of the queued animation
 * 
 * UNTESTED CLASS
 */
public class AnimationQueue {

	private Animation<?> currentAnimation;

	public AnimationQueue() {
	}

	/**
	 * Interrupt the currently running animation and start a new one.
	 */
	public void queueAnimation(Animation<?> animation) {
		queueAnimation(animation, false);
	}

	private void queueAnimation(Animation<?> animation, boolean play) {
		if (currentAnimation == null || !currentAnimation.isRunning()) {
			currentAnimation = animation;
			if (play) {
				animation.play();
			} else {
				animation.start();
			}
			return;
		}
		if (animation.isLoop()) {
			currentAnimation.stop();
			currentAnimation = animation;
			animation.start();
			return;
		} else {
			currentAnimation.pause();
			Animation<?> curref = currentAnimation;
			currentAnimation = animation;
			animation.start();
			Runnable r = animation.getOnStop();
			animation.onStop(() -> {
				if (r != null) {
					r.run();
				}
				queueAnimation(curref, true);
			});
		}
	}

}
