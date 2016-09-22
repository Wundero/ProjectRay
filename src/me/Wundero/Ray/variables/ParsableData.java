package me.Wundero.Ray.variables;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;

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

public class ParsableData {
	private Optional<Map<String, Object>> known = Optional.empty();
	private Optional<Player> sender = Optional.empty();
	private Optional<Player> recipient = Optional.empty();
	private Optional<Player> observer = Optional.empty();
	private boolean clickHover = false;

	public ParsableData() {
	}

	/**
	 * @return the known
	 */
	public Optional<Map<String, Object>> getKnown() {
		return known;
	}

	public ParsableData setKnown(Map<String, Object> known) {
		return setKnown(Optional.ofNullable(known));
	}

	public ParsableData withKnown(Map<String, Object> known) {
		return setKnown(known);
	}

	public ParsableData withKnown(Optional<Map<String, Object>> known) {
		return setKnown(known);
	}

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

	public ParsableData setSender(Player sender) {
		return setSender(Optional.ofNullable(sender));
	}

	public ParsableData withSender(Player sender) {
		return setSender(sender);
	}

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

	public ParsableData setRecipient(Player recipient) {
		return setRecipient(Optional.ofNullable(recipient));
	}

	public ParsableData withRecipient(Player recipient) {
		return setRecipient(recipient);
	}

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

	public ParsableData withObserver(Player observer) {
		return setObserver(observer);
	}

	public ParsableData withObserver(Optional<Player> observer) {
		return setObserver(observer);
	}

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
	 * @return the clickHover
	 */
	public boolean isClickHover() {
		return clickHover;
	}

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
}
