/*
 * Licensed to Laurent Broudoux (the "Author") under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Author licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.github.microcks.util.script;


import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This is a test case for class ScriptEngineBinder class.
 * @author laurent
 */
public class ScriptEngineBinderTest {

   @Test
   public void testRequestContentIsBound() {
      String script = """
            return mockRequest.requestContent;
            """;

      ScriptEngineManager sem = new ScriptEngineManager();
      String body = "content";

      try {
         // Evaluating request with script coming from operation dispatcher rules.
         ScriptEngine se = sem.getEngineByExtension("groovy");
         ScriptEngineBinder.bindEnvironment(se, body, null);
         String result = (String) se.eval(script);

         assertEquals(body, result);
      } catch (Exception e) {
         fail("Exception should no be thrown");
      }
   }

   @Test
   public void testRequestContentHeadersAreBound() {
      String script = """
            def headers = mockRequest.getRequestHeaders()
            log.info("headers: " + headers)
            return headers.get("foo", "null");
            """;

      ScriptEngineManager sem = new ScriptEngineManager();
      MockHttpServletRequest request = new MockHttpServletRequest();
      request.addHeader("foo", "bar");
      String body = "content";

      try {
         // Evaluating request with script coming from operation dispatcher rules.
         ScriptEngine se = sem.getEngineByExtension("groovy");
         ScriptEngineBinder.bindEnvironment(se, body, null, request);
         String result = (String) se.eval(script);

         assertEquals("bar", result);
      } catch (Exception e) {
         fail("Exception should no be thrown");
      }
   }

   @Test
   public void testRequestContextIsModified() {
      String script = """
            requestContext.foo = "bar";
            return mockRequest.requestContent;
            """;

      ScriptEngineManager sem = new ScriptEngineManager();
      Map<String, Object> context = new HashMap<>();
      String body = "content";

      try {
         // Evaluating request with script coming from operation dispatcher rules.
         ScriptEngine se = sem.getEngineByExtension("groovy");
         ScriptEngineBinder.bindEnvironment(se, body, context);
         String result = (String) se.eval(script);

         assertEquals(body, result);
         assertTrue(context.containsKey("foo"));
         assertEquals("bar", context.get("foo"));
      } catch (Exception e) {
         fail("Exception should no be thrown");
      }
   }
}
