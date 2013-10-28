/*
 * Copyright 2000-2013 JetBrains s.r.o.
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
package org.jetbrains.plugins.groovy.refactoring.introduce.variable;

import com.intellij.codeInsight.template.TemplateBuilderImpl;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.ui.NonFocusableCheckBox;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.api.auxiliary.modifiers.GrModifier;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrVariable;
import org.jetbrains.plugins.groovy.lang.psi.expectedTypes.SupertypeConstraint;
import org.jetbrains.plugins.groovy.lang.psi.expectedTypes.TypeConstraint;
import org.jetbrains.plugins.groovy.lang.psi.util.PsiUtil;
import org.jetbrains.plugins.groovy.refactoring.GroovyNameSuggestionUtil;
import org.jetbrains.plugins.groovy.refactoring.introduce.GrFinalListener;
import org.jetbrains.plugins.groovy.refactoring.introduce.GrInplaceIntroducer;
import org.jetbrains.plugins.groovy.refactoring.introduce.GrIntroduceContext;
import org.jetbrains.plugins.groovy.settings.GroovyApplicationSettings;
import org.jetbrains.plugins.groovy.template.expressions.ChooseTypeExpression;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author Max Medvedev
 */
public class GrInplaceVariableIntroducer extends GrInplaceIntroducer {
  private JCheckBox myCanBeFinalCb;

  public GrInplaceVariableIntroducer(GrVariable elementToRename,
                                     Editor editor,
                                     Project project,
                                     String title,
                                     List<RangeMarker> occurrences,
                                     @Nullable PsiElement elementToIntroduce) {
    super(elementToRename, editor, project, title, occurrences, elementToIntroduce);
  }

  @Override
  public LinkedHashSet<String> suggestNames(GrIntroduceContext context) {
    return ContainerUtil.newLinkedHashSet(GroovyNameSuggestionUtil.suggestVariableNames(getVariable().getInitializerGroovy(), new GroovyVariableValidator(context)));
  }

  @Nullable
  @Override
  protected JComponent getComponent() {
    myCanBeFinalCb = new NonFocusableCheckBox("Declare final");
    myCanBeFinalCb.setSelected(false);
    myCanBeFinalCb.setMnemonic('f');
    final GrFinalListener finalListener = new GrFinalListener(myEditor);
    myCanBeFinalCb.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        new WriteCommandAction(myProject, getCommandName(), getCommandName()) {
          @Override
          protected void run(Result result) throws Throwable {
            PsiDocumentManager.getInstance(myProject).commitDocument(myEditor.getDocument());
            final GrVariable variable = getVariable();
            if (variable != null) {
              finalListener.perform(myCanBeFinalCb.isSelected(), variable);
            }
          }
        }.execute();
      }
    });
    final JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(null);

    if (myCanBeFinalCb != null) {
      panel.add(myCanBeFinalCb, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }

    panel.add(Box.createVerticalBox(), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0));

    return panel;

  }

  @Override
  public void finish(boolean success) {
    super.finish(success);

    if (success) {
      GroovyApplicationSettings.getInstance().INTRODUCE_LOCAL_SELECT_DEF = getVariable().getDeclaredType() == null;
    }
  }

  @Override
  protected void addAdditionalVariables(TemplateBuilderImpl builder) {
    GrVariable variable = getVariable();
    assert variable != null;
    TypeConstraint[] constraints = {SupertypeConstraint.create(variable.getInitializerGroovy().getType())};
    ChooseTypeExpression typeExpression = new ChooseTypeExpression(constraints, variable.getManager(), variable.getResolveScope(), true, GroovyApplicationSettings.getInstance().INTRODUCE_LOCAL_SELECT_DEF);
    PsiElement element = variable.getTypeElementGroovy() != null ? variable.getTypeElementGroovy()
                                                                 : PsiUtil.findModifierInList(variable.getModifierList(), GrModifier.DEF);
    builder.replaceElement(element, "Variable_type", typeExpression, true, true);
  }
}
