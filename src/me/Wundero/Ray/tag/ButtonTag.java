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

import java.util.Optional;

import org.spongepowered.api.text.Text;

import me.Wundero.Ray.utils.Utils;
import me.Wundero.Ray.variables.ParsableData;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class ButtonTag extends Tag<Text> {

	public ButtonTag(String name, Text object) {
		super(name, object);
	}

	@Override
	public void serialize(ConfigurationNode onto) throws ObjectMappingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deserialize(ConfigurationNode from) throws ObjectMappingException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean verify(Object o) {
		return o instanceof Text;
	}

	@Override
	public Optional<Text> get(Optional<ParsableData> data) {
		if (data.isPresent() && !hasPermission(data.get())) {
			return Optional.empty();
		}
		return Utils.wrap(object);
	}

}
