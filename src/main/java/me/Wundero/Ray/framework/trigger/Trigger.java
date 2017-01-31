package me.Wundero.Ray.framework.trigger;

import java.util.List;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;

import me.Wundero.Ray.Ray;
import me.Wundero.Ray.framework.Group;
import me.Wundero.Ray.framework.format.Format;
import me.Wundero.Ray.utils.RayCollectors;
import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

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

@ConfigSerializable
public abstract class Trigger {

	@Setting
	protected List<String> formats = Utils.al();
	private Group group;
	private List<Format> fmts = Utils.al();

	public int trigger(ParsableData data, List<Player> targets) {
		int count = 0;
		for (Format f : fmts) {
			UUID uuid = UUID.randomUUID();
			for (Player t : targets) {
				ParsableData d2 = data.copy().setRecipient(t);
				FormatEvent e = new FormatEvent(Cause.source(Ray.get().getPlugin()).build(), f, d2);
				if (!Sponge.getEventManager().post(e)) {
					boolean s = e.getFormat().send(e.getData().getRecipient().orElse(t), e.getData(),
							e.getData().getSender().orElse(data.getSender().orElse(null)), uuid, false, false);
					if (s) {
						count++;
					}
				}
			}
		}
		return count;
	}

	public void setGroup(Group g) {
		this.group = g;
		this.fmts = g.getAllFormats().get().stream().filter(f -> formats.contains(f.getName()))
				.collect(RayCollectors.rayList());
	}
}
