package me.Wundero.ProjectRay.listeners;
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.achievement.GrantAchievementEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.Wundero.ProjectRay.Ray;
import me.Wundero.ProjectRay.ValueHolder;
import me.Wundero.ProjectRay.framework.Group;
import me.Wundero.ProjectRay.framework.RayCombinedMessageChannel;
import me.Wundero.ProjectRay.framework.RayPlayer;
import me.Wundero.ProjectRay.framework.channel.ChatChannel;
import me.Wundero.ProjectRay.framework.format.Format;
import me.Wundero.ProjectRay.framework.format.FormatType;
import me.Wundero.ProjectRay.utils.Utils;
import me.Wundero.ProjectRay.variables.ParsableData;

public class MainListener {

	private boolean handle(FormatType t, MessageChannelEvent e, Map<String, Object> v, final Player p,
			MessageChannel channel) {
		return handle(t, e, v, p, channel, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}

	private boolean handle(FormatType t, MessageChannelEvent e, Map<String, Object> v, final Player p,
			MessageChannel channel, Optional<Player> msgsender, Optional<Player> msgrecip, Optional<String> formatName,
			Optional<Player> observer) {
		if (p == null) {
			return false;
		}
		RayPlayer r = RayPlayer.getRay(p);
		Group g = r.getActiveGroup();
		if (g == null) {
			return false;
		}
		Format fx;
		if (formatName.isPresent()) {
			fx = g.getFormat(t, formatName.get());
		} else {
			fx = g.getFormat(t);
		}
		if (fx == null) {
			return false;
		}
		final Format f = fx;
		final Map<String, Object> args = Utils.sm(v);
		ChatChannel pc = r.getActiveChannel();
		boolean obfuscate = pc != null && pc.isObfuscateRanged();
		double range = pc == null ? -1 : pc.range();
		// note that MessageChannel channel is inconsistent and can be many
		// things. Do not infer types with it.

		// RayCombinedMessageChannel allows an individual channel to delete the
		// message.
		MessageChannel newchan = new RayCombinedMessageChannel(channel, new MessageChannel() {
			@Override
			public Optional<Text> transformMessage(Object sender, MessageReceiver recipient, Text original,
					ChatType type) {
				Map<String, Object> mc = Utils.sm(args);
				if (recipient instanceof Player && sender instanceof Player) {
					Player s = (Player) sender;
					Player r = (Player) recipient;
					if (RayPlayer.getRay(r).isIgnoring(RayPlayer.getRay(s))) {
						return Optional.empty();
					}
					if (obfuscate && !Utils.inRange(r.getLocation(), s.getLocation(), range)) {
						double delta = Utils.difference(s.getLocation(), r.getLocation());
						// percent difference calc adjusted to make the percent
						// larger but never above 100 (range is 10% smaller than
						// normal)
						// lim(delta->infinity) (func %dc) = 100
						double percentDiscoloration = ((delta - (range / 1.1)) / delta) * 100;
						double percentObfuscation = Math.sqrt(percentDiscoloration);
						Text m = Utils.obfuscate((Text) mc.get("message"), percentObfuscation, percentDiscoloration);
						mc.put("message", m);
					} else if (!Utils.inRange(s.getLocation(), r.getLocation(), range)) {
						return Optional.empty();
					}
				}
				// prolly count have used a supplieer here but meh
				ValueHolder<Text> vv = new ValueHolder<Text>();
				if (!f.send((text) -> {
					if (vv.getValue() != null) {
						return false;
					}
					vv.setValue(text);
					return true;
				}, new ParsableData().setKnown(mc).setSender(msgsender.orElse(p))
						.setRecipient(msgrecip.orElse(recipient instanceof Player ? (Player) recipient : null))
						.setClickHover(true).setObserver(observer))) {
					return Optional.empty();
				}
				return Optional.of(vv.getValue());
			}

			@Override
			public Collection<MessageReceiver> getMembers() {
				return Collections.emptyList();
			}

		});
		e.setChannel(newchan);
		return false;
	}

	@Listener
	public void onChat(MessageChannelEvent.Chat event) {
		Map<String, Object> vars = Utils.sm();
		Player p = null;
		if (event.getCause().containsType(Player.class)) {
			p = (Player) event.getCause().first(Player.class).get();
		}
		vars.put("message", Utils.transIf(event.getRawMessage().toPlain(), p));
		if (event.getCause().containsNamed("formattype")) {
			Optional<Player> sf = Optional.empty();
			Optional<Player> st = Optional.empty();
			Optional<String> fn = Optional.empty();
			Optional<Player> o = Optional.empty();
			if (event.getCause().containsNamed("sendfrom")) {
				sf = event.getCause().get("sendfrom", Player.class);
			}
			if (event.getCause().containsNamed("sendto")) {
				st = event.getCause().get("sendto", Player.class);
			}
			if (event.getCause().containsNamed("formatname")) {
				fn = event.getCause().get("formatname", String.class);
			}
			if (event.getCause().contains("observer")) {
				o = event.getCause().get("observer", Player.class);
			}
			event.setCancelled(handle(event.getCause().get("formattype", FormatType.class).get(), event, vars, p,
					event.getChannel().get(), sf, st, fn, o));
		} else {
			event.setCancelled(handle(FormatType.CHAT, event, vars, p, event.getChannel().get()));
		}
	}

	@Listener
	public void onJoin(ClientConnectionEvent.Join event) {
		boolean welcome = !event.getTargetEntity().hasPlayedBefore();
		Ray.get().setLoadable(event.getTargetEntity());
		final RayPlayer p = RayPlayer.get(event.getTargetEntity());
		p.setTabTask(() -> {
			Player player = event.getTargetEntity();
			final TabList list = player.getTabList();
			List<TabListEntry> lx = Utils.sl(list.getEntries());
			for (TabListEntry e : lx) {
				Optional<Player> h = Sponge.getServer().getPlayer(e.getProfile().getUniqueId());
				if (!h.isPresent()) {
					continue;
				}
				Player pla = h.get();
				RayPlayer plx = RayPlayer.get(pla);
				Group g = plx.getActiveGroup();
				if (g == null) {
					continue;
				}
				Format f = g.getFormat(FormatType.TABLIST_ENTRY);
				if (f == null) {
					continue;
				}
				f.send((text) -> {
					TabListEntry e2 = e;
					if (!list.getEntry(e.getProfile().getUniqueId()).isPresent()) {
						return false;
					}
					if (!(list.getEntry(e.getProfile().getUniqueId()).get().equals(e))) {
						e2 = list.getEntry(e.getProfile().getUniqueId()).get();
					}
					e2.setDisplayName(text);
					return true;
				}, new ParsableData().setClickHover(false).setSender(pla).setRecipient(player));
			}
		});
		p.startTabHFTask();
		Group g = p.getActiveGroup();
		if (g != null) {
			Format h = g.getFormat(FormatType.TABLIST_HEADER);
			Format f = g.getFormat(FormatType.TABLIST_FOOTER);
			if (h != null) {
				h.send(text -> {
					p.queueHeader(text);
					return true;
				}, new ParsableData().setClickHover(false).setSender(event.getTargetEntity())
						.setRecipient(event.getTargetEntity()));
			}
			if (f != null) {
				f.send(text -> {
					p.queueHeader(text);
					return true;
				}, new ParsableData().setClickHover(false).setSender(event.getTargetEntity())
						.setRecipient(event.getTargetEntity()));
			}
		}

		// TODO tablist header/footer

		if (event.getChannel().isPresent()) {
			event.setMessageCancelled(true);
			Task.builder().delayTicks(10).execute(() -> {
				MessageChannelEvent.Chat ev2 = SpongeEventFactory.createMessageChannelEventChat(
						Cause.builder().from(event.getCause()).named("formattype", FormatType.JOIN).build(),
						event.getChannel().get(), event.getChannel(), event.getFormatter(), event.getMessage(), false);
				Sponge.getEventManager().post(ev2);
				if (!ev2.isCancelled()) {
				}
			}).submit(Ray.get().getPlugin());
			Task.builder().delayTicks(20).execute(() -> {
				MessageChannelEvent.Chat ev2 = SpongeEventFactory.createMessageChannelEventChat(
						Cause.builder().from(event.getCause()).named("formattype", FormatType.JOIN).build(),
						event.getChannel().get(), event.getChannel(), event.getFormatter(), event.getMessage(), false);
				Sponge.getEventManager().post(ev2);
				if (!ev2.isCancelled()) {
					ev2.getChannel().get().send(event.getTargetEntity(), ev2.getMessage(), ChatTypes.CHAT);
				}
			}).submit(Ray.get().getPlugin());
		}
		if (welcome) {
			Task.builder().delayTicks(15).execute(() -> {
				MessageChannelEvent.Chat ev2 = SpongeEventFactory.createMessageChannelEventChat(
						Cause.builder().from(event.getCause()).named("formattype", FormatType.WELCOME).build(),
						MessageChannel.TO_ALL, Optional.of(MessageChannel.TO_ALL),
						new MessageEvent.MessageFormatter(Text.of(TextColors.LIGHT_PURPLE,
								"Welcome " + event.getTargetEntity().getName() + " to the server!")),
						event.getMessage(), false);
				Sponge.getEventManager().post(ev2);
				if (!ev2.isCancelled()) {
					ev2.getChannel().get().send(event.getTargetEntity(), ev2.getMessage(), ChatTypes.CHAT);
				}
			}).submit(Ray.get().getPlugin());
		}
		Task.builder().delayTicks(15).execute(() -> {
			MessageChannelEvent.Chat ev2 = SpongeEventFactory.createMessageChannelEventChat(
					Cause.builder().from(event.getCause()).named("formattype", FormatType.MOTD).build(),
					MessageChannel.fixed(event.getTargetEntity()),
					Optional.of(MessageChannel.fixed(event.getTargetEntity())),
					new MessageEvent.MessageFormatter(Text.of(TextColors.LIGHT_PURPLE, "Welcome to the server!")),
					event.getMessage(), false);
			Sponge.getEventManager().post(ev2);
			if (!ev2.isCancelled()) {
				ev2.getChannel().get().send(event.getTargetEntity(), ev2.getMessage(), ChatTypes.CHAT);
			}
		}).submit(Ray.get().getPlugin());
		Task.builder().delayTicks(30).execute(() -> {
			RayPlayer.updateTabs();
			p.updateTab();
		}).submit(Ray.get().getPlugin());
	}

	// Logs ALL commands that are handled by the server
	@Listener
	public void onCommand(SendCommandEvent event) {
		String player = "";
		if (event.getCause().containsType(CommandSource.class)) {
			player = event.getCause().first(CommandSource.class).get().getName() + ": ";
		}
		Ray.get().getLogger().info(player + "/" + event.getCommand() + " " + event.getArguments());
	}

	@Listener
	public void onQuit(ClientConnectionEvent.Disconnect event) {
		Map<String, Object> vars = Utils.sm();
		event.setMessageCancelled(
				handle(FormatType.LEAVE, event, vars, event.getTargetEntity(), event.getChannel().get()));
		RayPlayer.updateTabs();
	}

	// TODO figure out way to customize messages - locale is retrivable so use
	// translations?

	/*
	 * For now, I'm going to skip working on this. I cannot figure out a simple
	 * way to properly handle this; it will take a lot of config (either
	 * translations *ugh* or a terrible .conf file) and will likely be
	 * bug-ridden. If I can think of a simple, consistent method of handling
	 * this that works with what I have completed to this point, I will
	 * implement that. For now, I will just leave this as dead code.
	 */
	@Listener
	public void onDeath(DestructEntityEvent.Death event) {
		// for now, not handling deaths. This weird check is so that I don't get
		// dead code errors.
		if (!(event.getTargetEntity() instanceof Player) || event instanceof Event) {
			return;
		}
		Map<String, Object> vars = Utils.sm();
		String text = event.getOriginalMessage().toString();
		String firstpart = "Text\\{children\\=\\[Text\\{children\\=\\[Text\\{children\\=\\[Text\\{children\\=\\[Text\\{SpongeTranslation\\{id\\=[A-Za-z0-9\\.]+\\}\\,";
		Matcher m = Pattern.compile(firstpart).matcher(text);
		m.find();
		text = m.group();
		text = text.substring(
				"Text{children=[Text{children=[Text{children=[Text{children=[Text{SpongeTranslation{id=".length(),
				text.length() - 2);
		System.out.println(text);
		// text is what client sees as translatable
		// TODO translate this and figure out how to parse variables n stuff
		String json = "{\"translate\":\"" + text + "\",\"with\":[\"DEAD\",\"KILLER\",\"ITEM\"]}";
		Optional<DamageSource> s = event.getCause().first(DamageSource.class);
		Optional<Player> killer = Optional.empty();
		Optional<ItemStack> used = Optional.empty();
		if (s.isPresent()) {
			DamageSource source = s.get();
			if (source instanceof IndirectEntityDamageSource) {
				IndirectEntityDamageSource ieds = (IndirectEntityDamageSource) source;
				Entity k = ieds.getSource();
				if (!(k instanceof Living)) {
					k = ieds.getIndirectSource();
				}
				if (k instanceof Player) {
					Player p = (Player) k;
					used = p.getItemInHand(HandTypes.MAIN_HAND);
					if (used.isPresent()) {
						Optional<Text> t = used.get().get(Keys.DISPLAY_NAME);
						if (t.isPresent()) {
							vars.put("item",
									Text.of("using ")
											.concat(Text.builder("[").append(t.get()).append(Text.of("]"))
													.color(TextColors.AQUA).style(TextStyles.ITALIC)
													.onHover(TextActions.showItem(used.get())).build()));
						}
					}
					killer = Optional.of(p);
				} else {
					String name = k.getType().getName();
					Text.Builder b = Text.builder(name);
					if (k.get(Keys.DISPLAY_NAME).isPresent()) {
						b = k.get(Keys.DISPLAY_NAME).get().toBuilder();
						name = k.get(Keys.DISPLAY_NAME).get().toPlain();
					}
					vars.put("killer", b.onHover(TextActions.showEntity(k, name)));
				}
			} else if (source instanceof EntityDamageSource) {
				Entity k = ((EntityDamageSource) source).getSource();
				if (k instanceof Player) {
					Player p = (Player) k;
					used = p.getItemInHand(HandTypes.MAIN_HAND);
					if (used.isPresent()) {
						Optional<Text> t = used.get().get(Keys.DISPLAY_NAME);
						if (t.isPresent()) {
							vars.put("item",
									Text.of("using ")
											.concat(Text.builder("[").append(t.get()).append(Text.of("]"))
													.color(TextColors.AQUA).style(TextStyles.ITALIC)
													.onHover(TextActions.showItem(used.get())).build()));
						}
					}
					killer = Optional.of(p);
				} else {
					String name = k.getType().getName();
					Text.Builder b = Text.builder(name);
					if (k.get(Keys.DISPLAY_NAME).isPresent()) {
						b = k.get(Keys.DISPLAY_NAME).get().toBuilder();
						name = k.get(Keys.DISPLAY_NAME).get().toPlain();
					}
					vars.put("killer", b.onHover(TextActions.showEntity(k, name)));
				}
			}
		}
		((Player) event.getTargetEntity()).sendMessage(TextSerializers.JSON.deserialize(json));
		event.setMessageCancelled(handle(FormatType.DEATH, event, vars, (Player) event.getTargetEntity(),
				event.getChannel().orElse(event.getOriginalChannel()), Optional.empty(), Optional.empty(),
				Optional.empty(), killer));
	}

	@Listener
	public void onKick(KickPlayerEvent event) {
		Map<String, Object> vars = Utils.sm();
		event.setMessageCancelled(
				handle(FormatType.KICK, event, vars, event.getTargetEntity(), event.getChannel().get()));
		RayPlayer.updateTabs();
	}

	@Listener
	public void onAch(GrantAchievementEvent.TargetPlayer event) {
		Map<String, Object> vars = Utils.sm();
		Achievement ach = event.getAchievement();
		vars.put("achievement",
				Text.builder().append(Text.of(ach.getName())).onHover(TextActions.showAchievement(ach)).build());
		event.setMessageCancelled(
				handle(FormatType.ACHIEVEMENT, event, vars, event.getTargetEntity(), event.getChannel().get()));

	}

}
