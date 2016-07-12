package me.Wundero.ProjectRay;

import me.Wundero.ProjectRay.framework.Groups;
import me.Wundero.ProjectRay.framework.command.CommandRegistrar;
import me.Wundero.ProjectRay.framework.config.Config;
import me.Wundero.ProjectRay.framework.language.Messages;
import me.Wundero.ProjectRay.variables.Parser;
import me.Wundero.ProjectRay.variables.Store;

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

public class Ray {

	private static Ray singleton = new Ray();

	private Ray() {
	}

	public static Ray get() {
		return singleton;
	}

	private IRay plugin;
	private Parser varParser;
	private Store varStore;
	private Groups grouplist;
	private Messages messages;
	private Config config;
	private CommandRegistrar commands;

	public void load(IRay plugin) {
		this.setPlugin(plugin);
		this.setVarParser(new Parser());
		this.setVarStore(new Store());
		this.setConfig(plugin.config());
		this.setGrouplist(new Groups(getConfig()));
		this.setMessages(new Messages());
		this.setCommands(new CommandRegistrar());
	}

	public IRay getPlugin() {
		return plugin;
	}

	private void setPlugin(IRay plugin) {
		this.plugin = plugin;
	}

	public Store getStore() {
		return varStore;
	}

	private void setVarStore(Store varStore) {
		this.varStore = varStore;
	}

	public Parser getParser() {
		return varParser;
	}

	private void setVarParser(Parser varParser) {
		this.varParser = varParser;
	}

	public Messages getMessages() {
		return messages;
	}

	private void setMessages(Messages messages) {
		this.messages = messages;
	}

	public Groups getGrouplist() {
		return grouplist;
	}

	private void setGrouplist(Groups grouplist) {
		this.grouplist = grouplist;
	}

	public Config getConfig() {
		return config;
	}

	private void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * @return the commands
	 */
	public CommandRegistrar getCommands() {
		return commands;
	}

	/**
	 * @param commands
	 *            the commands to set
	 */
	public void setCommands(CommandRegistrar commands) {
		this.commands = commands;
	}
}
