package me.Wundero.ProjectRay;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.Wundero.ProjectRay.framework.PlayerWrapper;
import me.Wundero.ProjectRay.framework.config.Config;
import me.Wundero.ProjectRay.framework.expansion.Expansion;
import me.Wundero.ProjectRay.framework.expansion.ExpansionManager;

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

//will be used when I add sponge/bukkit/spigot/whatever support
public interface IRay {
	File getDirectory();

	Logger getLogger();

	void log(String s, Level l);

	void log(String s);

	String getVersion();

	ExpansionManager getExpansionManager();// TODO remove

	boolean registerExpansion(Expansion e);// TODO remove

	void sendJSON(final String json, final PlayerWrapper<?>... to);

	Config config();

	// TODO get + add commands
}