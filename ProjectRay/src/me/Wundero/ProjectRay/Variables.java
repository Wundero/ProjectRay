package me.Wundero.ProjectRay;
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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

public class Variables {
	// Doing it the lame way for now
	public Object get(String key, Object... objects) {
		switch (key) {
		case "online":
			return Sponge.getServer().getOnlinePlayers().size();
		case "displayname":
			if (objects[0] == null) {
				return "";
			}
			return ((Player) objects[0]).get(Keys.DISPLAY_NAME).isPresent()
					? ((Player) objects[0]).get(Keys.DISPLAY_NAME).get() : ((Player) objects[0]).getName();
		case "player":
			if (objects[0] == null) {
				return "";
			}
			return ((Player) objects[0]).getName();
		}
		return "";
	}
}
