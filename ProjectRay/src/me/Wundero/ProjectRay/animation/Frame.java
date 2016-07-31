package me.Wundero.ProjectRay.animation;

import org.spongepowered.api.entity.living.player.Player;

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

public class Frame {
	private Sendable sendable;
	private int delayTicks;

	public Frame(Sendable s, int d) {
		this.setSendable(s);
		this.setDelayTicks(d);
	}

	public Sendable getSendable() {
		return sendable;
	}

	public void setSendable(Sendable sendable) {
		this.sendable = sendable;
	}

	public int getDelayTicks() {
		return delayTicks;
	}

	public void setDelayTicks(int delayTicks) {
		this.delayTicks = delayTicks;
	}

	public boolean send(Player to) {
		return sendable.send(to);
	}
}