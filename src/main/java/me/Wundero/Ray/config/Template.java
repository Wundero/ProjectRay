package me.Wundero.Ray.config;
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
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.TextTemplate.Arg;

import com.google.common.reflect.TypeToken;

import me.Wundero.Ray.framework.format.context.FormatContext;
import me.Wundero.Ray.framework.format.location.FormatLocation;
import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

/**
 * Configuration template. Applies to the config as it is built.
 */
public class Template {

	private ConfigurationNode node = null;

	private Template(ConfigurationNode node) {
		this.node = node;
	}

	/**
	 * Start building a new template.
	 */
	public static Template.Builder builder(ConfigurationNode node) {
		return new Builder(node);
	}

	// builder automatically applies to config instead of creating
	// groups/formats without important info
	public static class Builder {

		private Template template;
		ConfigurationNode groupNode = null;

		Builder(ConfigurationNode node) {
			template = new Template(node);
			groupNode = template.node.getNode("worlds", "all", "groups");
		}

		/**
		 * Build the template.
		 */
		public Template build() {
			return template;
		}

		/**
		 * Add a new group
		 */
		public GroupBuilder withGroup(String name) {
			return new GroupBuilder(groupNode.getNode(name), name, this);
		}

		public static class GroupBuilder {

			private ConfigurationNode node;
			@SuppressWarnings("unused")
			private String name;
			private int priority = 0;
			private String permission = "";
			private List<String> parents = Utils.al();
			private Builder parent;

			GroupBuilder(ConfigurationNode node, String name, Builder parent) {
				this.parent = parent;
				this.node = node;
				this.name = name;
			}

			/**
			 * Build the group
			 */
			public Builder build() {
				node.getNode("priority").setValue(priority);
				if (!permission.isEmpty()) {
					node.getNode("permission").setValue(permission);
				}
				if (!parents.isEmpty()) {
					node.getNode("parents").setValue(parents);
				}
				return parent;
			}

			/**
			 * Set the priority of the group
			 */
			public GroupBuilder withPriority(int priority) {
				this.priority = priority;
				return this;
			}

			/**
			 * Set the permission of the group
			 */
			public GroupBuilder withPermission(String permission) {
				if (permission == null) {
					return this;
				}
				this.permission = permission;
				return this;
			}

			/**
			 * Add a parent group
			 */
			public GroupBuilder withParent(String... parent) {
				for (String s : parent) {
					parents.add(s);
				}
				return this;
			}

			/**
			 * Add a default format
			 */
			public GroupBuilder withFormat(DefaultFormat f) {
				return f.applyTo(this);
			}

			/**
			 * Create a new format
			 */
			public FormatBuilder withFormat(String name) {
				return new FormatBuilder(node.getNode("formats", name), name, this);
			}

			public static class FormatBuilder {

				private ConfigurationNode node;
				@SuppressWarnings("unused")
				private String name;
				private Optional<FormatContext> type = Optional.empty();
				private Optional<FormatLocation> loc = Optional.empty();
				private Optional<Consumer<ConfigurationNode>> locdataset = Optional.empty();
				private GroupBuilder parent;
				private TextTemplate template;
				private Map<Arg, InternalClickAction<?>> clicks = Utils.hm();
				private Map<Arg, InternalHoverAction<?>> hovers = Utils.hm();

				FormatBuilder(ConfigurationNode node, String name, GroupBuilder parent) {
					this.node = node;
					this.name = name;
					this.parent = parent;
					template = TextTemplate.of();
				}

				/**
				 * Build the format
				 */
				public GroupBuilder build() {
					try {
						node.getNode("format").setValue(TypeToken.of(TextTemplate.class), template);
						ConfigurationNode args = node.getNode("format_args", "arguments");
						for (Arg a : clicks.keySet()) {
							args.getNode(a.getName(), "click").setValue(TypeToken.of(InternalClickAction.class),
									clicks.get(a));
						}
						for (Arg a : hovers.keySet()) {
							args.getNode(a.getName(), "hover").setValue(TypeToken.of(InternalHoverAction.class),
									hovers.get(a));
						}
						if (type.isPresent()) {
							node.getNode("context").setValue(type.get().getName());
						}
						if (loc.isPresent()) {
							node.getNode("location").setValue(loc.get().getName());
							if (locdataset.isPresent()) {
								locdataset.get().accept(node.getNode("location-data"));
							}
						}
					} catch (ObjectMappingException e) {
						Utils.printError(e);
					}
					return parent;
				}

				/**
				 * Add a context
				 */
				public FormatBuilder withType(FormatContext type) {
					this.type = Optional.ofNullable(type);
					return this;
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(String key) {
					return withArg(key, null, null);
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(String key, InternalHoverAction<?> hover) {
					return withArg(key, null, hover);
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(String key, InternalClickAction<?> click) {
					return withArg(key, click, null);
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(String key, boolean optional) {
					return withArg(key, null, null);
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(String key, InternalHoverAction<?> hover, boolean optional) {
					return withArg(key, null, hover);
				}

				/**
				 * Add an argument
				 */

				public FormatBuilder withArg(String key, InternalClickAction<?> click, boolean optional) {
					return withArg(key, click, null);
				}

				/**
				 * Add an argument
				 */

				public FormatBuilder withArg(String key, InternalClickAction<?> click, InternalHoverAction<?> hover) {
					return withArg(key, click, hover, false);
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(String key, InternalClickAction<?> click, InternalHoverAction<?> hover,
						boolean optional) {
					return withArg(TextTemplate.arg(key), click, hover, optional);
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(DefaultArg arg) {
					return withArg(arg.getBuilder(), arg.getClick(), arg.getHover(), arg.isOptional());
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(Arg.Builder builder, InternalHoverAction<?> hover) {
					return withArg(builder, null, hover);
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(Arg.Builder builder, InternalClickAction<?> click) {
					return withArg(builder, click, null);
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(Arg.Builder builder) {
					return withArg(builder, null, null);
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(Arg.Builder builder, InternalHoverAction<?> hover, boolean optional) {
					return withArg(builder, null, hover, optional);
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(Arg.Builder builder, InternalClickAction<?> click, boolean optional) {
					return withArg(builder, click, null, optional);
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(Arg.Builder builder, boolean optional) {
					return withArg(builder, null, null, optional);
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(Arg.Builder builder, InternalClickAction<?> click,
						InternalHoverAction<?> hover) {
					return withArg(builder, click, hover, false);
				}

				/**
				 * Add an argument
				 */
				public FormatBuilder withArg(Arg.Builder builder, InternalClickAction<?> click,
						InternalHoverAction<?> hover, boolean optional) {
					builder.optional(optional);
					Arg built = builder.build();
					if (click != null) {
						clicks.put(built, click);
					}
					if (hover != null) {
						hovers.put(built, hover);
					}
					template = template.concat(TextTemplate.of(builder.build()));
					return this;
				}

				/**
				 * Add some text
				 */
				public FormatBuilder withText(String... texts) {
					for (String s : texts) {
						template = template.concat(TextTemplate.of(s));
					}
					return this;
				}

				/**
				 * Add some text
				 */
				public FormatBuilder withText(Text... texts) {
					for (Text t : texts) {
						template = template.concat(TextTemplate.of(t));
					}
					return this;
				}

				/**
				 * Set location data for the location
				 */
				public FormatBuilder withLocData(Consumer<ConfigurationNode> task) {
					this.locdataset = Utils.wrap(task);
					return this;
				}

				/**
				 * Set the location for this format
				 */
				public FormatBuilder withLoc(FormatLocation loc) {
					this.loc = Utils.wrap(loc);
					return this;
				}

				/**
				 * Set the node for this format
				 */
				public void setNode(ConfigurationNode node) {
					this.node = node;
				}

				/**
				 * Get the node for this format
				 */
				public ConfigurationNode getNode() {
					return node;
				}
			}
		}
	}
}
