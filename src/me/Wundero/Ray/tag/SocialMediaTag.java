package me.Wundero.Ray.tag;
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

import java.net.URL;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import me.Wundero.Ray.framework.player.RayPlayer;
import me.Wundero.Ray.framework.player.SocialMedia;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class SocialMediaTag extends Tag<SocialMedia> {

	public SocialMediaTag(String name, SocialMedia medium) {
		super(name, medium);
	}

	@Override
	public boolean verify(Object o) {
		return o instanceof SocialMedia;
	}

	@Override
	public Optional<Text> get(Optional<ParsableData> data) {
		if (object == null) {
			return Optional.empty();
		}
		if (data.isPresent()) {
			ParsableData d = data.get();
			if (!hasPermission(d)) {
				return Optional.empty();
			}
			if (d.getSender().isPresent()) {
				Player s = d.getSender().get();
				RayPlayer rp = RayPlayer.get(s);
				URL u = rp.getMediaURL(this.getObject());
				if (u != null) {
					return Optional.of(getObject().formatTag(u));
				} else {
					return Optional.empty();
				}
			} else {
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void serialize(ConfigurationNode onto) throws ObjectMappingException {
		onto.getNode("medium").setValue(this.object.name().toLowerCase());
	}

	@Override
	public void deserialize(ConfigurationNode from) throws ObjectMappingException {
		this.object = SocialMedia.fromString(from.getNode("medium").getValue().toString());
	}

}
