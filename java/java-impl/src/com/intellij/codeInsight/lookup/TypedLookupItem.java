/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.codeInsight.lookup;

import com.intellij.codeInsight.lookup.impl.JavaElementLookupRenderer;
import com.intellij.openapi.util.ClassConditionKey;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * A lookup element representing a type. Must extend {@link LookupElement}
 */
public interface TypedLookupItem {
  ClassConditionKey<TypedLookupItem> CLASS_CONDITION_KEY = ClassConditionKey.create(TypedLookupItem.class);

  /**
   * @return the type represented by this item
   */
  @Nullable 
  PsiType getType();

  /**
   * @return icon associated with item type
   */
  default @Nullable Icon getIcon() {
    return DefaultLookupItemRenderer.getRawIcon((LookupElement)this); 
  }

  /**
   * @return whether the item should be rendered as stroke out
   */
  default boolean isToStrikeout() {
    return JavaElementLookupRenderer.isToStrikeout((LookupElement)this);
  }
}
