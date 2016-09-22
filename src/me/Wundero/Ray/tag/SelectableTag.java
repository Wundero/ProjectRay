package me.Wundero.ProjectRay.tag;
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

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.framework.player.RayPlayer;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;

public class SelectableTag extends Tag<Map<String, TextTemplate>> {

	public SelectableTag(String name, Map<String, TextTemplate> object) {
		super(name, object);
	}

	@SuppressWarnings("unchecked")
	public boolean verify(Object o) {
		if (!(o instanceof Map)) {
			return false;
		}
		try {
			Map<String, TextTemplate> m = (Map<String, TextTemplate>) o;// possible
																		// class
																		// cast
			m.put("", TextTemplate.of());// more likely class cast
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public Optional<Text> get(Optional<ParsableData> d) {
		if (!d.isPresent()) {
			return Optional.empty();
		}
		ParsableData data = d.get();
		if (!data.getSender().isPresent()) {
			return Optional.empty();
		}
		Player s = data.getSender().get();
		RayPlayer r = RayPlayer.get(s);
		String v = r.getSelected(this);
		if (v == null) {
			return Optional.empty();
		}
		TextTemplate t = this.object.get(v);
		Map<String, Object> dat = d.get().getKnown().orElse(Utils.sm());
		dat.put("tag:" + v, "");// non recursive variable parse
		ParsableData d2 = d.get();
		d2.setKnown(dat);
		Text t2 = Ray.get().applyVars(t, d2);
		return Optional.ofNullable(t2);
	}
}