package me.Wundero.ProjectRay.framework;

import org.bukkit.OfflinePlayer;

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
public class RayPlayer extends DataHolder {
	private OfflinePlayer player;
	private String lastName;

	// TODO insert map of Location > Group (location as in bungee/server/world,
	// not bukkit loc)

	public String getLastName() {
		return lastName;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	// Not worth it to implement yet
	/*
	 * public void cacheVariableResult(String var, String[] data, Object result)
	 * {
	 * 
	 * this.putData(jvd(var, data), result); }
	 * 
	 * private String jvd(String var, String[] data) { List<String> list =
	 * Lists.newArrayList("variable: " + var); for (String s : data) {
	 * list.add(s); } return Utils.join("|", list); }
	 * 
	 * public boolean hasVariableCache(String var, String[] data) { return
	 * this.hasData(jvd(var, data)); }
	 * 
	 * public Object getVariableCache(String var, String[] data) { if
	 * (!hasVariableCache(var, data)) { return null; } return
	 * this.getData(jvd(var, data)); }
	 */
}
