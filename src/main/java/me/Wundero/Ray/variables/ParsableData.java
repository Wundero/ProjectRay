package me.Wundero.Ray.variables;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;

import me.Wundero.Ray.utils.Utils;

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

/**
 * Represents data that a variable may require for parsing.
 */
public class ParsableData {
	private Optional<Map<String, Object>> known = Optional.empty();
	private Optional<Player> sender = Optional.empty();
	private Optional<Player> recipient = Optional.empty();
	private Optional<Player> observer = Optional.empty();
	private Optional<Integer> page = Optional.empty();
	private boolean clickHover = true;

	public ParsableData() {
	}

	/**
	 * @return the known data
	 */
	public Optional<Map<String, Object>> getKnown() {
		return known;
	}

	/**
	 * Set the known data
	 */
	public ParsableData setKnown(Map<String, Object> known) {
		return setKnown(Optional.ofNullable(known));
	}

	/**
	 * Set the known data
	 */
	public ParsableData withKnown(Map<String, Object> known) {
		return setKnown(known);
	}

	/**
	 * Set the known data
	 */
	public ParsableData withKnown(Optional<Map<String, Object>> known) {
		return setKnown(known);
	}

	/**
	 * Set the known data
	 */
	public ParsableData setKnown(Optional<Map<String, Object>> known) {
		this.known = known;
		return this;
	}

	/**
	 * @return the sender
	 */
	public Optional<Player> getSender() {
		return sender;
	}

	/**
	 * @param sender
	 *            the sender to set
	 */
	public ParsableData setSender(Player sender) {
		return setSender(Optional.ofNullable(sender));
	}

	/**
	 * @param sender
	 *            the sender to set
	 */
	public ParsableData withSender(Player sender) {
		return setSender(sender);
	}

	/**
	 * @param sender
	 *            the sender to set
	 */
	public ParsableData withSender(Optional<Player> sender) {
		return setSender(sender);
	}

	/**
	 * @param sender
	 *            the sender to set
	 */
	public ParsableData setSender(Optional<Player> sender) {
		this.sender = sender;
		return this;
	}

	/**
	 * @return the recipient
	 */
	public Optional<Player> getRecipient() {
		return recipient;
	}

	/**
	 * @param recipient
	 *            the recipient to set
	 */
	public ParsableData setRecipient(Player recipient) {
		return setRecipient(Optional.ofNullable(recipient));
	}

	/**
	 * @param recipient
	 *            the recipient to set
	 */
	public ParsableData withRecipient(Player recipient) {
		return setRecipient(recipient);
	}

	/**
	 * @param recipient
	 *            the recipient to set
	 */
	public ParsableData withRecipient(Optional<Player> recipient) {
		return setRecipient(recipient);
	}

	/**
	 * @param recipient
	 *            the recipient to set
	 */
	public ParsableData setRecipient(Optional<Player> recipient) {
		this.recipient = recipient;
		return this;
	}

	/**
	 * @return the observer
	 */
	public Optional<Player> getObserver() {
		return observer;
	}

	/**
	 * @param observer
	 *            the observer to set
	 */
	public ParsableData withObserver(Player observer) {
		return setObserver(observer);
	}

	/**
	 * @param observer
	 *            the observer to set
	 */
	public ParsableData withObserver(Optional<Player> observer) {
		return setObserver(observer);
	}

	/**
	 * @param observer
	 *            the observer to set
	 */
	public ParsableData setObserver(Player observer) {
		return setObserver(Optional.ofNullable(observer));
	}

	/**
	 * @param observer
	 *            the observer to set
	 */
	public ParsableData setObserver(Optional<Player> observer) {
		this.observer = observer;
		return this;
	}

	/**
	 * @return whether click and hover is supported
	 */
	public boolean isClickHover() {
		return clickHover;
	}

	/**
	 * @param clickHover
	 *            the clickHover to set
	 */
	public ParsableData withClickHover(boolean clickHover) {
		return setClickHover(clickHover);
	}

	/**
	 * @param clickHover
	 *            the clickHover to set
	 */
	public ParsableData setClickHover(boolean clickHover) {
		this.clickHover = clickHover;
		return this;
	}

	/**
	 * @return what page to use for pagination objects
	 */
	public Optional<Integer> getPage() {
		return page;
	}

	/**
	 * Set the page to use
	 */
	public ParsableData setPage(Optional<Integer> i) {
		return withPage(i);
	}

	/**
	 * Set the page to use
	 */
	public ParsableData withPage(Optional<Integer> i) {
		if (!i.isPresent()) {
			return this;
		}
		return withPage(i.get());
	}

	/**
	 * Set the page to use
	 */
	public ParsableData setPage(int i) {
		return withPage(i);
	}

	/**
	 * Set the page to use
	 */
	public ParsableData withPage(int i) {
		this.page = Utils.wrap(i, i >= 0);
		return this;
	}
}
