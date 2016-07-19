package me.Wundero.ProjectRay.helper;
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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.utils.Usable;
import me.Wundero.ProjectRay.utils.Utils;

@SuppressWarnings("unused")
public abstract class ObjectBuilder {
	//This is probably useless but ill keep it for now
	private BiMap<String, Method> invokable = HashBiMap.create();
	private Method build;
	private Class<?> clazz;
	private Object instance;
	private Player pl;
	private String commandName;
	private CommandSpec command = null;
	private Task inputTask = null;

	public ObjectBuilder(Player player, Object emptyInstance, Map<String, Method> invoke, Method builder) {
		this.pl = player;
		this.instance = emptyInstance;
		this.clazz = instance.getClass();
		this.invokable = HashBiMap.create(invoke);
		this.build = builder;
		this.command = CommandSpec.builder().arguments(GenericArguments.choices(Text.of("Part"), invokable))
				.executor(new Executor()).build();
		this.commandName = "click" + UUID.randomUUID();
		Sponge.getCommandManager().register(Ray.get().getPlugin(), command, commandName);
		verifyInvokable();
	}

	protected Text[] getOptions() {
		Text[] out = new Text[invokable.keySet().size()];
		int i = 0;
		for (String s : invokable.keySet()) {
			Text t = Text.builder().append(Text.of(s)).color(TextColors.AQUA).style(TextStyles.BOLD)
					.onClick(TextActions.runCommand(commandName + " " + s)).build();
			out[i] = t;
			i++;
		}
		return out;
	}

	private void verifyInvokable() {
		for (String name : invokable.keySet()) {
			Method m = invokable.get(name);
			for (Class<?> c : m.getParameterTypes()) {
				if (!c.equals(String.class) || !c.equals(Object.class)) {
					invokable.remove(name);
				}
			}
		}
	}

	public abstract void displayOptions(Text[] options);

	public abstract void getInput(Usable<String> cmd, String key);

	public abstract Text getSuccessfullClick();

	public abstract Text getFailedClick();

	public class Executor implements CommandExecutor {
		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (!(src instanceof Player) || !((Player) src).getUniqueId().equals(pl.getUniqueId())) {
				src.sendMessage(getFailedClick());
				return CommandResult.empty();
			}
			Optional<Method> selected = args.getOne("Part");
			if (!selected.isPresent()) {
				src.sendMessage(getFailedClick());
				return CommandResult.empty();
			}
			final Method method = selected.get();
			final String key = invokable.inverse().get(method);
			Usable<String> c = (value) -> {
				try {
					boolean a = method.isAccessible();
					method.setAccessible(true);
					method.invoke(instance, value);
					method.setAccessible(a);
				} catch (Exception e) {
					Utils.printError(e);
				}
			};
			getInput(c, key);
			src.sendMessage(getSuccessfullClick());
			return CommandResult.success();
		}

	}
}
