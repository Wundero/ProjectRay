package me.Wundero.ProjectRay.variables;

import java.util.logging.Level;

import javax.script.ScriptEngine;

import me.Wundero.ProjectRay.ProjectRay;
import me.Wundero.ProjectRay.framework.JSType;
import me.Wundero.ProjectRay.framework.JavaScriptHelper;
import me.Wundero.ProjectRay.framework.iface.Expressable;
import me.Wundero.ProjectRay.utils.Utils;

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
public class JSVariable extends Variable implements Expressable {

	private JSType type;
	private String exp, f, t;

	public JSVariable(String s, String exp, JSType type, String fr, String tr) {
		super(s);
		this.exp = exp;
		this.type = type;
		this.f = fr;
		this.t = tr;
	}

	@Override
	public String getExpression() {
		return exp;
	}

	@Override
	public String evaluate(OfflinePlayer p, Object... args) {
		String exp = getExpression();
		ScriptEngine engine = JavaScriptHelper.getEngine();
		engine.put("Player", p);
		// possible infinite loop; TODO prevent?
		exp = Parser.get().parse(p, null, exp);
		try {
			Object res = engine.eval(exp);
			if (res.getClass() != type.getClazz()
					&& !type.getClazz().isAssignableFrom(res.getClass())) {
				return "invalid";
			}
			if (type == JSType.BOOLEAN) {
				if ((Boolean) res) {
					return Parser.get().parse(p, null, trueResult());// loops
				} else {
					return Parser.get().parse(p, null, falseResult());// loops
				}
			}
			return Parser.get().parse(p, null, res.toString());// all da loops
		} catch (Exception e) {
			ProjectRay.get().log(
					"Error in javascript variable " + super.getName() + "!",
					Level.SEVERE);
			Utils.printError(e);
		}
		return "invalid";
	}

	@Override
	public String parse(OfflinePlayer player, String[] data) {
		return evaluate(player, (Object[]) data);
	}

	@Override
	public JSType getType() {
		return type;
	}

	@Override
	public String falseResult() {
		return f;
	}

	@Override
	public String trueResult() {
		return t;
	}

}
