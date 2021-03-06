/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.uberfire.client.mvp.UberElement;

public class MultipleInstanceVariableEditorPresenter extends FieldEditorPresenter<String> {

    public interface View extends UberElement<MultipleInstanceVariableEditorPresenter> {

        void setVariableName(String variableName);

        String getVariableName();

        void setReadOnly(boolean readOnly);
    }

    private final View view;

    @Inject
    public MultipleInstanceVariableEditorPresenter(View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    protected IsElement getView() {
        return view;
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        view.setVariableName(value);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    public void onVariableNameChange() {
        String oldValue = value;
        value = view.getVariableName();
        notifyChange(oldValue, value);
    }
}
