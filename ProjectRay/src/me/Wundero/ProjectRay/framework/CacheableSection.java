package me.Wundero.ProjectRay.framework;

import java.util.HashMap;
import java.util.List;

import me.Wundero.ProjectRay.fanciful.FancyMessage;
import me.Wundero.ProjectRay.framework.iface.Cacheable;
import me.Wundero.ProjectRay.utils.PRTimeUnit;
import me.Wundero.ProjectRay.utils.Utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
public class CacheableSection extends Section implements Cacheable {

	// expirable data cache (10 mins) TODO configurable expiry
	private DataHolder cache = new DataHolder() {

		private HashMap<String, Long> expiries = Maps.newHashMap();

		@Override
		public void putData(final String key, Object value) {
			super.putData(key, value);
			expiries.put(key, ((System.nanoTime() + (long) Utils.convert(10,
					PRTimeUnit.MINUTES, PRTimeUnit.NANOSECONDS))));
			Utils.sync(new Runnable() {// 10 mins later run
						@Override
						public void run() {
							purge(key);
						}
					}, 20 * (long) (Utils.convert(10, PRTimeUnit.MINUTES,
							PRTimeUnit.SECONDS)));
		}

		private List<String> locked = Lists.newArrayList();

		public synchronized void purge(String key) {
			locked.add(key);
			this.removeData(key);
			expiries.remove(key);
			locked.remove(key);
		}

		@Override
		public boolean hasData(String key) {
			if (locked.contains(key)) {
				return false;
			}
			if (super.hasData(key)) {
				if (System.nanoTime() > expiries.get(key)) {
					purge(key);
					return false;
				}
				return true;
			}
			return false;
		}

	};

	public CacheableSection(ConfigurationSection config) {
		super(config);
	}

	@Override
	public void clear() {
		cache.clearData();
	}

	@Override
	public FancyMessage getMessage(OfflinePlayer player, OfflinePlayer[] others) {
		String s = compileToKey(player, others);
		if (cache.hasData(s)) {
			return cache.getData(s);
		}
		FancyMessage out = super.getMessage(player, others);
		cache(player, others, out);
		return out;
	}

	private String compileToKey(OfflinePlayer p, OfflinePlayer[] o) {
		if (p == null) {
			return "noplayer";
		}
		String out = "" + p.getUniqueId().toString();
		if (o != null && o.length > 0) {
			for (OfflinePlayer pl : o) {
				if (pl == null) {
					continue;
				}
				out += "" + pl.getUniqueId().toString();
			}
		}
		return out;
	}

	public void cache(OfflinePlayer player, OfflinePlayer[] others,
			FancyMessage result) {
		String s = compileToKey(player, others);
		if (cache.getData(s).equals(result)) {
			return;
		}
		cache.putData(s, result);
	}

}
