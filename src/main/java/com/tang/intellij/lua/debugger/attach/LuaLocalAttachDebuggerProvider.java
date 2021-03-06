/*
 * Copyright (c) 2017. tangzx(love.tangzx@qq.com)
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

package com.tang.intellij.lua.debugger.attach;

import com.intellij.execution.process.ProcessInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.xdebugger.attach.XLocalAttachDebugger;
import com.intellij.xdebugger.attach.XLocalAttachDebuggerProvider;
import com.intellij.xdebugger.attach.XLocalAttachGroup;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Created by tangzx on 2017/2/28.
 */
public class LuaLocalAttachDebuggerProvider implements XLocalAttachDebuggerProvider {

    @NotNull
    @Override
    public XLocalAttachGroup getAttachGroup() {
        return LuaLocalAttachGroup.INSTANCE;
    }

    @NotNull
    @Override
    public List<XLocalAttachDebugger> getAvailableDebuggers(@NotNull Project project, @NotNull ProcessInfo processInfo, @NotNull UserDataHolder userDataHolder) {
        if (processInfo.getExecutableName().endsWith(".exe")) {
            ArrayList<XLocalAttachDebugger> list = new ArrayList<>();
            list.add(new LuaLocalAttachDebugger(processInfo));
            return list;
        }

        return Collections.emptyList();
    }
}
