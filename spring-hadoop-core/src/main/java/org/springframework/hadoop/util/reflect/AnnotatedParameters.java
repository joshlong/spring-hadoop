/*
 * Copyright 2006-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.hadoop.util.reflect;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.MethodParameter;

/**
 * @author Dave Syer
 * 
 */
public class AnnotatedParameters implements ParameterMatcher {

	private final Class<? extends Annotation> annotation;

	public AnnotatedParameters(Class<? extends Annotation> annotation) {
		this.annotation = annotation;
	}

	public List<MethodParameter> match(List<MethodParameter> parameters) {
		List<MethodParameter> output = new ArrayList<MethodParameter>();
		for (MethodParameter parameter : parameters) {
			if (parameter.getParameterAnnotation(annotation) != null) {
				output.add(parameter);
			}
		}
		return output;
	}

}
