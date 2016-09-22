package me.Wundero.Ray.config;

/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.util.Identifiable;

import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;

import me.Wundero.Ray.config.InternalHoverAction.ShowEntity.Ref;
import me.Wundero.Ray.utils.TextUtils;
import me.Wundero.Ray.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

/**
 * Represents a {@link TextAction} that responds to hovers. This version has
 * been modified to add support for text templates.
 *
 * @param <R>
 *            The type of the result of the action
 */
public abstract class InternalHoverAction<R> extends TextAction<R> {

	/**
	 * Constructs a new {@link HoverAction} with the given result.
	 *
	 * @param result
	 *            The result of the hover action
	 */
	InternalHoverAction(R result) {
		super(result);
	}

	public static <T> ActionBuilder<T> builder() {
		return new ActionBuilder<T>();
	}

	public static class ActionBuilder<R> {
		@SuppressWarnings("unchecked")
		public InternalHoverAction<R> build(Class<?> type) {
			switch (type.getSimpleName()) {
			case "ShowTemplate":
				return (InternalHoverAction<R>) TextUtils.showTemplate((TextTemplate) result);
			case "ShowText":
				return (InternalHoverAction<R>) TextUtils.showText((Text) result);
			case "ShowItem":
				return (InternalHoverAction<R>) TextUtils.showItem((ItemStack) result);
			case "ShowEntity":
				return (InternalHoverAction<R>) TextUtils.showEntity((Ref) result);
			case "ShowAchievement":
				return (InternalHoverAction<R>) TextUtils.showAchievement((Achievement) result);
			default:
				return (InternalHoverAction<R>) TextUtils.showText(Text.of(((Object) result).toString()));
			}

		}

		private R result = null;

		public ActionBuilder<R> withResult(R result) {
			this.result = result;
			return this;
		}
	}

	@SuppressWarnings("rawtypes")
	public static TypeSerializer<InternalHoverAction> serializer() {
		return new TypeSerializer<InternalHoverAction>() {

			@Override
			public InternalHoverAction deserialize(TypeToken<?> arg0, ConfigurationNode arg1)
					throws ObjectMappingException {
				final String icc = "";
				final ConfigurationNode result = arg1.getNode("result");
				switch (arg1.getNode("type").getString()) {
				case icc + "ShowTemplate":
					return TextUtils.showTemplate(result.getValue(TypeToken.of(TextTemplate.class)));
				case icc + "ShowText":
					return TextUtils.showText(result.getValue(TypeToken.of(Text.class)));
				case icc + "ShowItem":
					return TextUtils.showItem(result.getValue(TypeToken.of(ItemStack.class)));
				case icc + "ShowAchievement":
					return TextUtils.showAchievement(result.getValue(TypeToken.of(Achievement.class)));
				case icc + "ShowEntity":
					UUID uuid = result.getNode("uuid").getValue(TypeToken.of(UUID.class));
					String name = result.getNode("name").getString();
					EntityType t = result.getNode("entity").getValue(TypeToken.of(EntityType.class));
					if (t != null) {
						return TextUtils.showEntity(uuid, name, t);
					}
					return TextUtils.showEntity(uuid, name);
				}
				return null;
			}

			@Override
			public void serialize(TypeToken<?> arg0, InternalHoverAction arg1, ConfigurationNode arg2)
					throws ObjectMappingException {
				if (arg1 instanceof ShowTemplate) {
					arg2.getNode("result").setValue(TypeToken.of(TextTemplate.class),
							(TextTemplate) ((ShowTemplate) arg1).getTemplate());
				} else if (arg1 instanceof ShowText) {
					arg2.getNode("result").setValue(TypeToken.of(Text.class), (Text) arg1.getResult());
				} else if (arg1 instanceof ShowItem) {
					arg2.getNode("result").setValue(TypeToken.of(ItemStack.class), (ItemStack) arg1.getResult());
				} else if (arg1 instanceof ShowAchievement) {
					arg2.getNode("result").setValue(TypeToken.of(Achievement.class), (Achievement) arg1.getResult());
				} else if (arg1 instanceof ShowEntity) {
					Ref r = ((ShowEntity) arg1).getResult();
					ConfigurationNode n = arg2.getNode("result");
					n.getNode("uuid").setValue(TypeToken.of(UUID.class), r.uuid);
					n.getNode("name").setValue(r.name);
					if (r.getType().isPresent()) {
						n.getNode("entity").setValue(TypeToken.of(EntityType.class), r.getType().get());
					}
				}
				ConfigurationNode typ = arg2.getNode("type");
				typ.setValue(arg1.getClass().getSimpleName());
			}

		};
	}

	private HoverAction<?> toHover() {
		if (this instanceof ShowText) {
			return TextActions.showText((Text) this.getResult());
		}
		if (this instanceof ShowItem) {
			return TextActions.showItem((ItemStack) this.getResult());
		}
		if (this instanceof ShowAchievement) {
			return TextActions.showAchievement((Achievement) this.getResult());
		}
		if (this instanceof ShowEntity) {
			return TextActions.showEntity(
					new HoverAction.ShowEntity.Ref(((Ref) this.getResult()).uuid, ((Ref) this.getResult()).name));
		}
		TextTemplate t = ((ShowTemplate) this).template;
		Map<String, Object> p = Utils.sm();
		for (String k : t.getArguments().keySet()) {
			p.put(k, t.getOpenArgString() + k + t.getCloseArgString());
		}
		return TextActions.showText(t.apply(p).build());
	}

	@Override
	public void applyTo(Text.Builder builder) {
		builder.onHover(this.toHover());
	}

	public static final class ShowTemplate extends InternalHoverAction<TextTemplate> {
		private TextTemplate template;

		public ShowTemplate(TextTemplate template) {
			super(template);
			this.template = template;
		}

		public TextTemplate getTemplate() {
			return template;
		}

		public void apply(Map<String, ?> args) {
			template = TextTemplate.of(template.apply(args).build());
		}
	}

	/**
	 * Shows some text.
	 */
	public static final class ShowText extends InternalHoverAction<Text> {

		/**
		 * Constructs a new {@link ShowText} instance that will show text when
		 * it is hovered.
		 *
		 * @param text
		 *            The message to show
		 */
		public ShowText(Text text) {
			super(text);
		}
	}

	/**
	 * Shows information about an item.
	 */
	public static final class ShowItem extends InternalHoverAction<ItemStack> {

		/**
		 * Constructs a new {@link ShowItem} instance that will show information
		 * about an item when it is hovered.
		 *
		 * @param item
		 *            The item to display
		 */
		public ShowItem(ItemStack item) {
			super(item);
		}

	}

	/**
	 * Shows information about an achievement.
	 */
	public static final class ShowAchievement extends InternalHoverAction<Achievement> {

		/**
		 * Constructs a new {@link ShowAchievement} instance that will show
		 * information about an achievement when it is hovered.
		 *
		 * @param achievement
		 *            The achievement to display
		 */
		public ShowAchievement(Achievement achievement) {
			super(achievement);
		}

	}

	/**
	 * Shows information about an entity.
	 */
	public static final class ShowEntity extends InternalHoverAction<ShowEntity.Ref> {

		/**
		 * Constructs a new {@link ShowEntity} that will show information about
		 * an entity when it is hovered.
		 *
		 * @param ref
		 *            The reference to the entity to display
		 */
		public ShowEntity(Ref ref) {
			super(ref);
		}

		/**
		 * Represents a reference to an entity, used in the underlying JSON of
		 * the show entity action.
		 */
		public static final class Ref implements Identifiable {

			private final UUID uuid;
			private final String name;
			private final Optional<EntityType> type;

			/**
			 * Constructs a Ref to an entity.
			 *
			 * @param uuid
			 *            The UUID of the entity
			 * @param name
			 *            The name of the entity
			 * @param type
			 *            The type of the entity
			 */
			public Ref(UUID uuid, String name, @Nullable EntityType type) {
				this(uuid, name, Optional.ofNullable(type));
			}

			/**
			 * Constructs a Ref to an entity.
			 *
			 * @param uuid
			 *            The UUID of the entity
			 * @param name
			 *            The name of the entity
			 */
			public Ref(UUID uuid, String name) {
				this(uuid, name, Optional.<EntityType> empty());
			}

			/**
			 * Constructs a Ref, given an {@link Entity}.
			 *
			 * @param entity
			 *            The entity
			 * @param name
			 *            The name of the entity
			 */
			public Ref(Entity entity, String name) {
				this(entity.getUniqueId(), name, entity.getType());
			}

			/**
			 * Constructs a Ref directly.
			 *
			 * @param uuid
			 *            The UUID
			 * @param name
			 *            The name
			 * @param type
			 *            The type
			 */
			protected Ref(UUID uuid, String name, Optional<EntityType> type) {
				this.uuid = uuid;
				this.name = name;
				this.type = type;
			}

			/**
			 * Retrieves the UUID that this {@link Ref} refers to.
			 *
			 * @return The UUID
			 */
			@Override
			public UUID getUniqueId() {
				return this.uuid;
			}

			/**
			 * Retrieves the name that this {@link Ref} refers to.
			 *
			 * @return The name
			 */
			public String getName() {
				return this.name;
			}

			/**
			 * Retrieves the type that this {@link Ref} refers to, if it exists.
			 *
			 * @return The type, or {@link Optional#empty()}
			 */
			public Optional<EntityType> getType() {
				return this.type;
			}

			@Override
			public boolean equals(Object obj) {
				if (super.equals(obj)) {
					return true;
				}

				if (!(obj instanceof Ref)) {
					return false;
				}

				Ref that = (Ref) obj;
				return this.uuid.equals(that.uuid) && this.name.equals(that.name) && this.type.equals(that.type);
			}

			@Override
			public int hashCode() {
				return Objects.hashCode(this.uuid, this.name, this.type);
			}

			@SuppressWarnings("deprecation")
			@Override
			public String toString() {
				return Objects.toStringHelper(this).add("uuid", this.uuid).add("name", this.name).add("type", this.type)
						.toString();
			}

		}

	}

}