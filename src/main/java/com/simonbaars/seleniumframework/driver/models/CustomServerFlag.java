/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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

package com.simonbaars.seleniumframework.driver.models;

import io.appium.java_client.service.local.flags.ServerArgument;

/**
 * Here is the list of Android specific server arguments.
 */
public enum CustomServerFlag implements ServerArgument {
    /**
     * Port to use on device to talk to Appium. Sample:
     * --bootstrap-port 4724
     */
    RELAXED_SECURITY("--relaxed-security");

    private final String arg;

    CustomServerFlag(String arg) {
        this.arg = arg;
    }

    @Override public String getArgument() {
        return arg;
    }
}
