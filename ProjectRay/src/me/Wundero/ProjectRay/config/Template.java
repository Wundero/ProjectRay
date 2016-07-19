package me.Wundero.ProjectRay.config;
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

import java.util.List;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import me.Wundero.ProjectRay.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class Template {

	private ConfigurationNode node = null;

	private Template(ConfigurationNode node) {
		this.node = node;
	}

	public static Template.Builder builder(ConfigurationNode node) {
		return new Builder(node);
	}

	public static class Builder {

		private Template template;
		ConfigurationNode groupNode = null;

		Builder(ConfigurationNode node) {
			template = new Template(node);
			groupNode = template.node.getNode("worlds", "all", "groups");
		}

		public Template build() {
			return template;
		}

		public GroupBuilder withGroup(String name) {
			return new GroupBuilder(groupNode.getNode(name), name, this);
		}

		public static class GroupBuilder {

			private ConfigurationNode node;
			private String name;
			private int priority = 0;
			private String permission = "";
			private List<String> parents = Lists.newArrayList();
			private Builder parent;

			GroupBuilder(ConfigurationNode node, String name, Builder parent) {
				this.parent = parent;
				this.node = node;
				this.name = name;
			}

			public Builder build() {
				if (permission.isEmpty()) {
					permission = "ray." + name;
				}
				node.getNode("priority").setValue(priority);
				node.getNode("permission").setValue(permission);
				if (!parents.isEmpty()) {
					node.getNode("parents").setValue(parents);
				}
				return parent;
			}

			public GroupBuilder withPriority(int priority) {
				this.priority = priority;
				return this;
			}

			public GroupBuilder withPermission(String permission) {
				this.permission = permission;
				return this;
			}

			public GroupBuilder withParent(String... parent) {
				for (String s : parent) {
					parents.add(s);
				}
				return this;
			}

			public FormatBuilder withFormat(String name) {
				return new FormatBuilder(node.getNode("formats", name), name, this);
			}

			public static class FormatBuilder {

				private ConfigurationNode node;
				@SuppressWarnings("unused")
				private String name;
				private GroupBuilder parent;
				private TextTemplate template;

				FormatBuilder(ConfigurationNode node, String name, GroupBuilder parent) {
					this.node = node;
					this.name = name;
					this.parent = parent;
					template = TextTemplate.of();
				}

				public GroupBuilder build() {
					try {
						node.getNode("format").setValue(TypeToken.of(TextTemplate.class), template);
					} catch (ObjectMappingException e) {
						Utils.printError(e);
					}
					return parent;
				}

				public FormatBuilder withArg(String key) {
					template = template.concat(TextTemplate.of(TextTemplate.arg(key)));
					return this;
				}

				public FormatBuilder withText(Text... texts) {
					for (Text t : texts) {
						template = template.concat(TextTemplate.of(t));
					}
					return this;
				}
			}
		}
	}
}
