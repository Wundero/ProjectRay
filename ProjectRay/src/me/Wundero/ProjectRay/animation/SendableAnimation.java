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
import java.util.function.Function;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Tristate;

public class SendableAnimation<T> extends Animation<T> {

	private final Player player;

	public SendableAnimation(List<T> frames, BiFunction<T, Player, Integer> update, Function<T, Tristate> frameCheck,
			Player sendTo) {
		super(frames, (t) -> {
			return update.apply(t, sendTo);
		}, frameCheck);
		this.player = sendTo;
	}

	public Player getPlayer() {
		return player;
	}

}
