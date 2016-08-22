package me.Wundero.ProjectRay.framework.format;

import java.util.regex.Pattern;

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
public enum FormatType {

	/* God I hate eclipse formatting sometimes */

	CHAT("chat", new String[] { "c" }), // --------------------------------------------
	MESSAGE_SEND("send_message", new String[] { "sm", "smsg", "sendmsg" }), // --------
	MESSAGE_RECEIVE("receive_message", new String[] { "rm", "rmsg", "receivemsg" }), //
	DEATH("death", new String[] { "d" }), // ------------------------------------------
	JOIN("join", new String[] { "j" }), // --------------------------------------------
	LEAVE("leave", new String[] { "l" }), // ------------------------------------------
	CUSTOM("custom"), // --------------------------------------------------------------
	WELCOME("welcome", new String[] { "w" }), // --------------------------------------
	MOTD("motd"), // ------------------------------------------------------------------
	TABLIST_ENTRY("tablist", new String[] { "list", "t", "tab" }, true), // -----------
	DEFAULT("default"), // ------------------------------------------------------------
	ACHIEVEMENT("achievement", new String[] { "a", "ach" }), // -----------------------
	KICK("kick", new String[] { "k" }), // --------------------------------------------
	TABLIST_HEADER("header", new String[] { "h" }, true), // --------------------------
	TABLIST_FOOTER("footer", new String[] { "f" }, true); // --------------------------

	// format types:
	/*
	 * chat------------------------------------------------------------------Y
	 * message_send----------------------------------------------------------Y
	 * message_receive-------------------------------------------------------Y
	 * join------------------------------------------------------------------Y
	 * leave-----------------------------------------------------------------Y
	 * achievement-----------------------------------------------------------Y
	 * kick------------------------------------------------------------------Y
	 * death----------------------------------------------------------------/N
	 * custom----------------------------------------------------------------Y
	 * welcome---------------------------------------------------------------Y
	 * modt------------------------------------------------------------------Y
	 * tablist_entry--------------------------------------------------------AY
	 * default---------------------------------------------------------------/
	 * tablist_footer-------------------------------------------------------AY
	 * tablist_header-------------------------------------------------------AY
	 * 
	 * Format types to add maybe:
	 * afk--------------------------------------------------------------------
	 * helpop-----------------------------------------------------------------
	 * mail-------------------------------------------------------------------
	 * me (action)------------------------------------------------------------
	 * broadcast--------------------------------------------------------------
	 * announcement-----------------------------------------------------------
	 * animatable formats: ---------------------------------------------------
	 * -- bossbar ------------------------------------------------------------
	 * -- scoreboard ---------------------------------------------------------
	 * -- title --------------------------------------------------------------
	 * -- actionbar ----------------------------------------------------------
	 * 
	 */

	/*
	 * Since this seems to be where i dump ideas, todo:-----------------------
	 * range obfuscation (chat messages out of range of channels but still in
	 * world get slowly degenerated) should be done but not tested. ----------
	 * nicknames--------------------------------------------------------------
	 * permission setup ------------------------------------------------------
	 * prefixes/suffixes/displayname modification-----------------------------
	 * different types of chat (whisper, talk, yell)--------------------------
	 * message spying (includes being on all channels and stuff)--------------
	 * mute command-----------------------------------------------------------
	 * helpop command---------------------------------------------------------
	 * afk command------------------------------------------------------------
	 * broadcast command------------------------------------------------------
	 * me command-------------------------------------------------------------
	 * mail command-----------------------------------------------------------
	 * death messages + translations (I HATE THIS ONE :()---------------------
	 * format non-ray messages (events) - sort of done with custom formats but
	 * needs to be properly implemented for messages that are sent by other
	 * plugins. I would like to figure out if I can determine if the message is
	 * plugin sent or player sent, however that seems impractical as plugins
	 * don't consistently set themselves as senders.--------------------------
	 */

	// TODO add more formats as seen fit

	private static Pattern namepat = Pattern.compile("[a-zA-Z]+[_\\-\\. ]*[0-9]+", Pattern.CASE_INSENSITIVE);
	private static Pattern altpat = Pattern.compile("[_\\-\\. ]*[0-9]+", Pattern.CASE_INSENSITIVE);

	private String[] aliases;
	private String name;
	private boolean animated;

	FormatType(String name) {
		this.setName(name);
		this.setAliases(new String[] {});
	}

	FormatType(String name, String[] aliases) {
		this.setName(name);
		this.setAliases(aliases);
	}

	FormatType(String name, String[] aliases, boolean animatable) {
		this(name, aliases);
		this.setAnimated(animatable);
	}

	public String[] getAliases() {
		return aliases;
	}

	public void setAliases(String[] aliases) {
		this.aliases = aliases;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public static FormatType fromString(String s) {
		s = s.trim().toUpperCase().replace(" ", "_");
		if (namepat.matcher(s).matches()) {
			s = altpat.matcher(s).replaceAll("");
		}
		for (FormatType type : values()) {
			if (type.name.equalsIgnoreCase(s) || type.getName().equalsIgnoreCase(s)) {
				return type;
			}
		}
		for (FormatType type : values()) {
			for (String st : type.aliases) {
				if (st.equalsIgnoreCase(s)) {
					return type;
				}
			}
		}
		return DEFAULT;
	}

	/**
	 * @return the animated
	 */
	public boolean isAnimated() {
		return animated;
	}

	/**
	 * @param animated
	 *            the animated to set
	 */
	public void setAnimated(boolean animated) {
		this.animated = animated;
	}
}
