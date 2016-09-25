package me.Wundero.Ray.quote;

import java.util.UUID;

import org.spongepowered.api.entity.living.player.User;

import me.Wundero.Ray.config.RaySerializable;
import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

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

public class Quote implements RaySerializable {

	private String value;
	private User owner;

	@Override
	public void serialize(ConfigurationNode onto) throws ObjectMappingException {
		onto.getNode("quote").setValue(value);
		onto.getNode("owner").setValue(owner.getUniqueId().toString());
	}

	@Override
	public void deserialize(ConfigurationNode from) throws ObjectMappingException {
		this.value = from.getNode("quote").getString();
		this.owner = Utils.getUser(UUID.fromString(from.getNode("owner").getString()));
	}

}
