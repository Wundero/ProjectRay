package me.Wundero.ProjectRay;

import java.io.File;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import me.Wundero.ProjectRay.framework.PlayerWrapper;

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

@Plugin(id = "ray", name = "Ray", version = "v0.0.1")
public class ProjectRay implements IRay {

	@Inject
	private Logger logger;
	@Inject
	private Game game;

	@Override
	public File getDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public void log(String s) {
		getLogger().info(s);
	}

	public Game getGame() {
		return game;
	}

	@Override
	public String getVersion() {
		Optional<String> opt = game.getPluginManager().getPlugin("ray").get().getVersion();
		if (!opt.isPresent()) {
			return "v0.0.1";
		}
		return opt.get();
	}

	@Override
	public void sendJSON(String json, PlayerWrapper<?>... to) {
		// TODO Auto-generated method stub

	}

}
