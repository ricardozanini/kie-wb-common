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

package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.ArrayList;
import java.util.List;

import bpsim.NormalDistributionType;
import bpsim.PoissonDistributionType;
import bpsim.UniformDistributionType;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.MetaDataType;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class TestUtils {

    public static List<ExtensionAttributeValue> mockExtensionValues(EReference metadataFeatureName, String metadataElementName, String metadataElementValue) {
        List<MetaDataType> extensionElements = new ArrayList<>();
        MetaDataType metaDataType = mock(MetaDataType.class);
        when(metaDataType.getName()).thenReturn(metadataElementName);
        when(metaDataType.getMetaValue()).thenReturn(metadataElementValue);
        extensionElements.add(metaDataType);

        FeatureMap featureMap = mock(FeatureMap.class);
        when(featureMap.get(metadataFeatureName, true)).thenReturn(extensionElements);

        List<ExtensionAttributeValue> extensionValues = new ArrayList<>();
        ExtensionAttributeValue extensionAttributeValue = mock(ExtensionAttributeValue.class);
        when(extensionAttributeValue.getValue()).thenReturn(featureMap);
        extensionValues.add(extensionAttributeValue);
        return extensionValues;
    }

    public static Bounds mockBounds(float x, float y) {
        Bounds bounds = mock(Bounds.class);
        when(bounds.getX()).thenReturn(x);
        when(bounds.getY()).thenReturn(y);
        return bounds;
    }

    public static Bounds mockBounds(float x, float y, float width, float height) {
        Bounds bounds = mockBounds(x, y);
        when(bounds.getWidth()).thenReturn(width);
        when(bounds.getHeight()).thenReturn(height);
        return bounds;
    }

    public static void assertBounds(double x1, double y1, double x2, double y2, org.kie.workbench.common.stunner.core.graph.content.Bounds bounds) {
        assertEquals(x1, bounds.getUpperLeft().getX(), 0);
        assertEquals(y1, bounds.getUpperLeft().getY(), 0);
        assertEquals(x2, bounds.getLowerRight().getX(), 0);
        assertEquals(y2, bounds.getLowerRight().getY(), 0);
    }

    public static FormalExpression mockFormalExpression(String language, String body) {
        FormalExpression expression = mockFormalExpression(body);
        when(expression.getLanguage()).thenReturn(language);
        return expression;
    }

    public static FormalExpression mockFormalExpression(String body) {
        FormalExpression expression = mock(FormalExpression.class);
        when(expression.getBody()).thenReturn(body);
        return expression;
    }

    public static FeatureMap.Entry mockFeatureMapEntry(String name, Object value) {
        FeatureMap.Entry entry = mock(FeatureMap.Entry.class);
        EStructuralFeature feature = mock(EStructuralFeature.class);
        Mockito.when(feature.getName()).thenReturn(name);
        Mockito.when(entry.getEStructuralFeature()).thenReturn(feature);
        Mockito.when(entry.getValue()).thenReturn(value);
        return entry;
    }

    public static NormalDistributionType mockNormalDistributionType(double mean, double standardDeviation) {
        NormalDistributionType distributionType = mock(NormalDistributionType.class);
        when(distributionType.getMean()).thenReturn(mean);
        when(distributionType.getStandardDeviation()).thenReturn(standardDeviation);
        return distributionType;
    }

    public static UniformDistributionType mockUniformDistributionType(double min, double max) {
        UniformDistributionType distributionType = mock(UniformDistributionType.class);
        when(distributionType.getMin()).thenReturn(min);
        when(distributionType.getMax()).thenReturn(max);
        return distributionType;
    }

    public static PoissonDistributionType mockPoissonDistributionType(double mean) {
        PoissonDistributionType distributionType = mock(PoissonDistributionType.class);
        when(distributionType.getMean()).thenReturn(mean);
        return distributionType;
    }
}
