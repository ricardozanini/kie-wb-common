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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.Map;

import org.junit.Test;
import org.kie.dmn.model.v1_2.TImport;

import static org.junit.Assert.assertEquals;

public class ImportConverterTest {

    @Test
    public void testWbFromDMN() {

        final org.kie.dmn.model.api.Import dmn = new TImport();
        final String key = "drools";
        final String value = "http://www.drools.org/kie/dmn/1.1";
        dmn.getNsContext().put(key, value);

        final org.kie.workbench.common.dmn.api.definition.v1_1.Import anImport = ImportConverter.wbFromDMN(dmn);
        final Map<String, String> nsContext = anImport.getNsContext();

        assertEquals(1, nsContext.size());
        assertEquals(value, nsContext.get(key));
    }

    @Test
    public void testDmnFromWb() {

        final org.kie.workbench.common.dmn.api.definition.v1_1.Import wb = new org.kie.workbench.common.dmn.api.definition.v1_1.Import();
        final String key = "drools";
        final String value = "http://www.drools.org/kie/dmn/1.1";
        wb.getNsContext().put(key, value);

        final org.kie.dmn.model.api.Import anImport = ImportConverter.dmnFromWb(wb);
        final Map<String, String> nsContext = anImport.getNsContext();

        assertEquals(1, nsContext.size());
        assertEquals(value, nsContext.get(key));
    }
}
