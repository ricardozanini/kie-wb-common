/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner;

import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.activities.ReusableSubprocessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.processes.SubProcessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;

public class ConverterFactory extends BaseConverterFactory<BPMNDiagramImpl, AdHocSubprocess, ReusableSubprocess> {

    public ConverterFactory(DefinitionsBuildingContext context,
                            PropertyWriterFactory propertyWriterFactory) {
        super(context,
              propertyWriterFactory);
    }

    @Override
    public ReusableSubprocessConverter reusableSubprocessConverter() {
        return new ReusableSubprocessConverter(propertyWriterFactory);
    }

    public SubProcessConverter subProcessConverter() {
        return new SubProcessConverter(context, propertyWriterFactory, this);
    }

    @Override
    protected FlowElementConverter createFlowElementConverter() {
        return new FlowElementConverter(this);
    }
}