package me.Wundero.ProjectRay.config;

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

import me.Wundero.ProjectRay.config.InternalHoverAction.ShowEntity.Ref;

/**
 * Represents a {@link TextAction} that responds to hovers.
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
		return TextActions.showText(((ShowTemplate) this).template.apply().build());
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