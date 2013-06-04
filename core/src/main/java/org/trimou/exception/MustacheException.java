/*
 * Copyright 2013 Martin Kouba
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
package org.trimou.exception;

/**
 *
 * @author Martin Kouba
 */
public class MustacheException extends RuntimeException {

	private static final long serialVersionUID = -83740749228227269L;

	private MustacheProblem code;

	public MustacheException(MustacheProblem code) {
		this.code = code;
	}

	public MustacheException(MustacheProblem code, String message, Object... params) {
		super(String.format(message, params));
		this.code = code;
	}

	public MustacheException(MustacheProblem code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public MustacheException(String message, Throwable cause) {
		super(message, cause);
	}

	public MustacheException(String message) {
		super(message);
	}

	public MustacheException(Throwable cause) {
		super(cause);
	}

	public MustacheProblem getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return String.format("%s: %s", getCode(), super.getMessage());
	}

}
