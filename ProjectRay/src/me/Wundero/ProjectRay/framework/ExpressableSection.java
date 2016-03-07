package me.Wundero.ProjectRay.framework;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import me.Wundero.ProjectRay.framework.iface.Expressable;

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
public class ExpressableSection extends Section implements Expressable {

	// TODO this class

	public ExpressableSection(ConfigurationSection config) {
		super(config);
	}

	@Override
	public String getExpression() {
		return null;
	}

	@Override
	public String evaluate(OfflinePlayer p, Object... args) {
		return null;
	}

	@Override
	public JSType getType() {
		return null;
	}

	@Override
	public String falseResult() {
		return null;
	}

	@Override
	public String trueResult() {
		return null;
	}

}
